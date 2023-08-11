package com.example.archirectureobjects;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import com.example.archirectureobjects.ml.Newmodel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.annotation.Nullable;

public class ModelResult extends AppCompatActivity {
    LinearLayout backButtonLinear, historyButtonLinear, modelResultLinearLayout, homeButtonLinear, resultFrameLayout, closeAdditionalCardView;
    TextView backButtonText, historyButtonText, objectTitle, objectTitleCardView, objectDescriptionCardView, moreInfo, geo;

    ImageView backButtonImage, homeButtonImage, historyButtonImage;
    MaterialCardView additionalInfoCardView;
    ImageView resultImage;
    FrameLayout content;
    int currentNightMode;
    int selectedLanguage;
    int imageSize = 200;
    int requestCode;
    int selectedTab;
    String title;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        selectedLanguage = getIntent().getExtras().getInt("selectedLanguageCode");
        if (selectedLanguage == 1) {
            setAppLocale("ru");
        } else {
            setAppLocale("en");
        }
        setTheme(R.style.Theme_ArchirectureObjects);
        setContentView(R.layout.activity_model_result);
        modelResultLinearLayout = findViewById(R.id.modelResultLinearLayout);
        closeAdditionalCardView = findViewById(R.id.closeAdditionalCardView);
        additionalInfoCardView = findViewById(R.id.additionalInfoCardView);
        historyButtonLinear = findViewById(R.id.historyButtonLinear);
        resultFrameLayout = findViewById(R.id.resultFrameLayout);
        backButtonLinear = findViewById(R.id.backButtonLinear);
        homeButtonLinear = findViewById(R.id.homeButtonLinear);
        content = findViewById(R.id.content);
        historyButtonImage = findViewById(R.id.historyButtonImage);
        backButtonImage = findViewById(R.id.backButtonImage);
        homeButtonImage = findViewById(R.id.homeButtonImage);
        objectDescriptionCardView = findViewById(R.id.objectDescriptionCardView);
        objectTitleCardView = findViewById(R.id.objectTitleCardView);
        historyButtonText = findViewById(R.id.historyButtonText);
        backButtonText = findViewById(R.id.backButtonText);
        objectTitle = findViewById(R.id.objectTitle);
        resultImage = findViewById(R.id.resultImage);
        moreInfo = findViewById(R.id.moreInfo);
        geo = findViewById(R.id.geo);
        requestCode = getIntent().getExtras().getInt("requestCode");
        additionalInfoCardView.setVisibility(View.GONE);
        geo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (objectTitle.getText().toString().isEmpty()) {
                    createPopUpMenu();
                } else {
                    geoTag(title);
                }
            }
        });
        moreInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (objectTitle.getText().toString().isEmpty()) {
                    createPopUpMenu();
                } else {
                    additionalInfoCardView.setVisibility(View.VISIBLE);
                    moreInfo.setEnabled(false);
                    ScaleAnimation scaleAnimation = new ScaleAnimation(1.0f, 1.0f, 0f, 1f, Animation.RELATIVE_TO_SELF, 1.0f, Animation.RELATIVE_TO_SELF, 1.0f);
                    scaleAnimation.setFillAfter(true);
                    scaleAnimation.setDuration(400);
                    additionalInfoCardView.startAnimation(scaleAnimation);
                    readData(title);
                }
            }
        });
        closeAdditionalCardView.setOnTouchListener(new SwipeTouchListener(ModelResult.this) {
            public void onSwipeBottom() {
                additionalInfoCardView.setVisibility(View.GONE);
                moreInfo.setEnabled(true);
                ScaleAnimation scaleAnimation = new ScaleAnimation(1.0f, 1.0f, 1f, 0f, Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, 1.0f);
                scaleAnimation.setFillAfter(false);
                scaleAnimation.setDuration(400);
                additionalInfoCardView.startAnimation(scaleAnimation);
            }
        });
        currentNightMode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        switch (currentNightMode) {
            case Configuration.UI_MODE_NIGHT_YES:
                historyButtonText.setTextColor(getResources().getColor(R.color.black));
                backButtonText.setTextColor(getResources().getColor(R.color.black));
                homeButtonLinear.setBackgroundResource(R.drawable.round_corner_dark);
                homeButtonImage.setImageResource(R.drawable.ic_selected_home_dark);
                break;
            case Configuration.UI_MODE_NIGHT_NO:
                modelResultLinearLayout.setBackgroundColor(getResources().getColor(R.color.darkWhite));
                historyButtonText.setTextColor(getResources().getColor(R.color.white));
                backButtonText.setTextColor(getResources().getColor(R.color.white));
                homeButtonLinear.setBackgroundResource(R.drawable.round_corner);
                homeButtonImage.setImageResource(R.drawable.ic_selected_home);
                break;
        }
        if (requestCode == 3) {
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(cameraIntent, 3);
        } else {
            Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(galleryIntent, 1);
        }
        backButtonLinear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectedTab != 1) {
                    switch (currentNightMode) {
                        case Configuration.UI_MODE_NIGHT_YES:
                            backButtonLinear.setBackgroundResource(R.drawable.round_corner_dark);
                            backButtonImage.setImageResource(R.drawable.ic_selected_back_dark);
                            break;
                        case Configuration.UI_MODE_NIGHT_NO:
                            backButtonLinear.setBackgroundResource(R.drawable.round_corner);
                            backButtonImage.setImageResource(R.drawable.ic_selected_back);
                            break;
                    }
                    historyButtonLinear.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                    historyButtonImage.setImageResource(R.drawable.ic_history);
                    historyButtonText.setVisibility(View.GONE);
                    backButtonText.setVisibility(View.VISIBLE);
                    homeButtonLinear.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                    homeButtonImage.setImageResource(R.drawable.ic_home);
                    selectedTab = 1;
                    Intent fromResultToMain = new Intent(ModelResult.this, MainActivity.class);
                    startActivity(fromResultToMain);
                    finish();

                }
            }
        });
        homeButtonLinear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectedTab != 2) {
                    switch (currentNightMode) {
                        case Configuration.UI_MODE_NIGHT_YES:
                            homeButtonLinear.setBackgroundResource(R.drawable.round_corner_dark);
                            homeButtonImage.setImageResource(R.drawable.ic_selected_home_dark);
                            break;
                        case Configuration.UI_MODE_NIGHT_NO:
                            homeButtonLinear.setBackgroundResource(R.drawable.round_corner);
                            homeButtonImage.setImageResource(R.drawable.ic_selected_home);
                            break;
                    }
                    historyButtonLinear.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                    historyButtonImage.setImageResource(R.drawable.ic_history);
                    historyButtonText.setVisibility(View.GONE);
                    backButtonLinear.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                    backButtonImage.setImageResource(R.drawable.ic_back);
                    backButtonText.setVisibility(View.GONE);
                    selectedTab = 2;
                    resultFrameLayout.setVisibility(View.VISIBLE);
                    content.setVisibility(View.GONE);
                }
            }
        });
        historyButtonLinear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectedTab != 3) {
                    switch (currentNightMode) {
                        case Configuration.UI_MODE_NIGHT_YES:
                            historyButtonImage.setImageResource(R.drawable.ic_selected_history_dark);
                            historyButtonLinear.setBackgroundResource(R.drawable.round_corner_dark);
                            break;
                        case Configuration.UI_MODE_NIGHT_NO:
                            historyButtonImage.setImageResource(R.drawable.ic_selected_history);
                            historyButtonLinear.setBackgroundResource(R.drawable.round_corner);
                            break;
                    }
                    historyButtonText.setVisibility(View.VISIBLE);
                    backButtonLinear.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                    backButtonImage.setImageResource(R.drawable.ic_back);
                    backButtonText.setVisibility(View.GONE);
                    homeButtonLinear.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                    homeButtonImage.setImageResource(R.drawable.ic_home);
                    ScaleAnimation scaleAnimation = new ScaleAnimation(0.8f, 1.0f, 1f, 1f, Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f);
                    scaleAnimation.setFillAfter(true);
                    scaleAnimation.setDuration(200);
                    historyButtonLinear.startAnimation(scaleAnimation);
                    selectedTab = 3;
                    resultFrameLayout.setVisibility(View.GONE);
                    content.setVisibility(View.VISIBLE);
                    loadFragment(HistoryFragment.newInstance());
                }
            }
        });
    }
    private void loadFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.content, fragment);
        fragmentTransaction.commit();
    }
    public void classifyImage(Bitmap image) {
        try {
            Newmodel model = Newmodel.newInstance(getApplicationContext());
            TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 200, 200, 3}, DataType.FLOAT32);
            ByteBuffer byteBuffer = ByteBuffer.allocateDirect(4 * imageSize * imageSize * 3);
            byteBuffer.order(ByteOrder.nativeOrder());
            int[] intValues = new int[imageSize * imageSize];
            image.getPixels(intValues, 0, image.getWidth(), 0, 0, image.getWidth(), image.getHeight());
            int pixel = 0;
            for (int i = 0; i < imageSize; i++) {
                for (int j = 0; j < imageSize; j++) {
                    int val = intValues[pixel++];
                    byteBuffer.putFloat(((val >> 16) & 0xFF) * (1.f / 1));
                    byteBuffer.putFloat(((val >> 8) & 0xFF) * (1.f / 1));
                    byteBuffer.putFloat((val & 0xFF) * (1.f / 1));
                }
            }
            inputFeature0.loadBuffer(byteBuffer);
            Newmodel.Outputs outputs = model.process(inputFeature0);
            TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();
            float[] confidences = outputFeature0.getFloatArray();
            int maxPos = 0;
            float maxConfidence = 0;
            for (int i = 0; i < confidences.length; i++) {
                if (confidences[i] > maxConfidence) {
                    maxConfidence = confidences[i];
                    maxPos = i;
                }
            }
            String[] classes = {getResources().getString(R.string.anichkov), getResources().getString(R.string.tower),
                    getResources().getString(R.string.hermitage), getResources().getString(R.string.zinger)};
            objectTitle.setText(classes[maxPos]);
            title = classes[maxPos];
            writeHistory(title);
            model.close();

        } catch (IOException e) {
        }
    }
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == 3) {
                Bitmap cameraImageBitmap = (Bitmap) data.getExtras().get("data");
                int dimension = Math.min(cameraImageBitmap.getWidth(), cameraImageBitmap.getHeight());
                cameraImageBitmap = ThumbnailUtils.extractThumbnail(cameraImageBitmap, dimension, dimension);
                resultImage.setImageBitmap(cameraImageBitmap);
                cameraImageBitmap = Bitmap.createScaledBitmap(cameraImageBitmap, imageSize, imageSize, false);
                classifyImage(cameraImageBitmap);
            } else {
                Uri galleryImage = data.getData();
                Bitmap galleryImageBitmap = null;
                try {
                    galleryImageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), galleryImage);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                resultImage.setImageBitmap(galleryImageBitmap);
                galleryImageBitmap = Bitmap.createScaledBitmap(galleryImageBitmap, imageSize, imageSize, false);
                classifyImage(galleryImageBitmap);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    private void setAppLocale(String localeCode) {
        Locale locale = new Locale(localeCode);
        Locale.setDefault(locale);
        Configuration configuration = new Configuration();
        configuration.locale = locale;
    }
    public void geoTag(String buildTitle) {
        DocumentReference docRef = db.collection("builds").document(buildTitle);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Intent onMapIntent = new Intent();
                        onMapIntent.setAction(Intent.ACTION_VIEW);
                        onMapIntent.setData(Uri.parse(document.getString("geo")));
                        startActivity(onMapIntent);
                        Log.d("ANSWER", "DocumentSnapshot data: " + document.getData());
                    } else {
                        Log.d("ANSWER", "No such document");
                    }
                } else {
                    Log.d("ANSWER", "get failed with ", task.getException());
                }
            }
        });
    }
    public void readData(String buildTitle) {
        DocumentReference docRef = db.collection("builds").document(buildTitle);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d("ANSWER", "DocumentSnapshot data: " + document.getData());
                        objectTitleCardView.setText(document.getString("title"));
                        objectDescriptionCardView.setText(document.getString("description"));
                    } else {
                        Log.d("ANSWER", "No such document");
                    }
                } else {
                    Log.d("ANSWER", "get failed with ", task.getException());
                }
            }
        });
    }
    public void writeHistory(String title) {
        Map<String, Object> build = new HashMap<>();
        if (title.contentEquals("Zinger house") || title.contentEquals("Дом Зингера")) {
            build.put("title", "Дом Зингера");
            db.collection("historyRus").document("Дом Зингера").set(build);
            build.put("title", "Zinger house");
            db.collection("historyEng").document("Zinger house").set(build);
        }
        if (title.contentEquals("Duma tower") || title.contentEquals("Думская башня")) {
            build.put("title", "Думская башня");
            db.collection("historyRus").document("Думская башня").set(build);
            build.put("title", "Duma tower");
            db.collection("historyEng").document("Duma tower").set(build);
        }
        if (title.contentEquals("Anichkov bridge") || title.contentEquals("Аничков мост")) {
            build.put("title", "Аничков мост");
            db.collection("historyRus").document("Аничков мост").set(build);
            build.put("title", "Anichkov bridge");
            db.collection("historyEng").document("Anichkov bridge").set(build);
        }

        if (title.contentEquals("Hermitage") || title.contentEquals("Эрмитаж")) {
            build.put("title", "Эрмитаж");
            db.collection("historyRus").document("Эрмитаж").set(build);
            build.put("title", "Hermitage");
            db.collection("historyEng").document("Hermitage").set(build);
        }
    }
    public void createPopUpMenu() {
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View popUpView = inflater.inflate(R.layout.poput_menu, null);
        int width = ViewGroup.LayoutParams.MATCH_PARENT;
        int height = ViewGroup.LayoutParams.MATCH_PARENT;
        boolean focusable = true;
        PopupWindow popupWindow = new PopupWindow(popUpView, width, height, focusable);
        modelResultLinearLayout.post(new Runnable() {
            @Override
            public void run() {
                popupWindow.showAtLocation(modelResultLinearLayout, Gravity.CENTER, 0, 0);
            }
        });
        TextView popUpClose, popUpMenu;
        popUpClose = popUpView.findViewById(R.id.poputClose);
        popUpMenu = popUpView.findViewById(R.id.poputMenu);
        RelativeLayout relativeLayoutPopUp = popUpView.findViewById(R.id.relativeLinearPopUp);
        LinearLayout popUpLinearLayout = popUpView.findViewById(R.id.popUnLinearLayout);
        currentNightMode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        switch (currentNightMode) {
            case Configuration.UI_MODE_NIGHT_YES:
                relativeLayoutPopUp.setBackgroundColor(getResources().getColor(R.color.pochtiwhite));
                popUpLinearLayout.setBackgroundResource(R.drawable.poput_corners_dark);
                break;
            case Configuration.UI_MODE_NIGHT_NO:
                relativeLayoutPopUp.setBackgroundColor(getResources().getColor(R.color.pohtiblack));
                popUpLinearLayout.setBackgroundResource(R.drawable.poput_corners);
                break;
        }
        popUpClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupWindow.dismiss();
            }
        });
        popUpMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent fromResultToMain = new Intent(ModelResult.this, MainActivity.class);
                startActivity(fromResultToMain);
                finish();
            }
        });
    }
}


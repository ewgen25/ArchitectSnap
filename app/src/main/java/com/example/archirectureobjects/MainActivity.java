package com.example.archirectureobjects;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.cardview.widget.CardView;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.materialswitch.MaterialSwitch;
import java.util.Locale;
public class MainActivity extends AppCompatActivity {
    CardView circleAroundCamera, circleAroundGallery, cameraIcon, galleryIcon;
    MaterialSwitch languageToggle, themeToggle;
    MaterialCardView cameraCard, galleryCard;
    LinearLayout mainScreenLinearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadLocale();
        setContentView(R.layout.activity_main);
        mainScreenLinearLayout = findViewById(R.id.mainScreenLinearLayout);
        circleAroundGallery = findViewById(R.id.circleAroundGallery);
        circleAroundCamera = findViewById(R.id.circleAroundCamera);
        galleryIcon = findViewById(R.id.galleryIcon);
        galleryCard = findViewById(R.id.galleryCard);
        cameraIcon = findViewById(R.id.cameraIcon);
        cameraCard = findViewById(R.id.cameraCard);
        languageToggle = findViewById(R.id.languageToggle);
        themeToggle = findViewById(R.id.themeToggle);
        int currentNightMode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        String currentLanguage = Locale.getDefault().getLanguage();
        switch (currentNightMode) {
            case Configuration.UI_MODE_NIGHT_YES:
                themeToggle.setChecked(true);
                break;
            case Configuration.UI_MODE_NIGHT_NO:
                themeToggle.setChecked(false);
                galleryCard.setCardBackgroundColor(getResources().getColor(R.color.theWatersOfBondiBeach));
                circleAroundCamera.setCardBackgroundColor(getResources().getColor(R.color.bistreColor));
                circleAroundGallery.setCardBackgroundColor(getResources().getColor(R.color.azureBlue));
                galleryIcon.setCardBackgroundColor(getResources().getColor(R.color.aquamarineCrayola));
                mainScreenLinearLayout.setBackgroundColor(getResources().getColor(R.color.darkWhite));
                cameraIcon.setCardBackgroundColor(getResources().getColor(R.color.grayishYellow));
                cameraCard.setCardBackgroundColor(getResources().getColor(R.color.richYellow));
                break;
        }
        switch (currentLanguage) {
            case "ru":
                languageToggle.setChecked(true);
                break;
            case "en":
                languageToggle.setChecked(false);
                break;
            default:
                languageToggle.setChecked(false);
        }
        themeToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (themeToggle.isChecked()) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                }
            }
        });
        languageToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (languageToggle.isChecked()) {
                    setAppLocale("ru");
                    recreate();
                } else {
                    setAppLocale("en");
                    recreate();
                }
            }
        });
        cameraCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ModelResult.class);
                if (languageToggle.isChecked()) {
                    intent.putExtra("selectedLanguageCode", 1);
                } else {
                    intent.putExtra("selectedLanguageCode", 2);
                }
                intent.putExtra("requestCode", 3);
                startActivity(intent);
            }
        });
        galleryCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ModelResult.class);
                if (languageToggle.isChecked()) {
                    intent.putExtra("selectedLanguageCode", 1);
                } else {
                    intent.putExtra("selectedLanguageCode", 2);
                }
                intent.putExtra("requestCode", 1);
                startActivity(intent);
            }
        });
    }
    private void setAppLocale(String localeCode) {
        Locale locale = new Locale(localeCode);
        Locale.setDefault(locale);
        Configuration configuration = new Configuration();
        configuration.locale = locale;
        getBaseContext().getResources().updateConfiguration(configuration, getBaseContext().getResources().getDisplayMetrics());
        SharedPreferences.Editor editor = getSharedPreferences("Settings", MODE_PRIVATE).edit();
        editor.putString("My_Lang", localeCode);
        editor.apply();
    }
    public void loadLocale() {
        SharedPreferences preferences = getSharedPreferences("Settings", Activity.MODE_PRIVATE);
        String language = preferences.getString("My_Lang", "");
        setAppLocale(language);
    }
}
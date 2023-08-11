package com.example.archirectureobjects;

import static android.content.ContentValues.TAG;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import org.checkerframework.checker.nullness.qual.NonNull;
import java.util.ArrayList;
import java.util.Locale;

public class HistoryFragment extends Fragment {
    private final HistoryFragment historyFragment = this;
    ArrayList<HistoryBuildTitle> history;
    AppCompatEditText searchEditText;
    RecyclerView historyRecyclerView;
    String currentLanguage;
    View view;
    public static HistoryFragment newInstance() {
        return new HistoryFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_history, container, false);
        historyRecyclerView = view.findViewById(R.id.historyRecyclerView);
        searchEditText = view.findViewById(R.id.searchEditText);
        currentLanguage = Locale.getDefault().getLanguage();
        switch (currentLanguage) {
            case "ru":
                loadHistoryTitle("historyRus");
                break;
            case "en":
                loadHistoryTitle("historyEng");
                break;
            default:
                loadHistoryTitle("historyEng");
                break;
        }
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void afterTextChanged(Editable editable) {
                ArrayList<HistoryBuildTitle> filteredList = new ArrayList<>();
                for (HistoryBuildTitle item : history) {
                    if (item.gettitle().toLowerCase().contains(editable.toString().toLowerCase())) {
                        filteredList.add(item);
                    }
                }
                HistoryAdapter adapter = new HistoryAdapter(historyFragment, filteredList);
                historyRecyclerView.setAdapter(adapter);
            }
        });
        return view;
    }
    private void loadHistoryTitle(String collectionPath) {
        history = new ArrayList<>();
        FirebaseFirestore.getInstance().
                collection(collectionPath)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                            HistoryBuildTitle historyBuildTitle = queryDocumentSnapshot.toObject(HistoryBuildTitle.class);
                            history.add(historyBuildTitle);
                            Log.d(TAG, "" + historyBuildTitle);
                        }
                        historyRecyclerView.setLayoutManager(new GridLayoutManager(view.getContext(), 2));
                        HistoryAdapter adapter = new HistoryAdapter(historyFragment, history);
                        historyRecyclerView.setHasFixedSize(true);
                        historyRecyclerView.setAdapter(adapter);
                    }
                });

    }
}
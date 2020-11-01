package com.example.ern.LearnMode;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import androidx.fragment.app.Fragment;

import com.example.ern.MainActivity;
import com.example.ern.R;
import com.example.ern.TranslationPopup;

import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class LearnFragment extends Fragment {

    private View rootView;
    private EditText searchText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.learn_mode, container, false);
        Button search_button = rootView.findViewById(R.id.learn_mode_search);
        Button wipe_button = rootView.findViewById(R.id.learn_mode_wipe);
        searchText = rootView.findViewById(R.id.learn_mode_type);

        search_button.setOnClickListener(this::onKanjiSearchButtonClick);

        wipe_button.setOnClickListener(this::onWipeButtonClick);

        return rootView;
    }

    @Override
    public void onPause() {
        super.onPause();
        ((MainActivity) getActivity()).writeTranslations();
    }

    private void onKanjiSearchButtonClick(View v) {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(searchText.getWindowToken(), 0);
        ExecutorService executor_query = Executors.newSingleThreadExecutor();
        Future<String> queryFuture = executor_query.submit(() -> getStringFromDatabaseKanji(searchText.getText().toString()));
        try {
            String expression = searchText.getText().toString();
            String translation = queryFuture.get();
            TranslationPopup.showPopupWindow(expression, translation, v);
            ((MainActivity) getActivity()).addTranslation(expression, translation);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void onWipeButtonClick(View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setCancelable(true);
        builder.setTitle("Wipe");
        builder.setMessage("Are you sure you want to wipe all translations data?");
        builder.setPositiveButton("Confirm", (dialog, which) -> ((MainActivity) getActivity()).wipeTranslationsAndData());
        builder.setNegativeButton("Cancel", ((dialog, which) -> {}));

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private String getStringFromDatabaseKanji(String kanji) {
        Translation[] ts = TranslationDatabase.getInstance(getActivity()).translationDao().getTranslationsByKanji(kanji);
        Log.d("LEARN", String.valueOf(ts.length));
        return ts[0].translations[0];
    }
}

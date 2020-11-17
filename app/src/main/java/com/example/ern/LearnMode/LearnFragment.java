package com.example.ern.LearnMode;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.fragment.app.Fragment;

import com.example.ern.LearnMode.TranslationDatabase.Translation;
import com.example.ern.LearnMode.TranslationDatabase.TranslationDatabase;
import com.example.ern.MainActivity;
import com.example.ern.R;
import com.example.ern.TranslationPopup;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class LearnFragment extends Fragment {

    private static final String LEARN = "LEARN";
    private View rootView;
    private EditText searchText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.learn_mode, container, false);
        ImageButton search_button = rootView.findViewById(R.id.learn_mode_search);
        searchText = rootView.findViewById(R.id.learn_mode_type);

        search_button.setOnClickListener(this::onSearchButtonClick);

        search_button.setOnLongClickListener(this::onWipeButtonClick);

        return rootView;
    }

    @Override
    public void onPause() {
        super.onPause();
        ((MainActivity) getActivity()).writeTranslations();
    }

    private void onSearchButtonClick(View v) {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(searchText.getWindowToken(), 0);
        ExecutorService executor_query = Executors.newSingleThreadExecutor();
        Future<String> queryFuture = executor_query.submit(() -> getStringFromDatabase(searchText.getText().toString()));
        try {
            String expression = searchText.getText().toString();
            String translation = queryFuture.get();
            TranslationPopup.showPopupWindow(expression, translation, v);
            ((MainActivity) getActivity()).addTranslation(expression, translation);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean onWipeButtonClick(View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setCancelable(true);
        builder.setTitle("Wipe");
        builder.setMessage("Are you sure you want to wipe all translations data?");
        builder.setPositiveButton("Confirm", (dialog, which) -> ((MainActivity) getActivity()).wipeTranslationsAndData());
        builder.setNegativeButton("Cancel", ((dialog, which) -> {}));

        AlertDialog dialog = builder.create();
        dialog.show();
        return true;
    }

    //TODO: probably have to use some library to discern kana.
    private String getStringFromDatabase(String text) {
        Translation[] ts = TranslationDatabase.getInstance(getActivity()).translationDao().getTranslationsByKanji(text);
        String ret = "";
        if (ts.length == 0) {
            ts = TranslationDatabase.getInstance(getActivity()).translationDao().getTranslationsByRomaji(text);
            if (ts.length == 0) {
                Log.d(LEARN, "Probably invalid.");
                return MainActivity.INVALID_INPUT;
            }
            Log.d(LEARN, "Probably romaji.");
        }
        else {
            Log.d(LEARN, "Probably kanji");
            ret = ret.concat(ts[0].romajiExpression + "\n");
        }
        Log.d(LEARN, String.valueOf(ts.length));
        for (int i = 0; i < ts[0].translations.length; i++) {
            ret = ret.concat(String.valueOf(i + 1) + ". " + ts[0].translations[i] + (i == ts[0].translations.length - 1 ? "" : "\n"));
        }
        return ret;
    }
}

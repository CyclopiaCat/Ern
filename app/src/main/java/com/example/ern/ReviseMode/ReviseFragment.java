package com.example.ern.ReviseMode;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.ern.MainActivity;
import com.example.ern.R;
import com.example.ern.TranslationPopup;

import java.util.ArrayList;
import java.util.TreeMap;

public class ReviseFragment extends Fragment {

    private static String REVISE = "REVISE";

    private View rootView;
    private ArrayList<TreeMap<String, String>> currentSession;
    private int sessionProgress;
    private boolean inProgress;
    private TextView kanji;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.revise_mode, container, false);
        Button button_check = rootView.findViewById(R.id.revise_mode_check);
        Button button_next = rootView.findViewById(R.id.revise_mode_next);
        kanji = rootView.findViewById(R.id.revise_mode_kanji);

        button_check.setOnClickListener(this::onCheckButtonClick);
        button_next.setOnClickListener(this::onNextButtonClick);

        return rootView;
    }

    @Override
    public void onPause() {
        super.onPause();
        exitSession();
    }

    public void enterSession(ArrayList<TreeMap<String, String>> array) {
        currentSession = array;
        sessionProgress = 0;
        inProgress = array.size() > 0;
        if (inProgress) setKanji();
    }

    public void exitSession() {
        ((MainActivity) getActivity()).updateAndWriteTranslations(currentSession, sessionProgress);
    }

    private void onCheckButtonClick(View view) {
        try {
            if (inProgress) {
                TreeMap<String, String> currentTranslation = currentSession.get(sessionProgress);
                TranslationPopup.showPopupWindow(currentTranslation.get("Kanji"), currentTranslation.get("Translation"), view);
                nextTranslation();
            } else {
                TranslationPopup.showPopupWindow("Revise Mode:", "OVER", view);
            }
        } catch (Exception ignored) {}
    }

    private void onNextButtonClick(View view) {
        try {
            TreeMap<String, String> currentTranslation = currentSession.get(sessionProgress);
            currentTranslation.put("Successes", String.valueOf(Integer.parseInt(currentTranslation.get("Successes")) + 1));
            nextTranslation();
        } catch (Exception ignored) {}
    }

    private void setKanji() {
        kanji.setText(currentSession.get(sessionProgress).get("Kanji"));
    }

    private void nextTranslation() {
        if (++sessionProgress >= currentSession.size()) inProgress = false;
        else setKanji();
    }
}

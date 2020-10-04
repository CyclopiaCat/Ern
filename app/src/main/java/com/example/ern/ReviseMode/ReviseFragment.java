package com.example.ern.ReviseMode;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.ern.MainActivity;
import com.example.ern.R;
import com.example.ern.TranslationPopup;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.TreeMap;

public class ReviseFragment extends Fragment {

    private View rootView;
    private ArrayList<TreeMap<String, String>> currentSession;
    private int sessionProgress;
    private boolean inProgress;
    private TextView kanji;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.revise_mode, container, false);
        Button button = rootView.findViewById(R.id.revise_mode_check);
        kanji = rootView.findViewById(R.id.revise_mode_kanji);
        Log.d("REVISE", kanji.toString());

        button.setOnClickListener(this::showRevisePopupWindowOnClick);
        return rootView;
    }

    @Override
    public void onPause() {
        super.onPause();
        closeSession();
    }

    public void enterSession(ArrayList<TreeMap<String, String>> array) {
        currentSession = array;
        sessionProgress = 0;
        inProgress = array.size() > 0;
        if (inProgress) try {
            kanji.setText(currentSession.get(0).get("Kanji"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void closeSession() {
        ((MainActivity) getActivity()).updateTranslations(currentSession, sessionProgress);
    }

    public void showRevisePopupWindowOnClick(View view) {
        try {
            if (inProgress) {
                TreeMap<String, String> currentTranslation = currentSession.get(sessionProgress);
                TranslationPopup.showPopupWindow(currentTranslation.get("Kanji"), currentTranslation.get("Translation"), view);
                if (++sessionProgress >= currentSession.size()) inProgress = false;
                else kanji.setText(currentSession.get(sessionProgress).get("Kanji"));
            } else {
                TranslationPopup.showPopupWindow("Revise Mode:", "OVER", view);
            }
        } catch (Exception ignored) {}
    }
}

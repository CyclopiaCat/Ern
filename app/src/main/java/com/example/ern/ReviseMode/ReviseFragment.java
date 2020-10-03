package com.example.ern.ReviseMode;

import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.ern.R;
import com.example.ern.TranslationPopup;

public class ReviseFragment extends Fragment {

    private View rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.revise_mode, container, false);
        Button button = rootView.findViewById(R.id.revise_mode_check);
        TextView text = rootView.findViewById(R.id.revise_mode_kanji);

        button.setOnClickListener(this::showRevisePopupWindowOnClick);
        return rootView;
    }

    public void showRevisePopupWindowOnClick(View view) {
        TranslationPopup.showPopupWindow("Librum", "Ipsus", view); // Placeholder.
    }
}

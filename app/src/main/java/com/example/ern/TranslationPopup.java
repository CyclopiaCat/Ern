package com.example.ern;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

public abstract class TranslationPopup {

    public static void showPopupWindow(String kanji, String translation, View v) {
        LayoutInflater inflater = (LayoutInflater) v.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.translation_popup, null);
        TextView popup_kanji = popupView.findViewById(R.id.translation_popup_kanji);
        TextView popup_translation = popupView.findViewById(R.id.translation_popup_translation);

        popup_kanji.setText(kanji);
        popup_translation.setText(translation);

        int width = FrameLayout.LayoutParams.WRAP_CONTENT;
        int height = FrameLayout.LayoutParams.WRAP_CONTENT;
        boolean focusable = true;
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

        popupWindow.showAtLocation(v, Gravity.CENTER, 0, 0);
    }
}

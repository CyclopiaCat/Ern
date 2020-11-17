package com.example.ern;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import androidx.constraintlayout.widget.ConstraintAttribute;
import androidx.constraintlayout.widget.ConstraintLayout;

public class MainFrame extends FrameLayout {

    GestureDetector detector;

    public MainFrame(Context context) {
        super(context);
    }

    public MainFrame(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MainFrame(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @SuppressLint("ClickableViewAccessibility")
    public void setOnSwipeListener(OnSwipeListener osl) {
        detector = new GestureDetector(getContext(), osl);
        this.setOnTouchListener((v, ev) -> detector.onTouchEvent(ev));
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (!this.detector.onTouchEvent(ev))
            return super.dispatchTouchEvent(ev);
        else
            return true;
    }
}

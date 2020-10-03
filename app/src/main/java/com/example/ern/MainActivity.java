package com.example.ern;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MotionEventCompat;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.example.ern.LearnMode.LearnFragment;
import com.example.ern.ReviseMode.ReviseFragment;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class MainActivity extends AppCompatActivity {

    protected enum Current {
        LEARN,
        REVISE
    }

    private Current fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fragment = Current.LEARN;
        getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new LearnFragment()).commit();

        View main = findViewById(R.id.main);
        main.setOnTouchListener(new OnSwipeTouchListener(main.getContext()) {
            @Override
            public void onSwipeLeft() {
                trySwitchToLearn();
            }
            @Override
            public void onSwipeRight() {
                trySwitchToRevise();
            }
        });
    }

    private void trySwitchToLearn() {
        if (fragment == Current.LEARN) return;

        getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new LearnFragment()).commit();
        fragment = Current.LEARN;
    }

    private void trySwitchToRevise() {
        if (fragment == Current.REVISE) return;

        getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new ReviseFragment()).commit();
        fragment = Current.REVISE;
    }
}

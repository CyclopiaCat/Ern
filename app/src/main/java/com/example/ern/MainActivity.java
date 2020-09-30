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
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
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



//    public void showLearnMode() {
//        setContentView(R.layout.learn_mode);
//
//        Button button = findViewById(R.id.learn_mode_search);
//        EditText text = findViewById(R.id.learn_mode_type);
//
//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
//                imm.hideSoftInputFromWindow(findViewById(R.id.learn_mode_type).getWindowToken(), 0);
//                ExecutorService executor = Executors.newSingleThreadExecutor();
//                Future<JSONObject> json = executor.submit(() -> {
//                    return queryJishoGetJson(text.getText());
//                });
//                try {
//                    showLearnPopupWindow(json.get(), text.getText(), v);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        });
//    }
//
//    public void showLearnPopupWindow(JSONObject json, Editable text, View v) {
//        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        View popupView = inflater.inflate(R.layout.translation_popup, null);
//        TextView popup_kanji = popupView.findViewById(R.id.translation_popup_kanji);
//        TextView popup_text = popupView.findViewById(R.id.translation_popup_text);
//
//        popup_kanji.setText(text.toString());
//        try {
//            String english_meanings = json.getJSONArray("data").getJSONObject(0).getJSONArray("senses")
//                    .getJSONObject(0).getJSONArray("english_definitions").toString(2);
//            popup_text.setText(english_meanings);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        int width = FrameLayout.LayoutParams.WRAP_CONTENT;
//        int height = FrameLayout.LayoutParams.WRAP_CONTENT;
//        boolean focusable = true;
//        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);
//
//        popupWindow.showAtLocation(v, Gravity.CENTER, 0, 0);
//    }
//
//    public JSONObject queryJishoGetJson(Editable text) {
//        HttpURLConnection urlConnection = null;
//        JSONObject json = null;
//        try {
//            URL url = new URL("https://jisho.org/api/v1/search/words?keyword=" + text);
//            urlConnection = (HttpURLConnection) url.openConnection();
//            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
//            json = new JSONObject(readStream(in));
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            urlConnection.disconnect();
//        }
//        return json;
//    }
//
//    private String readStream(InputStream is) throws IOException {
//        StringBuilder sb = new StringBuilder();
//        BufferedReader r = new BufferedReader(new InputStreamReader(is),1000);
//        for (String line = r.readLine(); line != null; line =r.readLine()){
//            sb.append(line);
//        }
//        is.close();
//        return sb.toString();
//    }
}

package com.example.ern;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.TreeMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.apache.commons.lang3.math.NumberUtils.max;

//TODO: clean up, comment, scope manage.

public class MainActivity extends AppCompatActivity {

    private static String TRANSLATIONS = "TRANSLATIONS";

    private enum Current {
        LEARN,
        REVISE
    }

    private Current fragment;
    private ArrayList<TreeMap<String, String>> translations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FileManager.checkDatabase(this);
        translations = FileManager.readTranslations(this);

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

    public void wipeTranslationsAndData() {
        translations = new ArrayList<>();
        FileManager.writeTranslations(this, new ArrayList<>());
    }

    public void addTranslation(String kanji, String translation) {
        try {
            TreeMap<String, String> entry = new TreeMap<>();
            entry.put("Kanji", kanji);
            entry.put("Translation", translation);
            entry.put("Date", new SimpleDateFormat("MMM d, yyyy HH:mm:ss", Locale.US).format(new Date()));
            entry.put("Successes", "0");
            translations.add(entry);
            Log.d("TRANSLATIONS", translations.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateAndWriteTranslations(ArrayList<TreeMap<String, String>> array, int progress) {
        translations.sort((t1, t2) -> getTranslationPriority(t1) - getTranslationPriority(t2));
        FileManager.writeTranslations(this, translations);
    }

    @SuppressWarnings("ConstantConditions")
    private int getTranslationPriority(TreeMap<String, String> translation) {
        DateFormat date = DateFormat.getDateInstance();
        try {
            return -max(1 << Integer.parseInt(translation.get("Successes")) - date.parse(translation.get("Date")).getTime() / 80000 - 1,
                        0);                           //TODO: make this better.
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    private void trySwitchToLearn() {
        if (fragment == Current.LEARN) return;

        getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new LearnFragment()).commit();
        fragment = Current.LEARN;
    }

    private void trySwitchToRevise() {
        if (fragment == Current.REVISE) return;
        ReviseFragment reviseFragment = new ReviseFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, reviseFragment).commit();
        getSupportFragmentManager().executePendingTransactions();
        reviseFragment.enterSession(translations);
        fragment = Current.REVISE;
    }
}

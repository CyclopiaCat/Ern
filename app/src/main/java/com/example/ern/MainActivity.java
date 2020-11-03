package com.example.ern;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.ern.LearnMode.LearnFragment;
import com.example.ern.ReviseMode.ReviseFragment;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

import static org.apache.commons.lang3.math.NumberUtils.max;

//TODO: clean up, comment, scope manage.

public class MainActivity extends AppCompatActivity {

    private static final String TRANSLATIONS = "TRANSLATIONS";

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
                switchToLearnIfRevise();
            }
            @Override
            public void onSwipeRight() {
                switchToReviseIfLearn();
            }
        });
    }

    public void wipeTranslationsAndData() {
        translations = new ArrayList<>();
        FileManager.writeTranslations(this, new ArrayList<>());
    }

    // If the check is slow, move somewhere else.
    // This is and should be the only method that adds any entries. Thus, it should not generate errors and invalid entries.
    public void addTranslation(String expression, String translation) {
        try {
            // Update the existing entry if it exists.
            for (TreeMap<String, String> entry : translations) {
                if (entry.get("Expression").equals(expression)) {
                    entry.put("Successes", "0");
                    Log.d(TRANSLATIONS, translations.toString());
                    return;
                }
            }

            TreeMap<String, String> entry = new TreeMap<>();
            entry.put("Expression", expression);
            entry.put("Translation", translation);
            entry.put("Date", new SimpleDateFormat("MMM d, yyyy HH:mm:ss", Locale.US).format(new Date()));
            entry.put("Successes", "0");
            translations.add(0, entry); // Put at 0 for sort purposes.
            Log.d(TRANSLATIONS, translations.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateAndWriteTranslations() {
        translations.sort((t1, t2) -> getTranslationPriority(t1) - getTranslationPriority(t2));
        writeTranslations();
    }

    public void writeTranslations() {
        FileManager.writeTranslations(this, translations);
    }

    // max(2^Successes - Days - 1, 0)
    @SuppressWarnings("ConstantConditions")
    private int getTranslationPriority(TreeMap<String, String> translation) {
        DateFormat date = DateFormat.getDateInstance();
        try {
            int max = (int) max((1 << Integer.parseInt(translation.get("Successes"))) - 1
                           - TimeUnit.DAYS.convert(new Date().getTime() - date.parse(translation.get("Date")).getTime(),
                                           TimeUnit.MILLISECONDS),
                                0);
            Log.d(TRANSLATIONS, translation.get("Successes") + ", " + translation.get("Date") + " -> " + max);
            return max;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    private void switchToLearnIfRevise() {
        if (fragment == Current.LEARN) return;

        getSupportFragmentManager().beginTransaction().setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                                   .replace(R.id.content_frame, new LearnFragment()).commit();
        fragment = Current.LEARN;
    }

    private void switchToReviseIfLearn() {
        if (fragment == Current.REVISE) return;
        ReviseFragment reviseFragment = new ReviseFragment();
        getSupportFragmentManager().beginTransaction().setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                                   .replace(R.id.content_frame, reviseFragment).commit();
        getSupportFragmentManager().executePendingTransactions(); // enterSession misbehaves without this.
        reviseFragment.enterSession(translations);
        fragment = Current.REVISE;
    }
}

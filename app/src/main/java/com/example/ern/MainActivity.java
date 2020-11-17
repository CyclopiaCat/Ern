package com.example.ern;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;

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

    public static final String INVALID_INPUT = "Nothing was found with this query. Please try again.";

    private Current fragment;
    private ArrayList<TreeMap<String, String>> translations;

    @SuppressLint("ClickableViewAccessibility")
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

        MainFrame main = findViewById(R.id.content_frame);
        main.setOnSwipeListener(new OnSwipeListener() {
            @Override
            public boolean onSwipe(Direction direction) {
                if (direction == Direction.LEFT)
                    switchToLearnIfRevise();
                else if (direction == Direction.RIGHT)
                    switchToReviseIfLearn();
                else
                    return false;
                return true;
            }
        });
    }

    public void wipeTranslationsAndData() {
        translations = new ArrayList<>();
        FileManager.writeTranslations(this, new ArrayList<>());
    }

    // This is and should be the only method that adds any entries. Thus, it should not generate errors and invalid entries.
    public void addTranslation(String expression, String translation) {
        try {
            if (translation.equals(INVALID_INPUT)) {
                Log.d(TRANSLATIONS, "Translation body was empty. Ignored.");
                return;
            }
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
        switchToLearnIfRevise();
    }

    public void writeTranslations() {
        FileManager.writeTranslations(this, translations);
    }

    // max(2^Successes - Days - 1, 0)
    @SuppressWarnings("ConstantConditions")
    private int getTranslationPriority(TreeMap<String, String> translation) {
        DateFormat date = DateFormat.getDateInstance();
        try {
            int s = Integer.parseInt(translation.get("Successes"));
            int d = (int) TimeUnit.DAYS.convert(new Date().getTime() - date.parse(translation.get("Date")).getTime(),
                          TimeUnit.MILLISECONDS);
            int ret = (1 << s) - d;

            Log.d(TRANSLATIONS, s + ", " + d + " -> " + ret);
            return ret;
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
        updateAndWriteTranslations();
        getSupportFragmentManager().executePendingTransactions(); // enterSession misbehaves without this.
        reviseFragment.enterSession(translations);
        fragment = Current.REVISE;
    }
}

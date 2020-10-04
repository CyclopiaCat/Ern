package com.example.ern.LearnMode;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.ern.FileManager;
import com.example.ern.MainActivity;
import com.example.ern.R;
import com.example.ern.TranslationPopup;

import org.apache.commons.text.StringEscapeUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LearnFragment extends Fragment {

    private View rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.learn_mode, container, false);
        Button search_button = rootView.findViewById(R.id.learn_mode_search);
        Button wipe_button = rootView.findViewById(R.id.learn_mode_wipe);
        EditText text = rootView.findViewById(R.id.learn_mode_type);

        search_button.setOnClickListener(v -> {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(text.getWindowToken(), 0);
            ExecutorService executor = Executors.newSingleThreadExecutor();
            Future<String> translationFuture = executor.submit(() -> queryWiktionaryGetString(text.getText().toString()));
            try {
                String kanji = text.getText().toString();
                String translation = translationFuture.get();
                TranslationPopup.showPopupWindow(kanji, translation, v);
                ((MainActivity) getActivity()).addTranslation(kanji, translation);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        wipe_button.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

            builder.setCancelable(true);
            builder.setTitle("Wipe");
            builder.setMessage("Are you sure you want to wipe all the translations data?");
            builder.setPositiveButton("Confirm", (dialog, which) -> ((MainActivity) getActivity()).wipeTranslationsAndData());
            builder.setNegativeButton("Cancel", ((dialog, which) -> {}));

            AlertDialog dialog = builder.create();
            dialog.show();
        });

        return rootView;
    }

    private String readStream(InputStream is) throws IOException {
        StringBuilder sb = new StringBuilder();
        BufferedReader r = new BufferedReader(new InputStreamReader(is),1000);
        for (String line = r.readLine(); line != null; line =r.readLine()){
            sb.append(line);
        }
        is.close();
        return sb.toString();
    }

    public String queryWiktionaryGetString(String text) {
        HttpURLConnection urlConnection = null;
        String translation = null;
        try {
            URL url = new URL("https://ja.wiktionary.org/w/api.php?action=query&format=json&prop=extracts&titles=" + text);
            urlConnection = (HttpURLConnection) url.openConnection();
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            translation = parseWiktionaryString(readStream(in));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) urlConnection.disconnect();
        }
        return translation;
    }

    //TODO: fix this or change API.
    public String parseWiktionaryString(String text) {
        String ret = null;
        Matcher m = Pattern.compile("n<ol><li>.*?</li>").matcher(text);
        if (m.find()) ret = m.group();
        else return null;
        ret = StringEscapeUtils.unescapeJava(ret);
        ret = ret.substring(9, ret.length() - 5);
        Log.d("WIKTIONARY", ret);
        return ret;
    }
}

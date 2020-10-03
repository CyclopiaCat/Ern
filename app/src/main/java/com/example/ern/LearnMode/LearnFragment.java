package com.example.ern.LearnMode;

import android.content.Context;
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
        Button button = rootView.findViewById(R.id.learn_mode_search);
        EditText text = rootView.findViewById(R.id.learn_mode_type);

        button.setOnClickListener(v -> {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(text.getWindowToken(), 0);
            ExecutorService executor = Executors.newSingleThreadExecutor();
//            Future<JSONObject> json = executor.submit(() -> queryJishoGetJson(text.getText().toString()));
//            try {
//                TranslationPopup.showPopupWindow(text.getText().toString(), parseJishoJson(json.get()), v); // This crap freezes the app when no connection.
            Future<String> translation = executor.submit(() -> queryWiktionaryGetString(text.getText().toString()));
            try {
                TranslationPopup.showPopupWindow(text.getText().toString(), translation.get(), v);
            } catch (Exception e) {                                                                         //TODO: remove this
                e.printStackTrace();
            }
        });
        return rootView;
    }

    public JSONObject queryJishoGetJson(String text) throws IOException {
        HttpURLConnection urlConnection = null;
        JSONObject json = null;
        if (!LearnConnection.isNetworkAvailable(getActivity())) {
            throw new IOException("NO DAMN NETWORK DAMMIT!");
        }
        try {
            URL url = new URL("https://jisho.org/api/v1/search/words?keyword=" + text);
            urlConnection = (HttpURLConnection) url.openConnection();
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            json = new JSONObject(readStream(in));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) urlConnection.disconnect();
        }
        return json;
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

    private String parseJishoJson(JSONObject json) throws JSONException {
        Log.d("JSON", json.toString());
        String english_meanings = json.getJSONArray("data").getJSONObject(0).getJSONArray("senses")
                                      .getJSONObject(0).getJSONArray("english_definitions").toString(2);
        return english_meanings;
    }

    public String queryWiktionaryGetString(String text) throws IOException {
        HttpURLConnection urlConnection = null;
        String translation = null;
        if (!LearnConnection.isNetworkAvailable(getActivity())) {
            throw new IOException("NO DAMN NETWORK DAMMIT!");
        }
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

    public String parseWiktionaryString(String text) throws UnsupportedEncodingException {
        String ret = null;
        Matcher m = Pattern.compile("n<ol><li>.*?</li>").matcher(text);
        if (m.find()) ret = m.group();
        else return null;
        ret = StringEscapeUtils.unescapeJava(ret);
        Log.d("WIKTIONARY", ret);
        return ret;
    }
}

package com.example.ern;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.util.Pair;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

public final class FileManager {

    public static final String DATA = "data";

    public static void checkDatabase(Context context) {
        try {
            File file = new File(context.getFilesDir(), DATA);

            file.createNewFile();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<TreeMap<String, String>> readTranslations(Context context) {
        try {
            String translations = readData(context);
            if (translations.length() == 0) {
                Log.d("FILE_MANAGER", "File was empty.");
                return new ArrayList<>();
            }
            else {
                Gson gson = new Gson();
                Type type = new TypeToken<ArrayList<TreeMap<String, String>>>(){}.getType();
                return gson.fromJson(readData(context), type);
            }
        } catch (Exception ignored) {}
        return null;
    }

    public static void writeTranslations(Context context, ArrayList<TreeMap<String, String>> translations) {
        writeData(context, translations.toString());
    }

    public static void addData(Context context, String contents) {
        try (FileOutputStream fos = context.openFileOutput(DATA, Context.MODE_PRIVATE | Context.MODE_APPEND)) {
            fos.write(contents.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void writeData(Context context, String contents) {
        try (FileOutputStream fos = context.openFileOutput(DATA, Context.MODE_PRIVATE)) {
            fos.write(contents.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String readData(Context context) {
        StringBuilder stringBuilder = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(context.openFileInput(DATA), StandardCharsets.UTF_8))) {
            String line = reader.readLine();
            while (line != null) {
                stringBuilder.append(line).append('\n');
                line = reader.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }
}

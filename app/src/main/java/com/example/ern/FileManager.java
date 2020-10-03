package com.example.ern;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

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
import java.nio.charset.StandardCharsets;

public final class FileManager {

    public static final String DATABASE = "database";

    public void checkDatabase(Context context) throws IOException {
        File file = new File(context.getFilesDir(), DATABASE);

        file.createNewFile();
    }
    
    public void writeDatabase(Context context, String contents) {
        try (FileOutputStream fos = context.openFileOutput(DATABASE, Context.MODE_PRIVATE)) {
            fos.write(contents.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String readDatabase(Context context) throws IOException /* I mean damned exceptions */ {
        FileInputStream fis = context.openFileInput(DATABASE);
        InputStreamReader inputStreamReader = new InputStreamReader(fis, StandardCharsets.UTF_8);
        StringBuilder stringBuilder = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(inputStreamReader)) {
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

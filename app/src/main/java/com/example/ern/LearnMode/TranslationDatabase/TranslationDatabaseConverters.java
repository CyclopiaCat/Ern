package com.example.ern.LearnMode.TranslationDatabase;

import android.text.TextUtils;

import androidx.room.TypeConverter;

public class TranslationDatabaseConverters {
    @TypeConverter
    public String[] stringToStringArray(String str) {
        return str.split("\\Q|\\E");
    }
    @TypeConverter
    public String stringToStringArray(String[] strArr) {
        return TextUtils.join("\\Q|\\E", strArr);
    }
}

package com.example.ern.LearnMode;

import android.text.TextUtils;

import androidx.room.TypeConverter;

public class TranslationDatabaseConverters {
    @TypeConverter
    public String[] stringToStringArray(String str) {
        return str.split(",");
    }
    @TypeConverter
    public String stringToStringArray(String[] strArr) {
        return TextUtils.join(",", strArr);
    }
}

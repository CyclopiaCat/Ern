package com.example.ern.LearnMode;


import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverter;
import androidx.room.TypeConverters;

import com.example.ern.MainActivity;

@Database(entities = Translation.class, version = 1, exportSchema = false)
@TypeConverters({TranslationDatabaseConverters.class})
public abstract class TranslationDatabase extends RoomDatabase {

    private static final String DB_NAME = "translations.sqlite";
    private static volatile TranslationDatabase instance;

    public static synchronized TranslationDatabase getInstance(Context context) {
        if (instance == null) {
            init(context);
        }
        return instance;
    }

    public static void init(Context context) {
        instance = Room.databaseBuilder(context, TranslationDatabase.class, DB_NAME)
                       .createFromAsset("translations.db")
                       .build();
        Log.d("WORKS", "yep");
    }

    abstract public TranslationDao translationDao();


}

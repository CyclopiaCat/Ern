package com.example.ern.LearnMode;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "Translations")
public class Translation {
    @PrimaryKey
    public int id;

    public String kanjiExpression;
    public String romajiExpression;
    public String[] translations;


}

package com.example.ern.LearnMode.TranslationDatabase;

import androidx.room.Dao;
import androidx.room.Query;

@Dao
public interface TranslationDao {
    @Query("select * from Translations where kanjiExpression = :kanjiExpression")
    Translation[] getTranslationsByKanji(String kanjiExpression);

    @Query("select * from Translations where romajiExpression = :romajiExpression")
    Translation[] getTranslationsByRomaji(String romajiExpression);

    // For debug.
    @Query("select * from Translations limit :n")
    Translation[] getSomething(int n);
}

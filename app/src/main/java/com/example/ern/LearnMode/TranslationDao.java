package com.example.ern.LearnMode;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

@Dao
public interface TranslationDao {
    @Query("select * from Translations where kanjiExpression = :kanjiExpression")
    Translation[] getTranslationsByKanji(String kanjiExpression);

    @Query("select * from Translations where romajiExpression = :romajiExpression")
    Translation[] getTranslationsByRomaji(String romajiExpression);

    @Query("select * from Translations limit 4")
    Translation[] getSomething();
}

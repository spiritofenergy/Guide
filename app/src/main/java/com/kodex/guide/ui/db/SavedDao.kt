package com.kodex.guide.ui.db


import androidx.room.*
 import com.kodex.guide.data.source.local.SavedEntity
import kotlinx.coroutines.flow.Flow
    @Dao
    interface SavedDao {
        // Получаем ВСЕ сохраненные посты
        @Query("SELECT * FROM saved")
        fun getSavedPosts(): Flow<List<SavedEntity>>

        // Сохраняем пост
        @Insert(onConflict = OnConflictStrategy.REPLACE)
        suspend fun insertPost(post: SavedEntity)

        // Удаляем по ключу (String)
        @Query("DELETE FROM saved WHERE `key` = :key")
        suspend fun deleteByKey(key: String): Int

        // Проверяем наличие
        @Query("SELECT EXISTS(SELECT 1 FROM saved WHERE `key` = :key)")
        suspend fun existsByKey(key: String): Boolean
    }
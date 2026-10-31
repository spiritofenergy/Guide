package com.kodex.guide.ui.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.kodex.guide.data.source.local.SavedEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SavedPostsDao {
    @Query("SELECT * FROM saved")
    fun getSavedPosts(): Flow<List<SavedEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPost(post: SavedEntity)

    @Query("DELETE FROM saved WHERE `key` = :key")
    suspend fun deleteByKey(key: String): Int

    @Query("SELECT EXISTS(SELECT 1 FROM saved WHERE `key` = :key)")
    suspend fun existsByKey(key: String): Boolean
}
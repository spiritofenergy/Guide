package com.kodex.guide.ui.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.kodex.guide.data.source.local.MyPostEntity
import com.kodex.guide.data.source.local.SavedEntity
import kotlinx.coroutines.flow.Flow

    @Dao
    interface MyPostDao {

        @Query("SELECT * FROM mypost ORDER BY updatedAt DESC")
        fun getMyPosts(): Flow<List<MyPostEntity>>

        @Query("SELECT * FROM mypost WHERE `key` = :key")
        suspend fun getPostByKey(key: String): MyPostEntity?

        @Insert(onConflict = OnConflictStrategy.REPLACE)
        suspend fun insertPost(post: MyPostEntity)

        @Query("DELETE FROM mypost WHERE `key` = :key")
        suspend fun deleteByKey(key: String): Int

        @Query("SELECT EXISTS(SELECT 1 FROM mypost WHERE `key` = :key)")
        suspend fun existsByKey(key: String): Boolean

      /*  @Query("SELECT * FROM mypost")
        fun getSavedPosts(): Flow<List<MyPostEntity>>

        @Insert(onConflict = OnConflictStrategy.REPLACE)
        suspend fun insertPost(post: MyPostEntity)

        @Query("DELETE FROM mypost WHERE `key` = :key")
        suspend fun deleteByKey(key: String): Int

        @Query("SELECT EXISTS(SELECT 1 FROM mypost WHERE `key` = :key)")
        suspend fun existsByKey(key: String): Boolean*/
    }

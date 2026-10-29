package com.kodex.guide.ui.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.kodex.guide.data.source.local.BannerEntity
import com.kodex.guide.data.source.local.DraftEntity
import com.kodex.guide.domain.model.Book

@Database(entities = [Book::class, DraftEntity::class, BannerEntity::class], version = 7, exportSchema = false)
@TypeConverters(BookConverter::class)
abstract class MainDb: RoomDatabase() {

    abstract fun draftDao(): DraftDao
    abstract fun trackDao(): RoomDao

}



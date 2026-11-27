package com.kodex.guide.ui.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.kodex.guide.data.source.local.BannerEntity
import com.kodex.guide.data.source.local.MyPostEntity
import com.kodex.guide.data.source.local.SavedEntity
import com.kodex.guide.domain.model.Book

@Database(
    entities = [
    Book::class,
    SavedEntity::class,
    BannerEntity::class,
    MyPostEntity::class],
    version = 10, exportSchema = false)
@TypeConverters(BookConverter::class)
abstract class MainDb: RoomDatabase() {

    companion object {
        val MIGRATION_9_10 = object : Migration(9, 10) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    "ALTER TABLE mypost ADD COLUMN frame INTEGER NOT NULL DEFAULT 0"
                )
            }
        }
    }

    abstract fun savedDao(): SavedDao // Пока свободно
    abstract fun bookDao(): RoomDao   // BeckUp Firebase
    abstract fun savedPostsDao(): SavedPostsDao // Сохраненные по кнопке маркера
    abstract fun myPostDao(): MyPostDao // Мои посты Мною созданные

}



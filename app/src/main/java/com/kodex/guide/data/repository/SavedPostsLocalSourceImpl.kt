package com.kodex.guide.data.repository

import com.kodex.guide.data.source.local.SavedPostsLocalSource
import com.kodex.guide.data.source.local.toDomain
import com.kodex.guide.data.source.local.toSavedEntity
import com.kodex.guide.domain.model.Book
import com.kodex.guide.ui.db.RoomDao
import com.kodex.guide.ui.db.SavedDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject


class SavedPostsLocalSourceImpl @Inject constructor(
    private val dao: SavedDao // <-- Заменили RoomDao на SavedPostsDao
) : SavedPostsLocalSource {

    override fun observePosts(): Flow<List<Book>> {
        // Берем данные из таблицы saved_posts и превращаем Entity в Book
        return dao.getSavedPosts()
            .map { entities -> entities.map { it.toDomain() } }
            .flowOn(Dispatchers.IO)
    }

    override suspend fun exists(key: String): Boolean {
        return withContext(Dispatchers.IO) {
            dao.existsByKey(key)
        }
    }

    override suspend fun insert(book: Book) {
        withContext(Dispatchers.IO) {
            // Превращаем Book в Entity и сохраняем
            dao.insertPost(book.toSavedEntity())
        }
    }

    override suspend fun deleteByKey(key: String): Int {
        return withContext(Dispatchers.IO) {
            dao.deleteByKey(key)
        }
    }
}


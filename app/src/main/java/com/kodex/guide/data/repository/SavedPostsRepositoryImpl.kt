package com.kodex.guide.data.repository

 import com.kodex.guide.data.source.local.SavedPostsLocalSource
import com.kodex.guide.domain.model.Book
import com.kodex.guide.domain.repository.SavedPostsRepo
 import kotlinx.coroutines.flow.Flow
 import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import javax.inject.Inject


class SavedPostsRepositoryImpl @Inject constructor(
    private val localSource: SavedPostsLocalSource
) : SavedPostsRepo {

    override fun observeSavedPosts(): Flow<List<Book>> {
        return localSource.observePosts()
    }

    override fun observeSavedKeys(): Flow<Set<String>> {
        return localSource.observePosts()
            .map { posts ->
                posts.map { it.key }.toSet()
            }
            .distinctUntilChanged()
    }

    override suspend fun isSaved(key: String): Boolean {
        return localSource.exists(key)
    }

    override suspend fun save(book: Book) {
        localSource.insert(book)
    }

    override suspend fun remove(key: String) {
        localSource.deleteByKey(key)
    }

    override suspend fun toggle(book: Book): Boolean {
        return if (localSource.exists(book.key)) {
            localSource.deleteByKey(book.key)
            false // Вернули false, так как удалили
        } else {
            localSource.insert(book)
            true // Вернули true, так как добавили
        }

    }
}
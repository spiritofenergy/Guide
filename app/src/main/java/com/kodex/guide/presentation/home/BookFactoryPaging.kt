package com.kodex.guide.presentation.home

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.kodex.guide.data.mapper.toBook
import com.kodex.guide.data.source.remote.FirebaseBooksDataSource
import com.kodex.guide.domain.model.Book
import com.kodex.guide.domain.model.BookFilterState
import com.kodex.guide.ui.db.MainDb
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import java.io.IOException
import javax.inject.Inject

class BookFactoryPaging @Inject constructor(
    private val dataSource: FirebaseBooksDataSource,
    private val keysFavsList: List<String>,
    private val bookFilter: BookFilterState,
    private val mainDb: MainDb
) : PagingSource<String, Book>() {

    override fun getRefreshKey(state: PagingState<String, Book>): String? {
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.key
        }
    }

    override suspend fun load(params: LoadParams<String>): LoadResult<String, Book> {
        return try {
            // Загружаем из Firebase
            val booksPageDTO = dataSource.nextPage(
                keysFavesList = keysFavsList,
                pageSize = params.loadSize.toLong(),
                currentKey = params.key,
                bookFilter = bookFilter,
            )
            val books = booksPageDTO.books.map { it.toBook() }

            // Кэшируем в Room
            if (!books.isEmpty()) {
                mainDb.trackDao().insertAllBooks(books)
            }

            // Проверяем что nextKey не дублируется
            val lastKey = books.lastOrNull()?.key
            val hasNext = lastKey != null && books.size >= params.loadSize
            
            LoadResult.Page(
                data = books,
                prevKey = null,
                nextKey = if (hasNext) lastKey else null
            )
        } catch (e: IOException) {
            // При ошибке сети показываем кэш
            val cachedBooks = try {
                mainDb.trackDao().getAllPosts().first()
            } catch (ex: Exception) {
                emptyList()
            }
            LoadResult.Page(
                data = cachedBooks,
                prevKey = null,
                nextKey = null
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}

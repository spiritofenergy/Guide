package com.kodex.guide.presentation.home

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.kodex.guide.data.mapper.toBook
import com.kodex.guide.data.source.remote.FirebaseBooksDataSource
import com.kodex.guide.domain.model.Book
import com.kodex.guide.domain.model.BookFilterState
import com.kodex.guide.ui.db.RoomDao
import java.io.IOException
import javax.inject.Inject

class BookFactoryPaging @Inject constructor(
    private val dataSource: FirebaseBooksDataSource,
    private val bookFilter: BookFilterState,
    private val dao: RoomDao?,  // ✅ Room DAO для сохранения
): PagingSource<String, Book>() {

    override fun getRefreshKey(state: PagingState<String, Book>)
    : String? {
        return null
    }

    override suspend fun load (params: LoadParams<String>)
    : LoadResult<String, Book> {
        try {
            val currentPage = params.key
            val booksPageDTO = dataSource.nextPage(
                pageSize = params.loadSize.toLong(),
                currentKey = currentPage,
                bookFilter = bookFilter,
            )
            val books = booksPageDTO.books.map { it.toBook() }
            
            // ✅ Сохраняем посты в Room
            dao?.let { dao ->
                try {
                    dao.insertAllBooks(books)
                    Log.d("MyLog", "Сохранено ${books.size} постов в Room")
                } catch (e: Exception) {
                    Log.e("MyLog", "Ошибка сохранения в Room: ${e.message}")
                }
            }
            
            return LoadResult.Page(
                data = books,
                prevKey = null,
                nextKey = booksPageDTO.listItemId
            )
        }catch (e: IOException){
            return LoadResult.Error(e)
        }
    }
}
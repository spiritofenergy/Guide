package com.kodex.guide.presentation.home

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.kodex.guide.data.mapper.toBook
import com.kodex.guide.data.source.remote.FirebaseBooksDataSource
import com.kodex.guide.domain.model.Book
import com.kodex.guide.domain.model.BookCategories
import com.kodex.guide.domain.model.BookFilterState
import com.kodex.guide.ui.db.RoomDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject

class BookFactoryPaging @Inject constructor(
    private val dataSource: FirebaseBooksDataSource,
    private val bookFilter: BookFilterState,
    private val dao: RoomDao?,  // ✅ Room DAO для сохранения
    private val externalScope: CoroutineScope // 👈 ДОБАВИЛИ СКОУП ДЛЯ ФОНОВЫХ ЗАДАЧ
): PagingSource<String, Book>() {


    override fun getRefreshKey(state: PagingState<String, Book>): String? {
        return null
    }

    override suspend fun load(params: LoadParams<String>): LoadResult<String, Book> {
        try {
            // ✅ 1. ЕСЛИ ЭТО ПЕРВАЯ ЗАГРУЗКА — ПРОБУЕМ ВЗЯТЬ ИЗ ROOM
            if (params.key == null) {
                val allLocalBooks = dao?.getAllPosts()?.firstOrNull() ?: emptyList()

                // ✅ 2. ФИЛЬТРУЕМ ЛОКАЛЬНЫЕ ДАННЫЕ ПОД ВЫБРАННУЮ КАТЕГОРИЮ
                val filteredLocalBooks = allLocalBooks.filter { book ->
                    val isCategoryMatch = bookFilter.category == BookCategories.ALL ||
                            book.categoryIndex == bookFilter.category.id

                    // Если у вас еще есть поиск по тексту, можно добавить и его:
                    val isSearchMatch = bookFilter.searchText.isEmpty() ||
                            book.title.contains(bookFilter.searchText, ignoreCase = true)

                    isCategoryMatch && isSearchMatch
                }
                if (filteredLocalBooks.isNotEmpty()) {
                    // Запускаем фоновую синхронизацию с Firebase, НЕ ЖДЕМ ЕЁ
                    externalScope.launch(Dispatchers.IO) {
                        try {
                            val booksPageDTO = dataSource.nextPage(
                                pageSize = params.loadSize.toLong(),
                                currentKey = null,
                                bookFilter = bookFilter,
                            )
                            val books = booksPageDTO.books.map { it.toBook() }

                            dao?.insertAllBooks(books) // Обновляем Room

                            // Вызываем invalidate(), чтобы Paging перечитал данные (теперь их станет больше)
                            invalidate()
                        } catch (e: Exception) {
                            Log.e("MyLog", "Ошибка фоновой синхронизации: ${e.message}")
                        }
                    }

                    // Мгновенно отдаем локальные данные в UI!
                    return LoadResult.Page(
                        data = filteredLocalBooks, // или просто localBooks, если DAO возвращает Book
                        prevKey = null,
                        nextKey = null
                    )
                }
            }

            // ✅ 2. ЕСЛИ В ROOM ПУСТО ИЛИ ЭТО СЛЕДУЮЩАЯ СТРАНИЦА — ГРУЗИМ ИЗ СЕТИ
            val currentPage = params.key
            val booksPageDTO = dataSource.nextPage(
                pageSize = params.loadSize.toLong(),
                currentKey = currentPage,
                bookFilter = bookFilter,
            )
            val books = booksPageDTO.books.map { it.toBook() }

            // Сохраняем в Room для следующих запусков
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
        } catch (e: IOException) {
            return LoadResult.Error(e)
        } catch (e: Exception) {
            return LoadResult.Error(e)
        }
    }
}

/*
    override fun getRefreshKey(state: PagingState<String, Book>): String? {
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
    }*/

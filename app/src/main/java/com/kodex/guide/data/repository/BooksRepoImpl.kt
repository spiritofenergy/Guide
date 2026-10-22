package com.kodex.guide.data.repository

import android.net.Uri
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.kodex.guide.data.images.BitmapEncoder
import com.kodex.guide.data.mapper.toBookDTO
import com.kodex.guide.data.mapper.toDTO
import com.kodex.guide.data.mapper.toRatingData
import com.kodex.guide.data.source.remote.FirebaseBooksDataSource
import com.kodex.guide.domain.model.Book
import com.kodex.guide.domain.model.BookCategories
import com.kodex.guide.domain.model.BookFilterState
import com.kodex.guide.domain.model.RatingData
import com.kodex.guide.domain.repository.BooksRepo
import com.kodex.guide.presentation.home.BookFactoryPaging
import com.kodex.guide.ui.db.MainDb
import kotlinx.coroutines.flow.*
import javax.inject.Inject

const val BASE_64 = true
class BooksRepoImpl @Inject constructor(
    private val dataSource: FirebaseBooksDataSource,
    private val bitmapEncoder: BitmapEncoder,
    private val mainDb: MainDb
): BooksRepo{
    override fun getBooks(favsKeysList: List<String>, bookFilter: BookFilterState): Flow<PagingData<Book>> {
        return Pager(
            config = PagingConfig(
                pageSize = 26,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                BookFactoryPaging(
                    dataSource = dataSource,
                    keysFavsList = favsKeysList,
                    bookFilter = bookFilter,
                    mainDb = mainDb
                )
            }
        ).flow
    }

    override fun getMyPostsLocal(uid: String): Flow<List<Book>> {
        return mainDb.trackDao().observeMyPosts(uid)
    }

    override suspend fun deleteBook(book: Book): Result<Unit> {
        return dataSource.deleteBook(book.toBookDTO())
    }
        // only BASE64 image
    override suspend fun saveBook(book: Book, uri: Uri?): Result<Unit> {
        val imageUrl = bitmapEncoder.imageToBase64(uri)
        return dataSource.saveBook(book.copy(imageUrl = imageUrl).toBookDTO())
    }

    override suspend fun submitUserRating(
        ratingData: RatingData,
        bookId: String
    ): Result<Unit> {
        return dataSource.submitUserRating(ratingData.toDTO(), bookId)
    }

    override suspend fun deleteComment(uid: String): Result<Unit> {
        return dataSource.deleteComment(uid)
    }

    override suspend fun getBookComments(bookId: String): Result<List<RatingData>> {
        return dataSource.getBookComments(bookId).map { list->
            list.map {
                rData -> rData.toRatingData()
            }
        }
    }

    override suspend fun getUserRating(bookId: String): Result<RatingData?> {
        return dataSource.getUserRating(bookId).map {
            it?.toRatingData()
        }
    }
}

package com.kodex.guide.domain.repository

import android.net.Uri
import androidx.paging.PagingData
import com.kodex.guide.domain.model.Banner
import com.kodex.guide.domain.model.Book
import com.kodex.guide.domain.model.BookFilterState
import com.kodex.guide.domain.model.RatingData
import kotlinx.coroutines.flow.Flow

interface  BooksRepo {
    fun getBooks(favsKeysList: List<String>, bookFilter: BookFilterState): Flow<PagingData<Book>>
    fun getBannersFlow(): Flow<List<Banner>>
    suspend fun saveBannersToLocalDb(banner: Banner): Result<Unit>
    suspend fun publishPostAndBanner(book:Book, uri: Uri?):Result<Unit>
    suspend fun deleteBook(book: Book): Result<Unit>
    suspend fun saveBook(book: Book, uri: Uri?): Result<Unit>
    suspend fun submitUserRating(ratingData: RatingData, bookId: String): Result<Unit>
    suspend fun deleteComment(uid: String): Result<Unit>
    suspend fun getBookComments(bookId: String): Result<List<RatingData>>
    suspend fun getUserRating(bookId: String): Result<RatingData?>

    }
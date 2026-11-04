package com.kodex.guide.domain.repository

import com.kodex.guide.domain.model.Book
import kotlinx.coroutines.flow.Flow

interface MyPostsRepo {
    fun observeMyPosts(uid: String): Flow<List<Book>>
    suspend fun getPost(key: String): Result<Book?>
    suspend fun saveSample(book: Book): Result<Book>   // Room, isUploaded = false
    suspend fun upload(book: Book): Result<Unit>      // Firebase + флаг true
    suspend fun delete(book: Book): Result<Unit>      // Room + Firebase (если был залит)
}
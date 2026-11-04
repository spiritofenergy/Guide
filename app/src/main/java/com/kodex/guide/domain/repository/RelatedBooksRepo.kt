package com.kodex.guide.domain.repository

import com.kodex.guide.domain.model.Book
import com.kodex.guide.domain.model.BookCategories

interface RelatedBooksRepo {
    suspend fun getRelatedBooks(categoryId: Int, excludeKey: String): Result<List<Book>>
}
package com.kodex.guide.data.repository

import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.kodex.guide.data.mapper.toBook
import com.kodex.guide.data.model.BookDTO
import com.kodex.guide.data.source.remote.FirebaseConst.CATEGORY_INDEX
import com.kodex.guide.data.source.remote.FirebaseConst.POSTS
import com.kodex.guide.domain.model.Book
import com.kodex.guide.domain.model.BookCategories
import com.kodex.guide.domain.repository.RelatedBooksRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.collections.map

class RelatedBooksRepositoryImpl @Inject constructor() : RelatedBooksRepo {

    override suspend fun getRelatedBooks(
        categoryId: Int,
        excludeKey: String
    ): Result<List<Book>> = runCatching {
        withContext(Dispatchers.IO) {
            val snapshot = Firebase.firestore
                .collection(POSTS)                            // ← как в вашем getBooks
                .whereEqualTo(CATEGORY_INDEX, categoryId)
                .limit(11)
                .get()
                .await()

            // ✅ ТОТ ЖЕ маппинг, что в BookFactoryPaging:
            snapshot.toObjects(BookDTO::class.java)
                .map { it.toBook() }

        }.filter { it.key != excludeKey }   // убираем текущий пост
        }
    }

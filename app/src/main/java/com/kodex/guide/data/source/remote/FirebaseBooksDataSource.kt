package com.kodex.guide.data.source.remote

 import com.google.firebase.auth.FirebaseAuth
 import com.google.firebase.firestore.FieldPath
 import com.google.firebase.firestore.FirebaseFirestore
 import com.google.firebase.firestore.Query
 import com.kodex.guide.data.mapper.toFirebaseFields
 import com.kodex.guide.data.model.BookDTO
 import com.kodex.guide.data.model.BooksPageDTO
 import com.kodex.guide.data.model.RatingDataDTO
 import com.kodex.guide.domain.model.BookCategories
 import com.kodex.guide.data.source.remote.FirebaseConst.CATEGORY_INDEX
 import com.kodex.guide.data.source.remote.FirebaseConst.KEY
 import com.kodex.guide.data.source.remote.FirebaseConst.MODERATION
 import com.kodex.guide.data.source.remote.FirebaseConst.POSTS
 import com.kodex.guide.data.source.remote.FirebaseConst.RATING
 import com.kodex.guide.data.source.remote.FirebaseConst.RATING_DATA
 import com.kodex.guide.data.source.remote.FirebaseConst.SEARCH_TITLE
 import com.kodex.guide.domain.model.BookFilterState
 import kotlinx.coroutines.tasks.await
 import javax.inject.Singleton

@Singleton
class FirebaseBooksDataSource(
    private val fireStore: FirebaseFirestore,
    private val auth: FirebaseAuth,

    ) {
    suspend fun nextPage(
        keysFavesList: List<String>,
        pageSize: Long,
        currentKey: String?,
        bookFilter: BookFilterState,
    ): BooksPageDTO{
        var query: Query = fireStore.collection(POSTS)
            .limit(pageSize)
            .orderBy(bookFilter.filterData.filterType.toFirebaseFields())

        query = when (bookFilter.category) {
            BookCategories.ALL -> query
            BookCategories.SERVICES -> query.whereIn(FieldPath.of(KEY), keysFavesList.ifEmpty { listOf("-1") })
            else -> query.whereEqualTo(CATEGORY_INDEX, bookFilter.category.id)
        }

        if (bookFilter.searchText.isNotEmpty()) {
            query = query.whereGreaterThanOrEqualTo(SEARCH_TITLE, bookFilter.searchText.lowercase())
                .whereLessThan(SEARCH_TITLE, "${bookFilter.searchText.lowercase()}\uF7FF")
        }

      /*  if (bookFilter.searchText.isNotEmpty()) {
            query = query.whereGreaterThanOrEqualTo(SEARCH_TITLE, bookFilter.searchText)
                .whereLessThan(SEARCH_TITLE, "${bookFilter.searchText}\uF7FF")
        }*/

        if (bookFilter.filterData.filterType.toFirebaseFields() == FirebaseConst.PRICE
            && bookFilter.filterData.minPrise != 0
            && bookFilter.filterData.maxPrise != 0
            && bookFilter.filterData.minPrise <= bookFilter.filterData.minPrise
            ) {
            query = query.whereGreaterThanOrEqualTo(FirebaseConst.PRICE, bookFilter.filterData.minPrise)
                .whereLessThanOrEqualTo(FirebaseConst.PRICE, bookFilter.filterData.maxPrise)
        }

        if (currentKey != null) {
            query = query.startAfter(currentKey)
        }
        val querySnapshot = query.get().await()
        val books = querySnapshot.toObjects(BookDTO::class.java)
        val updatedBooks = books.map {
            if (keysFavesList.contains(it.key)) {
                it.copy(isFavorite = true)
            } else {
                it
            }
        }
        return BooksPageDTO(updatedBooks, books.lastOrNull()?.title)
    }

    suspend fun saveBook(book: BookDTO): Result<Unit> {
       return try {
           val db = fireStore.collection(POSTS)
           val key = if (book.key.isEmpty()) db.document().id else book.key
           db.document(key)
               .set(
                   book.copy(key = key)).await()
           Result.success(Unit)
       }catch (e: Exception){
           Result.failure(e)
       }
   }
    suspend fun submitUserRating(ratingData: RatingDataDTO, bookId: String): Result<Unit> {
        if (auth.uid == null) return Result.failure(Throwable("User not authenticated"))
      return  try {
            fireStore.collection(MODERATION)
                .document(auth.uid!!)
                .set(ratingData.copy(
                    name = auth.currentUser?.email ?: "Unknown",
                    uid = auth.uid!!,
                    bookId = bookId
                )).await()
            Result.success(Unit)
        }catch (e: Exception){
            Result.failure(e)
        }
    }
    suspend fun deleteComment(uid: String): Result<Unit> {
       return try {
           fireStore.collection(MODERATION)
               .document(uid)
               .delete().await()
           Result.success(Unit)
       }catch (e: Exception){
           Result.failure(e)
       }
    }

    suspend fun getBookComments(bookId: String): Result<List<RatingDataDTO>> {
       return try {
           val querySnapshot = fireStore.collection(RATING)
               .document(bookId)
               .collection(RATING_DATA)
               .get().await()
           Result.success(querySnapshot.toObjects(RatingDataDTO::class.java))
       }catch (e: Exception){
           Result.failure(e)
       }
    }
    suspend fun getUserRating(bookId: String): Result<RatingDataDTO?> {
        if (auth.uid == null) return  Result.failure(Throwable("User not authenticated"))
       return try {
            val querySnapshot = fireStore.collection(RATING)
                .document(bookId)
                .collection(RATING_DATA)
                .document(auth.uid!!)
                .get().await()
            return  Result.success(querySnapshot.toObject(RatingDataDTO::class.java))
      }  catch (e:Exception){
            Result.failure(e)
        }
    }


    suspend fun deleteBook(book: BookDTO): Result<Unit> {
        return try {
            fireStore.collection(POSTS)
                .document(book.key)
                .delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
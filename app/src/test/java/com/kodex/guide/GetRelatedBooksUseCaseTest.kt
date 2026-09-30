package com.kodex.guide

import com.kodex.guide.domain.model.Book
import com.kodex.guide.domain.model.BookCategories
import com.kodex.guide.domain.repository.RelatedBooksRepo
import com.kodex.guide.domain.usecase.GetRelatedBooksUseCase
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class GetRelatedBooksUseCaseTest {

    // ✅ Fake вместо реального Firestore
    private class FakeRelatedBooksRepo : RelatedBooksRepo {
        var booksToReturn: List<Book> = emptyList()
        var shouldFail = false
        var capturedCategory: BookCategories? = null
        var capturedExcludeKey: String? = null

        override suspend fun getRelatedBooks(
            category: BookCategories,
            excludeKey: String
        ): Result<List<Book>> {
            capturedCategory = category
            capturedExcludeKey = excludeKey
            return if (shouldFail) {
                Result.failure(Throwable("network error"))
            } else {
                Result.success(booksToReturn)
            }
        }
    }

    private val fakeRepo = FakeRelatedBooksRepo()
    private val useCase = GetRelatedBooksUseCase(fakeRepo)

    @Test
    fun `возвращает не больше limit постов`() = runTest {
        fakeRepo.booksToReturn = (1..10).map { Book(key = "k$it") }

        val result = useCase(BookCategories.ELECTRONICS, excludeKey = "k1", limit = 3)

        assertTrue(result.isSuccess)
        assertEquals(3, result.getOrNull()?.size)
    }

    @Test
    fun `передает категорию и excludeKey в репозиторий`() = runTest {
        useCase(BookCategories.OTHER, excludeKey = "abc")

        assertEquals(BookCategories.OTHER, fakeRepo.capturedCategory)
        assertEquals("abc", fakeRepo.capturedExcludeKey)
    }

    @Test
    fun `ошибка репозитория пробрасывается наружу`() = runTest {
        fakeRepo.shouldFail = true

        val result = useCase(BookCategories.OTHER, excludeKey = "x")

        assertTrue(result.isFailure)
    }
}
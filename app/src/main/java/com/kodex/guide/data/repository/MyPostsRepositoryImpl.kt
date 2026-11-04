package com.kodex.guide.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.kodex.guide.data.mapper.MyPostMapper
import com.kodex.guide.data.mapper.toBannerDTO
import com.kodex.guide.data.mapper.toBannerEntity
import com.kodex.guide.data.mapper.toBookDTO
import com.kodex.guide.data.source.remote.FirebaseBooksDataSource
import com.kodex.guide.domain.model.Book
import com.kodex.guide.domain.repository.MyPostsRepo
import com.kodex.guide.domain.repository.UserRoleRepo
import com.kodex.guide.ui.db.MyPostDao
import com.kodex.guide.ui.db.RoomDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.util.UUID
import javax.inject.Inject

class MyPostsRepositoryImpl @Inject constructor(
    private val dao: MyPostDao,
    private val remote: FirebaseBooksDataSource,
    private val auth: FirebaseAuth,
    private val userRoleRepo: UserRoleRepo
) : MyPostsRepo {


    // ✅ Мапим список Entity в список Book через Flow
    override fun observeMyPosts(uid: String): Flow<List<Book>> =
        dao.getMyPosts()
            .map { entities -> entities.map { MyPostMapper.entityToDomain(it) } }
            .flowOn(Dispatchers.IO)

    // ✅ Достаем Entity и мапим в Book
    override suspend fun getPost(key: String): Result<Book?> = runCatching {
        withContext(Dispatchers.IO) {
            dao.getPostByKey(key)?.let { MyPostMapper.entityToDomain(it) }
        }
    }


    override suspend fun saveSample(book: Book): Result<Book> = runCatching {
        withContext(Dispatchers.IO) {
            val uid = auth.uid ?: throw IllegalStateException("Нужен вход в аккаунт")
            val key = book.key.ifEmpty { UUID.randomUUID().toString() }

            // Получаем роль (может упасть, если пользователя нет, оставьте как есть)
            val currentRole = userRoleRepo.observeUserRole(uid).first()

            val savedBook = book.copy(
                key = key,
                authorUid = uid,
                isAuthor = true,
                authorRole = currentRole.level,
                isUploaded = false,
                searchTitle = book.title.lowercase(),
                timeStamp = System.currentTimeMillis()
            )

            // ✅ Мапим Book в Entity и сохраняем
            dao.insertPost(MyPostMapper.domainToEntity(savedBook))
            savedBook
        }
    }


    override suspend fun upload(book: Book): Result<Unit> = runCatching {
        withContext(Dispatchers.IO) {
            val uid = auth.uid ?: throw IllegalStateException("Нужен вход в аккаунт")
            val currentRole = userRoleRepo.observeUserRole(uid).first()
            val published = book.copy(
                key = book.key.ifEmpty { UUID.randomUUID().toString() },
                authorUid = uid,
                isAuthor = true,
                authorRole = currentRole.level,
                isUploaded = true,
                searchTitle = book.title.lowercase(),
                timeStamp = System.currentTimeMillis()
            )
            // 1. Сохраняем пост в Firebase
            remote.saveBook(published.toBookDTO()).getOrThrow()
            // 2. Сохраняем пост в Room
            dao.insertPost(MyPostMapper.domainToEntity(published))
            // 3. Баннер — только если реально включён
            if (published.isBanner) {
                // ✅ Отправляем в Firebase правильный DTO с текстами
                remote.saveBannerToFirebase(published.toBannerDTO()).getOrThrow()

                // if (published.isBanner) {
             //   val bannerEntity = published.toBannerEntity()
                // dao.insertBanners(listOf(bannerEntity)) // Раскомментируйте, если этот метод есть
            //    remote.saveBannerToFirebase(published.toBookDTO())
            }
            // val bannerBookDto = published.toBookDTO()
            //  remote.saveBannerToFirebase(bannerBookDto)
            //remote.saveBannerToFirebase(published.toBookDTO())
        }
    }


    override suspend fun delete(book: Book): Result<Unit> = runCatching {
        withContext(Dispatchers.IO) {
            if (book.isUploaded) remote.deleteBook(book.toBookDTO()).getOrThrow()
            dao.deleteByKey(book.key)
        }
    }
}

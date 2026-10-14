package com.kodex.guide.data.repository

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.firestore
import com.kodex.guide.data.mapper.toDTO
import com.kodex.guide.data.source.local.PreferenceDataSource
import com.kodex.guide.data.source.remote.FirebaseAuthDataSource
import com.kodex.guide.domain.model.Permission
import com.kodex.guide.domain.model.User
import com.kodex.guide.domain.model.UserRole
import com.kodex.guide.domain.repository.AuthRepo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseAuthRepoImpl @Inject constructor(
    private val firebaseAuthDataSource: FirebaseAuthDataSource,
    private val preferenceDataSource: PreferenceDataSource   // ✅ добавили

) : AuthRepo {
    private var cachedUser: User? = null

    override suspend fun signUp(email: String, password: String): Result<User> {
        val result = firebaseAuthDataSource.signUp(email, password)
        result.onSuccess { preferenceDataSource.saveUser(it) }   // ✅
        return result
    }

    override suspend fun signIn(email: String, password: String): Result<User> {
        val result = firebaseAuthDataSource.signIn(email, password)
        result.onSuccess { preferenceDataSource.saveUser(it) }   // ✅
        return result
    }

    override suspend fun resetPassword(email: String): Result<Unit> {
        return firebaseAuthDataSource.resetPassword(email)
    }

    override suspend fun deleteAccount(
        email: String,
        password: String
    ): Result<Unit> {
        return firebaseAuthDataSource.deleteAccount(email, password)
    }

    override fun observeUser(): Flow<User?> = flow {
        emit(cachedUser)
    }

    override suspend fun signInAnonymously(): Result<User> {
        return try {
            // 1. Выполняем анонимный вход через Firebase
            val firebaseUser = firebaseAuthDataSource.signInAnonymously()
            val user = firebaseUser.toDomain()
            // 2. Создаем профиль пользователя в Firestore с ролью USER
            createUserProfile(user)

            // 3. Конвертируем в доменную модель и возвращаем
            Result.success(user)
        } catch (e: Exception) {
            // 4. Обрабатываем ошибки
            Result.failure(e)
        }
    }

    override suspend fun createUserProfile(user: User) {
        Firebase.firestore
            .collection("users")
            .document(user.uid)
            .set(user.copy(role = UserRole.USER).toDTO())
            .addOnSuccessListener {
                Log.d("MyLog", "Профиль создан для ${user.uid}")
            }
            .addOnFailureListener { e ->
                Log.e("MyLog", "Ошибка создания профиля: ${e.message}")
            }
    }

    override suspend fun getUserRole(uid: String): UserRole {
        return try {
            val doc = Firebase.firestore.collection("users")
                .document(uid)
                .get()
                .await()
            val roleString = doc.getString("role") ?: "USER"
            UserRole.valueOf(roleString)
        } catch (e: Exception) {
            UserRole.USER
        }
    }

    override fun hasPermission(permission: Permission): Boolean {
        val role = cachedUser?.role ?: UserRole.ANONYMOUS
        return permission.isGrantedBy(role)
    }

    // Маппинг Firebase User → Domain User
    private suspend fun FirebaseUser.toDomain(
        isAnonymous: Boolean = this.isAnonymous
    ): User {
        val role = getUserRole(uid)
        cachedUser = User(
            userName = displayName,
            uid = uid,
            email = email,
            role = role,
            isAnonymous = isAnonymous,
            isRegistered = !isAnonymous,

            )
        return cachedUser!!
    }

    override fun getCurrentUser(): User? {
        return firebaseAuthDataSource.getCurrentUser()
    }

    override fun signOut() {
        firebaseAuthDataSource.signOut()
        // Проверить работу этого кода
        preferenceDataSource.clearUserSession()                      // ✅

    }
}
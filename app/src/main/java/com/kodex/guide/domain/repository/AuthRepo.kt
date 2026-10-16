package com.kodex.guide.domain.repository

import com.kodex.guide.domain.model.Permission
import com.kodex.guide.domain.model.User
import com.kodex.guide.domain.model.UserRole
import kotlinx.coroutines.flow.Flow

interface AuthRepo {
    suspend fun signUp(email: String, password: String): Result<User>
    suspend fun signIn(email: String, password: String): Result<User>
    suspend fun resetPassword(email: String, ): Result<Unit>
    suspend fun deleteAccount(email: String, password: String ): Result<Unit>

    /** Поток текущего пользователя */
    fun observeUser(): Flow<User?>

    /** Гостевой вход */
    suspend fun signInAnonymously(): Result<User>

    /** ✅ Создание профиля в Firestore */
    suspend fun createUserProfile(user: User)

    /** ✅ Получение роли */
    suspend fun getUserRole(uid: String): UserRole

    /** Проверка прав */
    fun hasPermission(permission: Permission): Boolean

    fun getCurrentUser(): User?
    fun signOut()

}
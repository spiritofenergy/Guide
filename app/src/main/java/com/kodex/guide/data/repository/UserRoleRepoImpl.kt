package com.kodex.guide.data.repository

import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.kodex.guide.domain.model.UserRole
import com.kodex.guide.domain.repository.UserRoleRepo
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import kotlin.coroutines.resume

class UserRoleRepoImpl @Inject constructor() : UserRoleRepo {

    private val firestore = Firebase.firestore

    override fun observeUserRole(uid: String): Flow<UserRole> = callbackFlow {
        val registration = firestore.collection("users")
            .document(uid)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                if (snapshot == null || !snapshot.exists()) {
                    trySend(UserRole.USER)
                    return@addSnapshotListener
                }

                val roleString = snapshot.getString("role") ?: "USER"

                val role = runCatching {
                    UserRole.valueOf(roleString)
                }.getOrDefault(UserRole.USER)

                trySend(role)
            }

        awaitClose {
            registration.remove()
        }
    }

    override suspend fun updateUserRole(
        uid: String,
        role: UserRole
    ): Result<Unit> {
        // TODO: Здесь будет вызов Cloud Function для безопасности
        // Сейчас — имитация успешного обновления для тестирования
        return suspendCancellableCoroutine { continuation ->
            firestore.collection("users")
                .document(uid)
                .update("role", role.name)
                .addOnSuccessListener {
                    continuation.resume(Result.success(Unit))
                }
                .addOnFailureListener { error ->
                    continuation.resume(Result.failure(error))
                }
        }
    }
}
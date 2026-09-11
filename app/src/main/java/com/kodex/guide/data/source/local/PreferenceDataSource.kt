package com.kodex.guide.data.source.local

import android.content.Context
import androidx.core.content.edit
import com.kodex.guide.domain.model.PostDraftState
import com.kodex.guide.domain.model.User
import com.kodex.guide.domain.model.UserRole
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

const val POST_DRAFT_KEY = "post_draft_key"
const val CARD_NUMBER_KEY = "card_number_key"
const val CARD_EXPIRY_KEY = "card_expiry_key"


@Singleton
class PreferenceDataSource @Inject constructor(@ApplicationContext
    context: Context
) {
// В PreferenceDataSource.kt

    fun savePostDraft(draft: PostDraftState) {
        pref.edit {
            putString(POST_DRAFT_KEY, Json.encodeToString(draft))
        }
    }
    fun getPostDraft(): PostDraftState? {
        return try {
            val json = pref.getString(POST_DRAFT_KEY, null) ?: return null
            Json.decodeFromString(json)
        } catch (e: Exception) {
            null
        }
    }

    fun clearPostDraft() {
        pref.edit { remove(POST_DRAFT_KEY) }
    }
  /*  // ✅ Сохранение черновика
    fun savePostDraft1(draft: PostDraftState) {
        pref.edit {
            putString(POST_DRAFT_KEY, Json.encodeToString(PostDraftState.serializer(), draft))
        }
    }

    // ✅ Загрузка черновика
    fun getPostDraft1(): PostDraftState? {
        return try {
            val json = pref.getString(POST_DRAFT_KEY, null) ?: return null
            Json.decodeFromString(PostDraftState.serializer(), json)
        } catch (e: Exception) {
            null
        }
    }*/

    // ✅ Очистка черновика после успешной публикации
    fun clearPostDraft1() {
        pref.edit { remove(POST_DRAFT_KEY) }
    }
    //  ключи карты

    // ===== Сохранение данных карты (кроме CVV!) =====
    fun saveCardData(cardNumber: String, expiry: String) {
        pref.edit {
            putString(CARD_NUMBER_KEY, cardNumber)
            putString(CARD_EXPIRY_KEY, expiry)
        }
    }

    fun getSavedCardNumber(): String =
        pref.getString(CARD_NUMBER_KEY, "") ?: ""

    fun getSavedCardExpiry(): String =
        pref.getString(CARD_EXPIRY_KEY, "") ?: ""

    fun hasSavedCard(): Boolean = getSavedCardNumber().isNotEmpty()

    fun clearCardData() {
        pref.edit {
            remove(CARD_NUMBER_KEY)
            remove(CARD_EXPIRY_KEY)
        }
    }
    private val pref = context.getSharedPreferences(MAIN_PREF, Context.MODE_PRIVATE)

    fun saveEmail(key: String, value: String){
        pref.edit { putString(key, value) } }
    fun saveName(key: String, value: String){
        pref.edit { putString(key, value) } }
    fun savePhone(key: String, value: String){
        pref.edit { putString(key, value) } }
    fun savePassword(key: String, value: String){
        pref.edit { putString(key, value) } }

    fun getEmail(key: String, defValue: String): String{
        return pref.getString(key, defValue)?: defValue }
    fun getName(key: String, defValue: String): String{
        return pref.getString(key, defValue)?: defValue }
    fun getPhone(key: String, defValue: String): String{
        return pref.getString(key, defValue)?: defValue }
    fun getPassword(key: String, defValue: String): String{
        return pref.getString(key, defValue)?: defValue
    }
    // ===== ✅ НОВОЕ: кеш текущего профиля для DrawerHeader =====
    fun saveUser(user: User) {
        pref.edit {
            putString(UID_KEY, user.uid)
            putString(EMAIL_KEY, user.email.orEmpty())
            putString(NAME_KEY, user.userName.orEmpty())
            putString(ROLE_KEY, user.role.name)
            putBoolean(IS_ANONYMOUS_KEY, user.isAnonymous)
            putBoolean(IS_REGISTERED_KEY, user.isRegistered)
        }
    }
    fun saveRole(role: UserRole) {
        pref.edit { putString(ROLE_KEY, role.name) }
    }

    fun getUser(): User? {
        val uid = pref.getString(UID_KEY, null) ?: return null
        return User(
            uid = uid,
            email = pref.getString(EMAIL_KEY, ""),
            userName = pref.getString(NAME_KEY, ""),
            role = runCatching {
                UserRole.valueOf(pref.getString(ROLE_KEY, UserRole.USER.name)!!)
            }.getOrDefault(UserRole.USER),
            isAnonymous = pref.getBoolean(IS_ANONYMOUS_KEY, false),
            isRegistered = pref.getBoolean(IS_REGISTERED_KEY, false)
        )
    }

    /** Сброс кеша сессии при выходе (email/пароль для автологина НЕ трогаем) */
    // Проверить работу этого кода
    fun clearUserSession() {
        pref.edit {
            remove(UID_KEY)
            remove(ROLE_KEY)
            remove(IS_ANONYMOUS_KEY)
            remove(IS_REGISTERED_KEY)
        }
    }
    companion object{
        const val MAIN_PREF = "main_pref"
        const val EMAIL_KEY = "email_key"
        const val NAME_KEY = "name_key"
        const val PHONE_KEY = "telephone_key"
        const val PASSWORD_KEY = "password_key"
        const val UID_KEY = "uid_key"
        const val ROLE_KEY = "role_key"
        const val IS_ANONYMOUS_KEY = "is_anonymous_key"
        const val IS_REGISTERED_KEY = "is_registered_key"
    }
    }

package com.kodex.guide.data.source.local

import android.content.Context
import androidx.core.content.edit
import com.kodex.guide.domain.model.PostSampleState
import com.kodex.guide.domain.model.User
import com.kodex.guide.domain.model.UserRole
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton


// ✅ Переименовали ключ с POST_DRAFT_KEY на SAMPLE_AD_KEY
const val SAMPLE_AD_KEY = "sample_ad_key"
const val CARD_NUMBER_KEY = "card_number_key"
const val CARD_EXPIRY_KEY = "card_expiry_key"
const val SHOW_DRAWER_KEY = "show_drawer_key"

@Singleton
class PreferenceDataSource @Inject constructor(
    @ApplicationContext context: Context
) {
    private val pref = context.getSharedPreferences(MAIN_PREF, Context.MODE_PRIVATE)

    // ===== ОБРАЗЕЦ ОБЪЯВЛЕНИЯ (ранее черновик) =====

    // Сохранить образец
    fun saveSampleAd(ad: PostSampleState) {
        pref.edit {
            putString(SAMPLE_AD_KEY, Json.encodeToString(ad))
        }
    }

    // Получить образец
    fun getSampleAd(): PostSampleState? {
        return try {
            val json = pref.getString(SAMPLE_AD_KEY, null) ?: return null
            Json.decodeFromString(json)
        } catch (e: Exception) {
            null
        }
    }

    // Очистить образец (например, после успешной публикации)
    fun clearSampleAd() {
        pref.edit { remove(SAMPLE_AD_KEY) }
    }

    // ===== ДАННЫЕ КАРТЫ =====
    fun saveCardData(cardNumber: String, expiry: String) {
        pref.edit {
            putString(CARD_NUMBER_KEY, cardNumber)
            putString(CARD_EXPIRY_KEY, expiry)
        }
    }

    fun getSavedCardNumber(): String = pref.getString(CARD_NUMBER_KEY, "") ?: ""
    fun getSavedCardExpiry(): String = pref.getString(CARD_EXPIRY_KEY, "") ?: ""
    fun hasSavedCard(): Boolean = getSavedCardNumber().isNotEmpty()

    fun clearCardData() {
        pref.edit {
            remove(CARD_NUMBER_KEY)
            remove(CARD_EXPIRY_KEY)
        }
    }

    // ===== ПОЛЬЗОВАТЕЛЬ =====
    fun saveEmail(key: String, value: String) { pref.edit { putString(key, value) } }
    fun saveName(key: String, value: String) { pref.edit { putString(key, value) } }
    fun savePhone(key: String, value: String) { pref.edit { putString(key, value) } }
    fun savePassword(key: String, value: String) { pref.edit { putString(key, value) } }

    fun getEmail(key: String, defValue: String): String = pref.getString(key, defValue) ?: defValue
    fun getName(key: String, defValue: String): String = pref.getString(key, defValue) ?: defValue
    fun getPhone(key: String, defValue: String): String = pref.getString(key, defValue) ?: defValue
    fun getPassword(key: String, defValue: String): String = pref.getString(key, defValue) ?: defValue

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

    fun clearUserSession() {
        pref.edit {
            remove(UID_KEY)
            remove(ROLE_KEY)
            remove(IS_ANONYMOUS_KEY)
            remove(IS_REGISTERED_KEY)
        }
    }

    // ===== НАСТРОЙКИ UI =====
    fun saveShowDrawer(show: Boolean) {
        pref.edit { putBoolean(SHOW_DRAWER_KEY, show) }
    }

    fun getShowDrawer(): Boolean = pref.getBoolean(SHOW_DRAWER_KEY, true)

    companion object {
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
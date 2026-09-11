package com.kodex.guide.domain.model

import kotlinx.serialization.Serializable

/**
 * Состояние черновика объявления для сохранения в SharedPreferences.
 * Соответствует SRP: хранит только данные, необходимые для восстановления формы.
 */
@Serializable
data class PostDraftState(
    val title: String = "",
    val description: String = "",
    val price: Int = 0,
    val telephone: String = "",
    val village: String = "",
    val street: String = "",
    val house: String = "",
    val flat: String = "",
    val categoryId: Int = 0, // Храним ID категории вместо объекта BookCategories
    val delivery: Boolean = false,
    val payment: Boolean = false,
    val location: Boolean = false,
    val imageUrl: String = "" // Base64 или URL
)
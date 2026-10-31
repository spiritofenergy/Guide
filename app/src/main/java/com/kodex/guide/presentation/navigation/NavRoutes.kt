package com.kodex.guide.presentation.navigation

import com.kodex.guide.domain.model.BookCategories
import kotlinx.serialization.Serializable

@Serializable
open class NavRoutes {

    @Serializable
    data class MyPostsNavObject(val uid: String = "")

    @Serializable
    data class MyPostEditorNavObject(val bookKey: String = "")

    @Serializable
    data class HomeDataObject(val uid: String = "", val email: String?)

    @Serializable
    object AdminPanelNavObject {}

    @Serializable
    object ModerationNavObject

    @Serializable
    object LoginNavObject

    @Serializable
    object SingUpNavObject

    @Serializable
    object SettingsNavObject

    @Serializable
    data class DetailNavObject(
        val bookId: String = "",
        val title: String = "",
        val description: String = "",
        val price: Int = 0,
        val isOpenNow: Boolean = true,
        val openingHours: String = "09:00 - 20:00",
        val address: String = "Кучугуры",
        val apartment: String? = null,
        val telephone: String = "+7(495)123-45-67",
        val website: String = "coffeehouse.ru",
        val village: String = "",
        val street: String = "ул. Ленина",
        val house: String = "14",
        val flat: String = "22",
        val location:  Boolean = false,
        val delivery: Boolean = false,
        val payment: Boolean = false,
        val latitude: String? = null,
        val longitude: String? = null,
        val categoryIndex: BookCategories = BookCategories.OTHER,
        val imageUrl: String = "",
        val author: String = "",
        val timestamp: Long = System.currentTimeMillis(),
        val isFaves: Boolean = false,
        val ratingsList: List<Int> = emptyList(),
        val hasDelivery: Boolean = true,
        val acceptsCard: Boolean = true,
        val publishPeriod: Int = 1
    )

    fun DetailNavObject.toCommentsNavData(): CommentsNavData {
        return CommentsNavData(
            bookId = bookId,
            title = title,
            ratingsList = ratingsList
        )
    }


    @Serializable
    data class CommentsNavData(
        val bookId: String = "",
        val title: String = "",
        val ratingsList: List<Int> = emptyList(),

        )

}
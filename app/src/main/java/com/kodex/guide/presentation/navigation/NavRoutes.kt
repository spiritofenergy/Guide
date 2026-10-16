package com.kodex.guide.presentation.navigation

import com.kodex.guide.domain.model.BookCategories
import kotlinx.serialization.Serializable

@Serializable
class NavRoutes {


    @Serializable
    data class MyPostsNavObject(
        val uid: String = ""
    )

    @Serializable
    data class MyPostEditorNavObject(
        val bookKey: String = ""
    )

    @Serializable
    data class HomeDataObject(
        val uid: String = "",
        val email: String?
    )

    @Serializable
    object AdminPanelNavObject {
    }

    @Serializable
    object ModerationNavObject

    @Serializable
    object LoginNavObject

    @Serializable
    object SingUpNavObject

    @Serializable
    object SettingsNavObject

    @Serializable
    data class ParallaxNavObject(
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
        val latitude: String = "",
        val longitude: String = "",
        val categoryIndex: BookCategories = BookCategories.OTHER,
        val imageUrl: String = "",
        val author: String = "",
        val timestamp: Long = System.currentTimeMillis(),
        val isFaves: Boolean = false,
        val ratingsList: List<Int> = emptyList(),
        val hasDelivery: Boolean = true,
        val acceptsCard: Boolean = true,
        val publishPeriod: Int = 1 )
                @Serializable
                data class ParallaxNavObject1(
                    val bookId: String = "",
                    val title: String = "",
                    val address: String = "",
                    val street: String = "",           //  Улица
                    val house: String = "",            // 🆕 Дом
                    val apartment: String? = null,     //  Квартира (опционально)
                    val price: Int = 0,
                    val imageUrl: String = "",
                    val isOpenNow: Boolean = true,
                    val openingHours: String = "",
                    val telephone: String = "",
                    val website: String = "",
                    val latitude: String = "",
                    val longitude: String = "",
                    val description: String = "",
                    val categoryIndex: BookCategories = BookCategories.OTHER,
                    val ratingsList: List<Int> = emptyList(),
                    val hasDelivery: Boolean = false,  // 🆕 Наличие доставки
                    val acceptsCard: Boolean = false   // 🆕 Оплата картой
        )


    @Serializable
    data class DetailNavObject(
        val bookId: String = "",
        val title: String = "",
        val description: String = "",
        val price: String = "",
        val telephone: String = "",
        val categoryIndex: BookCategories = BookCategories.OTHER,
        val imageUrl: String = "",
        val author: String = "",
        val timestamp: Long = System.currentTimeMillis(),
        val isFaves: Boolean = false,
        val ratingsList: List<Int> = emptyList()
    )

    @Serializable
    data class PlaceNavObject(
        val bookId: String = "",
        val title: String = "Уютный уголок",
        val description: String = "Уютная кофейня в центре города. Мы предлагаем свежую выпечку, авторский кофе и приятную атмосферу для работы и встреч с друзьями. У нас есть бесплатный Wi-Fi, розетки и вежливый персонал.",
        val price: Int = 0,
        val categoryIndex: BookCategories = BookCategories.OTHER,
        val imageUrl: String = "",
        val author: String = "",
        val address: String = "ул. Центральная, 15, Москва",
        val isOpen: Boolean = true,
        val workTime: String = "09:00 - 22:00",
        val contact: String = "info@cozyplace.ru",
        val telephone: String = "+7 (999) 123-45-67",
        val site: String = "https://cozyplace.ru",
        val timestamp: Long = System.currentTimeMillis(),
        val isFaves: Boolean = false,
        val ratingsList: List<Int> = emptyList()

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
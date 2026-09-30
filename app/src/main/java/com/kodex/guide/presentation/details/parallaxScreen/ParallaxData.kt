package com.kodex.guide.presentation.details.parallaxScreen

import com.kodex.guide.domain.model.BookCategories


data class ParallaxData (
    val bookId: String = "",
    val title: String = "",
    val address: String,
    val rating: Double,
    val reviewsCount: Int,
    val price: Int = 0,
    val imageUrl: String = "",
    val isOpenNow: Boolean,
    val openingHours: String,
    val telephone: String,
    val website: String,
    val amenities: List<String>,
    val latitude: Double,
    val longitude: Double,

    val description: String = "",
    val categoryIndex: Int = BookCategories.OTHER.id,
    val author: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val isFaves: Boolean = false,
    val ratingsList: List<Int> = emptyList()


)
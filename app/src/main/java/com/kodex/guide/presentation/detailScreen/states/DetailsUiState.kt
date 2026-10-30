package com.kodex.guide.presentation.detailScreen.states

import com.kodex.guide.domain.model.Book
import com.kodex.guide.domain.model.RatingData

data class DetailsUiState(
    val isLoading: Boolean = false,
    val comments: List<RatingData> = emptyList(),
    val ratingData: RatingData = RatingData(),
    val error: String? = null,

    val book: Book? = null,
    val showRateDialog: Boolean = false,
    val ratingDataToShow: RatingData = RatingData(),
    val showCommentDialog: Boolean = false,


    )
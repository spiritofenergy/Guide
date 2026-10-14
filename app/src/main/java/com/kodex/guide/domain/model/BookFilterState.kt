package com.kodex.guide.domain.model


data class BookFilterState(
    val category: BookCategories = BookCategories.ALL,
    val author: String? = null,
    val searchText: String = "",
    val filterData: FilterData = FilterData()

) {
    companion object {
        fun Category(
            category: BookCategories,
            author: String? = null
        ): BookFilterState = BookFilterState(
            category = category,
            author = author
        )
    }
}
package com.kodex.guide.data.model

import com.kodex.guide.domain.model.BookCategories
import com.kodex.guide.domain.model.FilterData

data class BookFilter(
    var category: BookCategories = BookCategories.OTHER,
    var searchText: String = "",
    var filterData: FilterData = FilterData()
)

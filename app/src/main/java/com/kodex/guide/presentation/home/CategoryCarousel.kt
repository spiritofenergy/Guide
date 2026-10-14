package com.kodex.guide.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kodex.bookmarketcompose.R
import com.kodex.guide.domain.model.BookCategories
import com.kodex.guide.ui.theme.ButtonColorBlue
import com.kodex.guide.ui.theme.Orange

@Composable
fun CategoryCarousel(
    selectedCategory: BookCategories,
    onCategoryClick: (BookCategories) -> Unit
) {
    val context = LocalContext.current
    val categoryList = context.resources.getStringArray(R.array.category_drawer).toList()
    
    // Filter out duplicates and "All" entries (keep only unique categories)
    val filteredCategories = categoryList.take(12) // First 12 items (all categories including Construction and Food)

    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 8.dp)
    ) {
        itemsIndexed(filteredCategories) { index, categoryName ->
            val isSelected = selectedCategory.id == index
            CategoryChip(
                label = categoryName,
                isSelected = isSelected,
                onClick = {
                    val category = BookCategories.entries.getOrNull(index)
                    if (category != null) {
                        onCategoryClick(category)
                    }
                }
            )
        }
    }
}

@Composable
fun CategoryChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .height(36.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(if (isSelected) Orange else Color.LightGray.copy(alpha = 0.5f))
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp)
    ) {
        Text(
            text = label,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            fontSize = 14.sp,
            color = if (isSelected) Color.White else Color.DarkGray,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

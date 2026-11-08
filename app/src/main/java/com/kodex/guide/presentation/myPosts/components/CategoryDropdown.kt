package com.kodex.guide.presentation.myPosts.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.kodex.guide.domain.model.BookCategories


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryDropdown(
    category: BookCategories,
    expanded: Boolean,
    names: Array<String>,
    onCategoryChange: (BookCategories) -> Unit,
    onExpandedChange: (Boolean) -> Unit
) {
    val availableCategories = BookCategories.entries.filter { it.id in names.indices }

    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = onExpandedChange) {
        OutlinedTextField(
            value = names.getOrNull(category.id) ?: names.lastOrNull().orEmpty(),
            onValueChange = {},
            readOnly = true,
            label = { Text("Категория") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { onExpandedChange(false) }) {
            availableCategories.forEach { c ->
                DropdownMenuItem(text = { Text(names[c.id]) }, onClick = { onCategoryChange(c) })
            }
        }
    }
}

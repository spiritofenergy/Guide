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


// Выпадающий список периода публикации
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PublishPeriodDropdown(
    period: Int,
    expanded: Boolean,
    names: Array<String>,
    onPeriodChange: (Int) -> Unit,
    onExpandedChange: (Boolean) -> Unit
) {
    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = onExpandedChange) {
        OutlinedTextField(
            value = names.getOrNull(period - 1) ?: names.first(),
            onValueChange = {},
            readOnly = true,
            label = { Text("Период публикации") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { onExpandedChange(false) }) {
            names.forEachIndexed { index, name ->
                DropdownMenuItem(text = { Text(name) }, onClick = { onPeriodChange(index + 1) })
            }
        }
    }
}
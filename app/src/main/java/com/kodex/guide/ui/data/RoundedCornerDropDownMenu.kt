package com.kodex.guide.ui.data

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun RoundedCornerDropDownMenu(
    option: List<String>,
    selectedOption: String,
    onOptionSelected: (Int) -> Unit,
 ) {
    val expanded = remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(Color.White)
            .clickable {
                expanded.value = true
            }
            .padding(20.dp)

    ) {
        Text(text = selectedOption)

        DropdownMenu(expanded = expanded.value,
            onDismissRequest = {
                expanded.value = false
            }) {
            option.forEachIndexed { index, title ->
                DropdownMenuItem(text = {
                        Text(text = title)
                    }, onClick = {
                        expanded.value = false
                        onOptionSelected(index)
                    })
            }
        }
    }
}
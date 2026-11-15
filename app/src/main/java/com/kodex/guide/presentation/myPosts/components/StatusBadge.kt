package com.kodex.guide.presentation.myPosts.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kodex.guide.ui.theme.Orange

@Composable
fun StatusBadge(isUploaded: Boolean) {
    val bg = if (isUploaded) Color(0xFF10B981).copy(alpha = 0.15f)
    else Orange.copy(alpha = 0.15f).copy(alpha = 0.15f)
    val text = if (isUploaded) "Опубликован" else "Образец"
    val color = if (isUploaded) Color(0xFF10B981) else Color(0xFFF59E0B)
    Surface(shape = MaterialTheme.shapes.small, color = bg) {
        Text(
            text = text,
            fontSize = 11.sp,
            color = color,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

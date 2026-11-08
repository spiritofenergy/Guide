package com.kodex.guide.presentation.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kodex.guide.ui.theme.Orange
import com.kodex.guide.ui.theme.Business

@Composable
fun StatusChip(isOpen: Boolean) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = if (isOpen) Business.copy(alpha = 0.1f) else Orange.copy(alpha = 0.1f)
    ) {
        Text(
            text = if (isOpen) "Открыто" else "Закрыто",
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = if (isOpen) Business else Orange
        )
    }
}

@Composable
fun DeliveryChip() {
    Surface(shape = RoundedCornerShape(16.dp), color = Business.copy(alpha = 0.1f)) {
        Text(
            text = "Доставка",
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = Business
        )
    }
}
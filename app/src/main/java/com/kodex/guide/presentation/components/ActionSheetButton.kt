package com.kodex.guide.presentation.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ActionSheetButton(
    text: String,
    icon: ImageVector? = null,
    containerColor: Color,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    loadingText: String? = null,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(52.dp),
        enabled = enabled,
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = Color.White
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                color = Color.White,
                strokeWidth = 2.dp
            )
            if (loadingText != null) {
                Spacer(Modifier.width(12.dp))
            }
        } else if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Spacer(Modifier.width(12.dp))
        }
        Text(
            text = if (isLoading) loadingText ?: text else text,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

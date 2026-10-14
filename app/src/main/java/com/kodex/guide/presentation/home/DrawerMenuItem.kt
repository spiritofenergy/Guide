package com.kodex.guide.presentation.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun DrawerMenuItem(
    iconDrawableId: ImageVector,
    text: String,
    suffixText: String? = null,
    onItemClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onItemClick() }
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = iconDrawableId,
            contentDescription = null,
        )
        Spacer(modifier = Modifier.width(14.dp))
        Text(
            text = text,
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(1f)
        )
        if (suffixText != null) {
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = suffixText,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Gray
            )
        }
    }
}

@Composable
@Preview(showBackground = true)
fun DrawerListItemPreview() {
}

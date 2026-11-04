package com.kodex.guide.presentation.settingsScreen.menuItems

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kodex.guide.ui.theme.ButtonColorBlue
import com.kodex.guide.ui.theme.DrawerColorBlue
import androidx.core.net.toUri


@Composable
fun DeveloperInfoFooter() {
    val context = LocalContext.current
    val phone = "+7 (999) 411-22-36"
    val phoneDisplay = "+7 (999) 411-22-36"

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(Color.LightGray.copy(alpha = 0.4f))
        )
        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Разработчик: ООО «Кодекс»",
            fontSize = 12.sp,
            color = Color.Gray
        )
        Spacer(modifier = Modifier.height(2.dp))

        Text(
            text = "ИНН: 561401001", // TODO: заменить на фактический ИНН ООО «Кодекс»
            fontSize = 12.sp,
            color = Color.Gray
        )
        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "Горячая линия: $phoneDisplay",
            fontSize = 13.sp,
            color = DrawerColorBlue,
            fontWeight = FontWeight.Medium,
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .clickable {
                    val intent = android.content.Intent(
                        android.content.Intent.ACTION_DIAL,
                        "tel:$phone".toUri()
                    )
                    context.startActivity(intent)
                }
                .padding(horizontal = 8.dp, vertical = 4.dp)
        )
        Spacer(modifier = Modifier.height(36.dp))
    }
}

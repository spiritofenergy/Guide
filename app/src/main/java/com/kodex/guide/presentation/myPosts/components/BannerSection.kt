package com.kodex.guide.presentation.myPosts.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kodex.guide.ui.theme.ButtonColorBlue


// Секция настройки баннера
@Composable
fun BannerSection(
    isBanner: Boolean,
    onIsBannerChange: (Boolean) -> Unit,
    bannerTextTopLeft: String,
    onBannerTextTopLeftChange: (String) -> Unit,
    bannerTextTopRight: String,
    onBannerTextTopRightChange: (String) -> Unit,
    bannerTextBottomLeft: String,
    onBannerTextBottomLeftChange: (String) -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isBanner) ButtonColorBlue.copy(alpha = 0.1f) else MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            // Переключатель баннера
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Режим баннера",
                    style = MaterialTheme.typography.titleMedium,
                    color = if (isBanner) ButtonColorBlue else MaterialTheme.colorScheme.onSurface
                )
                Switch(
                    checked = isBanner,
                    onCheckedChange = onIsBannerChange,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = ButtonColorBlue,
                        checkedTrackColor = ButtonColorBlue.copy(alpha = 0.3f)
                    )
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Поля баннера (показываем только если включен)
            if (isBanner) {
                OutlinedTextField(
                    value = bannerTextTopLeft,
                    onValueChange = onBannerTextTopLeftChange,
                    label = { Text("Текст слева сверху") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = bannerTextTopRight,
                    onValueChange = onBannerTextTopRightChange,
                    label = { Text("Ткст справа сверху") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = bannerTextBottomLeft,
                    onValueChange = onBannerTextBottomLeftChange,
                    label = { Text("Текст слева снизу") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }
        }
    }
}


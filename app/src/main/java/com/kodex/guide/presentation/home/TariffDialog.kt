package com.kodex.guide.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.kodex.guide.domain.model.UserRole
import com.kodex.guide.ui.theme.GreenSea
import com.kodex.guide.ui.theme.Premium
import com.kodex.guide.ui.theme.TextGray

@Composable
fun TariffDialog(
    currentRoleName: String,
    nextRoleName: String?,
    premiumRoleName: String = "Премиум",
    nextRequiresPayment: Boolean,
    showNextButton: Boolean,
    showBusinessButton: Boolean,
    showPremiumButton: Boolean,
    showGuestButton: Boolean,
    onDismiss: () -> Unit,
    onNextTariffClick: () -> Unit,
    onBusinessClick: () -> Unit,
    onPremiumClick: () -> Unit,
    onLogoutClick: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Ваш тариф",
                style = MaterialTheme.typography.headlineSmall
            )
        },
        text = {
            Column {
                Text(
                    text = "Текущий тариф: $currentRoleName",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(12.dp))

                if (nextRoleName != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Доступно повышение до: $nextRoleName",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (nextRequiresPayment) {
                            "Для перехода требуется оплата"
                        } else {
                            "Для перехода требуется регистрация"
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                } else {
                    Text(
                        text = "У вас максимальный тариф",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                }
            }
        },
        confirmButton = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            ) {
                // Бесплатное повышение (серая кнопка)
                if (showNextButton && nextRoleName != null) {
                    Button(
                        onClick = onNextTariffClick,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = TextGray.copy(alpha = 0.4f))
                    ) {
                        Text("Перейти на «$nextRoleName»")
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }

                // BUSINESS — зелёная кнопка
                if (showBusinessButton) {
                    Button(
                        onClick = onBusinessClick,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = GreenSea)
                    ) {
                        Text("Перейти на «Бизнес»")
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }

                // PREMIUM — фиолетовая кнопка
                if (showPremiumButton) {
                    Button(
                        onClick = onPremiumClick,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Premium)
                    ) {
                        Text("Перейти на «$premiumRoleName»")
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (showGuestButton) {
                        TextButton(onClick = onLogoutClick) {
                            Text("Гостевой")
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                    TextButton(onClick = onDismiss) {
                        Text("Отмена")
                    }
                }
            }
        },
        dismissButton = {}
    )
}

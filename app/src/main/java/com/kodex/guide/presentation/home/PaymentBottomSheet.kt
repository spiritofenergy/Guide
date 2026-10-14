package com.kodex.guide.presentation.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kodex.guide.domain.model.UserRole
import com.kodex.guide.ui.theme.ButtonColorBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentBottomSheet(
    viewModel: HomeViewModel,
    onDismiss: () -> Unit
) {
    val targetRole = viewModel.desiredRole.value ?: UserRole.BUSINESS
    val targetRoleName = viewModel.getRoleDisplayName(targetRole)
    val price = if (targetRole == UserRole.PREMIUM) "450 ₽ / месяц" else "150 ₽ / месяц"

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Оплата «$targetRoleName»",
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Стоимость: $price",
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Оплата временно недоступна: платёжный сервис ещё не подключён. Тариф не будет изменён.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.error
            )
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = ButtonColorBlue)
            ) {
                Text("Закрыть")
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

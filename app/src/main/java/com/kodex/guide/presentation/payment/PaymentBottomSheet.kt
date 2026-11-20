package com.kodex.guide.presentation.payment

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding // ✅ ДОБАВЛЕН ИМПОРТ
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.kodex.guide.domain.model.UserRole
import com.kodex.guide.presentation.home.HomeViewModel
import com.kodex.guide.ui.theme.ColorBlue
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentBottomSheet(
    viewModel: HomeViewModel,
    onDismiss: () -> Unit,
    isLoading: Boolean = false,
    onPaymentSuccess: () -> Unit
) {
    val targetRole = viewModel.desiredRole.value ?: UserRole.BUSINESS
    val targetRoleName = viewModel.getRoleDisplayName(targetRole)

    val basePrice = when (targetRole) {
        UserRole.BUSINESS -> 350
        UserRole.PREMIUM -> 2500
        else -> 0
    }

    val coroutineScope = rememberCoroutineScope()
    var cardNumber by remember { mutableStateOf("") }
    var expiry by remember { mutableStateOf("") }
    var cvv by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val isFormValid = cardNumber.length == 16 && expiry.length == 5 && cvv.length == 3
    var saveCard by remember { mutableStateOf(false) }

    val textFieldColors = OutlinedTextFieldDefaults.colors(
        focusedContainerColor = Color.White,
        unfocusedContainerColor = Color.White,
        disabledContainerColor = Color.White,
        focusedBorderColor = ColorBlue,
        unfocusedBorderColor = Color.Gray
    )

    LaunchedEffect(Unit) {
        val (savedNumber, savedExpiry) = viewModel.getSavedCardData()
        if (savedNumber.isNotEmpty()) {
            cardNumber = savedNumber
            expiry = savedExpiry
            saveCard = true
        }
    }

    ModalBottomSheet(
        onDismissRequest = {
            if (!viewModel.paymentInProgress.value) onDismiss()
        },
        sheetState = rememberModalBottomSheetState(),
        contentColor = Color(0xFF212121)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                // ✅ Добавляем отступ для системных кнопок навигации снизу
                .navigationBarsPadding()
                .padding(horizontal = 16.dp)
                .padding(bottom = 16.dp), // ✅ Даем отступ снизу для Column
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Оплата «$targetRoleName»",
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Стоимость: $basePrice ₽",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = ColorBlue
            )
            Spacer(modifier = Modifier.height(8.dp))

            HorizontalDivider(modifier = Modifier.padding(bottom = 8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = saveCard,
                    onCheckedChange = { saveCard = it },
                    colors = CheckboxDefaults.colors(checkedColor = ColorBlue)
                )
                Text(
                    text = "Сохранить данные карты",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            Text(
                text = "CVV не сохраняется в целях безопасности",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = cardNumber.chunked(4).joinToString(" "),
                onValueChange = { cardNumber = it.filter { c -> c.isDigit() }.take(16) },
                label = { Text("Номер карты") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                colors = textFieldColors
            )
            Spacer(modifier = Modifier.height(4.dp))

            OutlinedTextField(
                value = expiry,
                onValueChange = { newValue ->
                    val digits = newValue.filter { it.isDigit() }.take(4)
                    expiry = if (digits.length > 2) {
                        "${digits.take(2)}/${digits.drop(2)}"
                    } else {
                        digits
                    }
                },
                label = { Text("ММ/ГГ") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                colors = textFieldColors
            )
            Spacer(modifier = Modifier.height(4.dp))

            OutlinedTextField(
                value = cvv,
                onValueChange = { cvv = it.filter { c -> c.isDigit() }.take(3) },
                label = { Text("CVV") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                modifier = Modifier.fillMaxWidth(),
                colors = textFieldColors
            )

            errorMessage?.let {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.height(16.dp)) // Чуть больше отступ перед кнопкой

            Button(
                onClick = {
                    errorMessage = null
                    if (saveCard) {
                        viewModel.saveCardData(cardNumber, expiry)
                    } else {
                        viewModel.clearCardData()
                    }

                    viewModel.paymentInProgress.value = true

                    coroutineScope.launch {
                        delay(1500)

                        val uid = Firebase.auth.currentUser?.uid
                        if (uid != null) {
                            if (targetRole == UserRole.PREMIUM) {
                                errorMessage = "Оплата Премиума в разработке"
                                viewModel.paymentInProgress.value = false
                            } else {
                                viewModel.upgradeToBusiness(
                                    uid = uid,
                                    onSuccess = {
                                        viewModel.paymentInProgress.value = false
                                        viewModel.showPaymentSheet.value = false
                                        onPaymentSuccess()
                                        onDismiss()
                                    },
                                    onError = { msg ->
                                        viewModel.paymentInProgress.value = false
                                        errorMessage = msg
                                    }
                                )
                            }
                        } else {
                            viewModel.paymentInProgress.value = false
                            errorMessage = "Требуется авторизация"
                        }
                    }
                },
                enabled = !viewModel.paymentInProgress.value && isFormValid,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = ColorBlue,
                    contentColor = Color.White,
                    disabledContainerColor = ColorBlue.copy(alpha = 0.4f),
                    disabledContentColor = Color.White.copy(alpha = 0.8f)
                )
            ) {
                if (viewModel.paymentInProgress.value) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color.White
                    )
                } else {
                    Text("Оплатить $basePrice ₽")
                }
            }

            // ✅ Увеличенный отступ снизу, чтобы кнопка не прилипала к краю экрана
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
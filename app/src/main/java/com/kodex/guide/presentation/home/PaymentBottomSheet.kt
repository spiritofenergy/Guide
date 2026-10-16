package com.kodex.guide.presentation.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.kodex.guide.domain.model.UserRole
import com.kodex.guide.ui.theme.ButtonColorBlue
import com.kodex.guide.ui.theme.Orange
import com.kodex.guide.ui.theme.PurpleGrey40
import com.kodex.guide.ui.theme.PurpleGrey80
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentBottomSheet(
    viewModel: HomeViewModel,
    onDismiss: () -> Unit,
    onPaymentSuccess: () -> Unit

) {
    // ✅ Определяем, за что платим (desiredRole ставится в requestUpgrade / requestPremiumUpgrade)
    val targetRole = viewModel.desiredRole.value ?: UserRole.BUSINESS
    val targetRoleName = viewModel.getRoleDisplayName(targetRole)
    val price = if (targetRole == UserRole.PREMIUM) "450 ₽ / месяц" else "150 ₽ / месяц"

    val coroutineScope = rememberCoroutineScope()
    var cardNumber by remember { mutableStateOf("") }
    var expiry by remember { mutableStateOf("") }
    var cvv by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val isFormValid = cardNumber.length == 16 && expiry.length == 5 && cvv.length == 3
    var saveCard by remember { mutableStateOf(false) }

// ✅ Подгрузка сохранённой карты при открытии шторки
    LaunchedEffect(Unit) {
        val (savedNumber, savedExpiry) = viewModel.getSavedCardData()
        if (savedNumber.isNotEmpty()) {
            cardNumber = savedNumber
            expiry = savedExpiry
            saveCard = true   // карта уже сохранялась — чекбокс включён
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
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Оплата «$targetRoleName»",
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Стоимость: $price",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            // ✅ Сохранение карты
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = saveCard,
                    onCheckedChange = { saveCard = it },
                    colors = CheckboxDefaults.colors(
                        checkedColor = ButtonColorBlue
                    )
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

            // Номер карты с форматированием 0000 0000 0000 0000
            OutlinedTextField(
                value = cardNumber.chunked(4).joinToString(" "),
                onValueChange = { cardNumber = it.filter { c -> c.isDigit() }.take(16) },
                label = { Text("Номер карты") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,      // ✅ фон при фокусе
                    unfocusedContainerColor = Color.White,    // ✅ фон без фокуса
                    disabledContainerColor = Color.White,     // фон в disabled
                    focusedBorderColor = ButtonColorBlue,     // рамка при фокусе — синяя
                    unfocusedBorderColor = Color.Gray         // рамка без фокуса
                )


            )
            Spacer(modifier = Modifier.height(4.dp))

            // Срок с авто-вставкой "/"
            OutlinedTextField(
                value = expiry,
                onValueChange = { newValue ->
                    val digits = newValue.filter { it.isDigit() }.take(4)
                    expiry = if (digits.length > 2)
                        "${digits.take(2)}/${digits.drop(2)}"
                    else digits
                },
                label = { Text("ММ/ГГ") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,      // ✅ фон при фокусе
                    unfocusedContainerColor = Color.White,    // ✅ фон без фокуса
                    disabledContainerColor = Color.White,     // фон в disabled
                    focusedBorderColor = ButtonColorBlue,     // рамка при фокусе — синяя
                    unfocusedBorderColor = Color.Gray         // рамка без фокуса
                )
            )
            Spacer(modifier = Modifier.height(4.dp))

            OutlinedTextField(
                value = cvv,
                onValueChange = { cvv = it.filter { c -> c.isDigit() }.take(3) },
                label = { Text("CVV") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,      // ✅ фон при фокусе
                    unfocusedContainerColor = Color.White,    // ✅ фон без фокуса
                    disabledContainerColor = Color.White,     // фон в disabled
                    focusedBorderColor = ButtonColorBlue,     // рамка при фокусе — синяя
                    unfocusedBorderColor = Color.Gray         // рамка без фокуса
                )
            )

            // Ошибка
            errorMessage?.let {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    errorMessage = null
                    // ✅ Сохраняем или удаляем карту по чекбоксу (CVV НЕ сохраняем)
                    if (saveCard) {
                        viewModel.saveCardData(cardNumber, expiry)
                    } else {
                        viewModel.clearCardData()
                    }
                    viewModel.paymentInProgress.value = true
                    coroutineScope.launch {
                        // TODO: реальный запрос к платёжному шлюзу (ЮKassa, Stripe и т.д.)
                        kotlinx.coroutines.delay(1500) // имитация сети

                        val uid = Firebase.auth.currentUser?.uid
                        if (uid != null) {
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
                        } else {
                            viewModel.paymentInProgress.value = false
                            errorMessage = "Требуется авторизация"
                        }
                    }
                },
                enabled = !viewModel.paymentInProgress.value && isFormValid,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = ButtonColorBlue,          // ✅ синий 0xFF008BF5
                    contentColor = Color.White,
                    disabledContainerColor = ButtonColorBlue.copy(alpha = 0.4f),
                    disabledContentColor = Color.White.copy(alpha = 0.8f)
                )

            ) {
                if (viewModel.paymentInProgress.value) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color.White
                    )
                } else {
                    Text("Оплатить")
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
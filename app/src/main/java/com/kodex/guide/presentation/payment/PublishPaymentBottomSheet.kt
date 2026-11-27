package com.kodex.guide.presentation.payment

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.kodex.guide.domain.model.PostEditorState
import com.kodex.guide.presentation.myPosts.MyPostsViewModel
import com.kodex.guide.ui.theme.ColorBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PublishPaymentBottomSheet(
    viewModel: MyPostsViewModel,
    postState: PostEditorState,
    periodDays: Long,
    framePrice: Int,
    bannerPrice: Int,
    totalPrice: Int,
    isLoading: Boolean,
    onDismiss: () -> Unit
) {
    val savedCardData = remember(viewModel) { viewModel.getSavedCardData() }
    var cardNumber by remember(viewModel) { mutableStateOf(savedCardData.first) }
    var expiry by remember(viewModel) { mutableStateOf(savedCardData.second) }
    var cvv by remember { mutableStateOf("") }
    var saveCard by remember(viewModel) {
        mutableStateOf(savedCardData.first.isNotEmpty())
    }

    val isFormValid = cardNumber.length == 16 && expiry.length == 5 && cvv.length == 3
    val textFieldColors = OutlinedTextFieldDefaults.colors(
        focusedContainerColor = Color.White,
        unfocusedContainerColor = Color.White,
        disabledContainerColor = Color.White,
        focusedBorderColor = ColorBlue,
        unfocusedBorderColor = Color.Gray
    )

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 20.dp)
                .padding(bottom = 16.dp)
        ) {
            // 👇 Скроллируемая часть контента
            Column(
                modifier = Modifier
                   // .weight(1f)
                    // .verticalScroll(rememberScrollState())
            ) {
                Text("Оплата публикации", style = MaterialTheme.typography.titleLarge)
                Spacer(Modifier.height(8.dp))
                Text("Период публикации: $periodDays дн.")

                if (framePrice > 0) {
                    PublishPaymentPriceRow("Рамка", framePrice)
                }
                if (bannerPrice > 0) {
                    PublishPaymentPriceRow("Баннер", bannerPrice)
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Итого", style = MaterialTheme.typography.titleMedium)
                    Text("$totalPrice ₽", style = MaterialTheme.typography.titleMedium, color = ColorBlue)
                }

                if (totalPrice > 0) {
                    Spacer(Modifier.height(12.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = saveCard,
                            onCheckedChange = { saveCard = it },
                            colors = CheckboxDefaults.colors(checkedColor = ColorBlue)
                        )
                        Text("Сохранить данные карты")
                    }
                    OutlinedTextField(
                        value = cardNumber.chunked(4).joinToString(" "),
                        onValueChange = { cardNumber = it.filter(Char::isDigit).take(16) },
                        label = { Text("Номер карты") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        colors = textFieldColors,
                        singleLine = true
                    )
                    Spacer(Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = expiry,
                            onValueChange = { value ->
                                val digits = value.filter(Char::isDigit).take(4)
                                expiry = if (digits.length > 2) {
                                    "${digits.take(2)}/${digits.drop(2)}"
                                } else {
                                    digits
                                }
                            },
                            label = { Text("ММ/ГГ") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f),
                            colors = textFieldColors,
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = cvv,
                            onValueChange = { cvv = it.filter(Char::isDigit).take(3) },
                            label = { Text("CVV") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                            modifier = Modifier.weight(1f),
                            colors = textFieldColors,
                            singleLine = true
                        )
                    }
                    Text(
                        "CVV не сохраняется в целях безопасности",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            } // Конец скроллируемой части

            Spacer(Modifier.height(16.dp))

            // 👇 Кнопка всегда закреплена внизу
            Button(
                onClick = {
                    viewModel.savePost(
                        state = postState,
                        publish = true,
                        paymentAmountRubles = totalPrice.takeIf { it > 0 },
                        saveCard = saveCard,
                        cardNumber = cardNumber.takeIf { it.isNotEmpty() },
                        cardExpiry = expiry.takeIf { it.isNotEmpty() }
                    )
                },
                enabled = !isLoading && (totalPrice == 0 || isFormValid),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = ColorBlue,
                    contentColor = Color.White,
                    disabledContainerColor = ColorBlue.copy(alpha = 0.4f),
                    disabledContentColor = Color.White.copy(alpha = 0.8f)
                )
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp), // 👈 Добавил размер, как в тарифном
                        color = Color.White
                    )
                } else {
                    Text(if (totalPrice == 0) "Опубликовать" else "Оплатить $totalPrice ₽")
                }
            }
        }
    }
}

@Composable
private fun PublishPaymentPriceRow(label: String, price: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label)
        Text("$price ₽")
    }
}
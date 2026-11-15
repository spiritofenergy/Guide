package com.kodex.guide.presentation.myPosts.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun OptionsSection(
    delivery: Boolean,
    onDeliveryChange: (Boolean) -> Unit,
    payment: Boolean,
    onPaymentChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        OptionSwitchCard(
            label = "Доставка",
            checked = delivery,
            onCheckedChange = onDeliveryChange,
            modifier = Modifier.weight(0.45f)
        )
        OptionSwitchCard(
            label = "Оплата картой",
            checked = payment,
            onCheckedChange = onPaymentChange,
            modifier = Modifier.weight(0.55f)

        )
    }
}

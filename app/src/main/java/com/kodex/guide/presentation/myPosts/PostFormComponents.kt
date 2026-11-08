package com.kodex.guide.presentation.myPosts

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kodex.guide.ui.theme.ButtonColorBlue
import com.kodex.guide.ui.theme.Orange

private val OptionalMark = Color(0xFF3B82F6)

@Composable
fun PhotoSection(
    onGalleryClick: () -> Unit,
    onCameraClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        OutlinedButton(
            onClick = onGalleryClick,
            modifier = Modifier.weight(1f),
            colors = ButtonDefaults.outlinedButtonColors(containerColor = Orange)
        ) {
            Icon(Icons.Default.PhotoCamera, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(4.dp))
            Text("Выбрать фото")
        }
        OutlinedButton(
            onClick = onCameraClick,
            modifier = Modifier.weight(1f),
            colors = ButtonDefaults.outlinedButtonColors(containerColor = ButtonColorBlue)
        ) {
            Icon(Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(4.dp))
            Text("Камера")
        }
    }
}

@Composable
fun AddressSection(
    village: String, onVillageChange: (String) -> Unit,
    street: String, onStreetChange: (String) -> Unit,
    house: String, onHouseChange: (String) -> Unit,
    flat: String, onFlatChange: (String) -> Unit,
    location: Boolean, onLocationChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("Адрес", fontWeight = FontWeight.Bold, fontSize = 16.sp)

        OutlinedTextField(
            value = village, onValueChange = onVillageChange,
            label = { Text("Станица / населённый пункт") },
            modifier = Modifier.fillMaxWidth(), singleLine = true
        )

        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = location, onCheckedChange = onLocationChange,
                colors = CheckboxDefaults.colors(checkedColor = OptionalMark))
            Text("Показать на карте")
        }

        OutlinedTextField(
            value = street, onValueChange = onStreetChange,
            label = {
                Row {
                    Text("Улица")
                    if (location) Text(" *", color = OptionalMark)
                }
            },
            modifier = Modifier.fillMaxWidth(), singleLine = true
        )

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = house, onValueChange = onHouseChange,
                label = {
                    Row {
                        Text("Дом")
                        if (location) Text(" *", color = OptionalMark)
                    }
                },
                modifier = Modifier.weight(1f), singleLine = true
            )
            OutlinedTextField(
                value = flat, onValueChange = onFlatChange,
                label = { Text("Квартира") },
                modifier = Modifier.weight(1f), singleLine = true
            )
        }
    }
}

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
            modifier = Modifier.weight(1f)
        )
        OptionSwitchCard(
            label = "Оплата",
            checked = payment,
            onCheckedChange = onPaymentChange,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun OptionSwitchCard(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = if (checked) ButtonColorBlue.copy(alpha = 0.1f)
            else MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.titleMedium,
                color = if (checked) ButtonColorBlue else MaterialTheme.colorScheme.onSurface
            )
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = ButtonColorBlue,
                    checkedTrackColor = ButtonColorBlue.copy(alpha = 0.3f)
                )
            )
        }
    }
}
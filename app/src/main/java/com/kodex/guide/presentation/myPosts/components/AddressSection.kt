package com.kodex.guide.presentation.myPosts.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kodex.guide.ui.theme.ButtonColorBlue


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
                colors = CheckboxDefaults.colors(checkedColor = ButtonColorBlue))
            Text("Показать на карте")
        }

        OutlinedTextField(
            value = street, onValueChange = onStreetChange,
            label = {
                Row {
                    Text("Улица")
                    if (location) Text(" *", color = ButtonColorBlue)
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
                        if (location) Text(" *", color = ButtonColorBlue)
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


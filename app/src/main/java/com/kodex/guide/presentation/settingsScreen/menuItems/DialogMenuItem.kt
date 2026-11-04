package com.kodex.guide.presentation.settingsScreen.menuItems

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kodex.guide.presentation.settingsScreen.DialogType
import com.kodex.guide.presentation.settingsScreen.MenuItem

@Composable
fun DialogMenuItem(
    item: MenuItem.DialogItem = MenuItem.DialogItem("Test Item"),
    onItemClick: (DialogType, String, Int)-> Unit,

    ) {
    Column(
        modifier = Modifier.fillMaxWidth().clickable{
            onItemClick(item.dialogType, item.title, item.fieldsLabelsArrayId)
        }
    ) {
        Row(modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = item.title,
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            )
        }
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(Color.Black)
                .padding(10.dp)
        )
    }
}


@Composable
@Preview(showBackground = true)
fun ShowMenuUiItem() {
    //DialogMenuItem()
}
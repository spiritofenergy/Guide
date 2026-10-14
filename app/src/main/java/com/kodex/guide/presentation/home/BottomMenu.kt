package com.kodex.guide.presentation.home

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dialpad
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.kodex.guide.domain.model.BookCategories
import kotlinx.coroutines.launch

@Composable
fun BottomMenu(
    selectedItem: Int,
    onCategoryClick: (BookCategories)-> Unit = {},
    onHomeClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {},
    onSavedClick: () -> Unit = {}
) {
    val items = listOf(
        BottomMenuItem.Home,
        BottomMenuItem.Saved,
        BottomMenuItem.Settings
    )

    // val selectedItem = remember { mutableStateOf("Home") }

    NavigationBar(
        modifier = Modifier.offset { IntOffset(x = 0, y = 5) },
        containerColor = androidx.compose.ui.graphics.Color.White
    ) {
        items.forEach { item ->
            NavigationBarItem(
                selected = selectedItem == item.titleId,
                onClick = {
                    when (item.titleId) {
                        BottomMenuItem.Home.titleId -> onHomeClick()
                        BottomMenuItem.Saved.titleId -> onSavedClick()
                        BottomMenuItem.Settings.titleId -> onSettingsClick()
                    }
                },
                icon = {
                    Icon(
                        painter = painterResource(id = item.iconId),
                        contentDescription = null,



                    )
                },
                label = {
                    Text(
                        text = stringResource(item.titleId)
                    )
                }
            )
        }
    }
}

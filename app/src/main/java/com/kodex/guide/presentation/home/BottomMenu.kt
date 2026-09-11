package com.kodex.guide.presentation.home

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dialpad
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.kodex.guide.domain.model.BookCategories
import com.kodex.guide.ui.theme.PurpleGrey80
import kotlinx.coroutines.launch

@Composable
fun BottomMenu(
    selectedItem: Int,
    onCategoryClick: (BookCategories)-> Unit = {},
    onHomeClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {}
) {
    val items = listOf(
        BottomMenuItem.Home,
        BottomMenuItem.Saved,
      //  BottomMenuItem.Settings

    )

    // val selectedItem = remember { mutableStateOf("Home") }

    NavigationBar(
        containerColor = PurpleGrey80
    ) {
        items.forEach { item ->
            NavigationBarItem(
                selected = selectedItem == item.titleId,
                //selected = selectedItem == item.titleId,
                onClick = {
                    when (item.titleId) {
                        BottomMenuItem.Home.titleId -> onHomeClick()
                        BottomMenuItem.Saved.titleId -> onCategoryClick(BookCategories.OTHER)
                       // BottomMenuItem.Settings.titleId -> onSettingsClick()

                    }
                },
                icon = {
                    Icon(
                        painter = painterResource(id = item.iconId),
                        contentDescription = null
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

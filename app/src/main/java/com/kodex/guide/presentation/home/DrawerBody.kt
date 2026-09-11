package com.kodex.guide.presentation.home

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddHomeWork
import androidx.compose.material.icons.filled.Agriculture
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Celebration
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.CrueltyFree
import androidx.compose.material.icons.filled.Dialpad
import androidx.compose.material.icons.filled.ElectricalServices
import androidx.compose.material.icons.filled.Login
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.MiscellaneousServices
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.kodex.bookmarketcompose.R
import com.kodex.guide.domain.model.BookCategories
import com.kodex.guide.domain.model.UserRole
import com.kodex.guide.ui.theme.ButtonColorBlue
import com.kodex.guide.ui.theme.GrayLite
import kotlinx.coroutines.launch

/**
 * Модель элемента бокового меню.
 */
private data class DrawerCategoryItem(
    val icon: ImageVector,
    val category: BookCategories,
   // @StringRes val titleResId: Int
)

@Composable
fun DrawerBody(
    viewModelHome: HomeViewModel = hiltViewModel(),
    onRegistrationNeeded: () -> Unit = {},
    onEnter: () -> Unit = {},
    onAddBookClick: () -> Unit = {},
    onLoginClick: () -> Unit = {},
    onAnonymousClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {},
    onAdmin: (Boolean) -> Unit = {},
    onAdminClick: () -> Unit = {},
    onCategoryClick: (BookCategories) -> Unit = {},
    onMyPostsClick: () -> Unit = {},
    onCloseDrawer: () -> Unit = {}   // ✅ передаём закрытие снаружи
) {
    // Собираем состояния один раз
    val userRole by viewModelHome.userRole.collectAsState()
    val isAdmin by viewModelHome.isAdminState.collectAsState()
    val isAuthorized by viewModelHome.isAuthorized.collectAsState()

    val categoryList = stringArrayResource(id = R.array.category_array)
    val categoryAdmin = stringArrayResource(id = R.array.category_admin)

    // ✅ Статический список категорий — порядок больше не важен
    val categoryItems = remember {
        listOf(
            DrawerCategoryItem(Icons.Default.CrueltyFree, BookCategories.ANIMALS,),           // 0 - Животные
            DrawerCategoryItem(Icons.Default.Celebration, BookCategories.PLANTS, ),             // 1 - Растения
            DrawerCategoryItem(Icons.Default.CleaningServices, BookCategories.WORK),            // 2 - Работа
            DrawerCategoryItem(Icons.Default.MiscellaneousServices, BookCategories.SERVICES), // 3 - Услуги
            DrawerCategoryItem(Icons.Default.AddHomeWork, BookCategories.REAL_ESTATE ),   // 4 - Недвижимость
            DrawerCategoryItem(Icons.Default.Agriculture, BookCategories.AUTO, ),                 // 5 - Авто
            DrawerCategoryItem(Icons.Default.ElectricalServices, BookCategories.ELECTRONICS,), // 6 - Электроника
            DrawerCategoryItem(Icons.Default.AutoAwesome, BookCategories.ENTERTAINMENTS), // 7 - Развлечения
            DrawerCategoryItem(Icons.Default.AutoAwesome, BookCategories.NEWS), // 7 - Развлечения
            DrawerCategoryItem(Icons.Default.Dialpad, BookCategories.OTHER)                    // 8 - Сохраненные
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ButtonColorBlue)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            Divider()
            Spacer(modifier = Modifier.height(16.dp))

            // В DrawerBody.kt

// ✅ Рендерим категории циклом (используем item.category.id для получения правильного текста из массива)
            categoryItems.forEach { item ->
                DrawerMenuItem(
                    iconDrawableId = item.icon,
                    text = categoryList[item.category.id],
                    onItemClick = {
                        onCategoryClick(item.category) // 1. Сообщаем, что нажали
                        onCloseDrawer()                // 2. Просим родительский экран закрыть шторку
                    }
                )
            }

            Spacer(modifier = Modifier.height(15.dp))
            Divider()
            Spacer(modifier = Modifier.height(15.dp))

            // Админ-пункт
            if (isAdmin) {
                DrawerMenuItem(
                    iconDrawableId = Icons.Default.Security,
                    text = categoryAdmin[0],
                    onItemClick = {
                        onAdminClick()
                        onCloseDrawer()
                    }
                )
            }

            // Пункты для авторизованных / анонимных
            if (userRole.hasAccessTo(UserRole.ANONYMOUS)) {
                DrawerMenuItem(
                    iconDrawableId = Icons.Default.Add,
                    text = stringResource(id = R.string.create_post), // ✅ вынесли в strings.xml
                    onItemClick = {
                        onAddBookClick()
                        onCloseDrawer() // ✅ теперь drawer закрывается
                    }
                )

                DrawerMenuItem(
                    iconDrawableId = if (isAuthorized) Icons.Default.Logout else Icons.Default.Login,
                    text = if (isAuthorized) categoryAdmin[3] else categoryAdmin[2],
                    onItemClick = {
                        viewModelHome.onAuthButtonClick()
                        onCloseDrawer()
                    }
                )

                if (userRole.hasAccessTo(UserRole.USER)) {
                    DrawerMenuItem(
                        iconDrawableId = Icons.Default.Person,
                        text = categoryAdmin[5], // ✅ добавили именованный параметр
                        onItemClick = {
                            onMyPostsClick()
                            onCloseDrawer()
                        }
                    )
                }
            }
        }
    }

}

/** Вынесенный разделитель — убираем дублирование разметки */
@Composable
private fun Divider() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(GrayLite)
    )
}
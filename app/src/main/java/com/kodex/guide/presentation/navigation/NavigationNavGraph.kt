package com.kodex.guide.presentation.navigation

import android.os.Build
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.toRoute
import com.kodex.guide.R
import com.kodex.guide.data.mapper.toParallaxNavObject
import com.kodex.guide.presentation.admin_panel.AdminPanelScreen
import com.kodex.guide.presentation.admin_panel.ModerationScreen
import com.kodex.guide.presentation.comments.CommentsScreen
import com.kodex.guide.presentation.detailScreen.DetailScreen
import com.kodex.guide.presentation.home.HomeScreen
import com.kodex.guide.presentation.home.HomeViewModel
import com.kodex.guide.presentation.login.LoginScreen
import com.kodex.guide.presentation.login.sign_up.SignUpScreen
import com.kodex.guide.presentation.myPosts.MyPostEditorScreen
import com.kodex.guide.presentation.myPosts.MyPostsScreen
import com.kodex.guide.presentation.settingsScreen.SettingsScreen
import com.kodex.guide.ui.theme.DrawerColorBlue
import com.kodex.guide.ui.theme.Orange

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NavigationNavGraph(
    navController: NavHostController,
    onExitApp: () -> Unit
) {
    var showExitDialog by remember { mutableStateOf(false) }
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val isRootDestination = currentBackStackEntry != null &&
            navController.previousBackStackEntry == null

    BackHandler(enabled = isRootDestination) {
        showExitDialog = true
    }

    NavHost(
        navController = navController,
        startDestination = NavRoutes.HomeDataObject(uid = "", email = ""),
    ) {
        composable<NavRoutes.LoginNavObject> {
            LoginScreen(
                onNavigationToMainScreen = { navData -> navController.navigate(navData) },
                onNavigationToSignUpScreen = { navData -> navController.navigate(navData) }
            )
        }
        composable<NavRoutes.SingUpNavObject> {
            SignUpScreen() { navData -> navController.navigate(navData) }
        }

        composable<NavRoutes.HomeDataObject> { navEntry ->
            val navData = navEntry.toRoute<NavRoutes.HomeDataObject>()
            val homeViewModel: HomeViewModel = hiltViewModel()
            val currentUid = homeViewModel.headerUser.value?.uid ?: ""

            HomeScreen(
                navController = navController,
                navData = navData,
                viewModel = homeViewModel, // Передаем viewModel явно
                onBookClick = { book ->
                    navController.navigate(book.toParallaxNavObject())
                },
                onBookEditClick = { book ->
                    navController.navigate(NavRoutes.MyPostEditorNavObject(bookKey = book.key))
                },
                onAdminClick = { navController.navigate(NavRoutes.ModerationNavObject) },
                onLoginClick = { navController.navigate(NavRoutes.LoginNavObject) },
                onAnonymousClick = {
                    navController.navigate(
                        NavRoutes.HomeDataObject(
                            uid = "",
                            email = ""
                        )
                    )
                },
                onSettingsClick = { navController.navigate(NavRoutes.SettingsNavObject) },
                onCategoryClick = {
                    navController.navigate(
                        NavRoutes.HomeDataObject(
                            uid = "", email = ""
                        )
                    )
                },
                onAddClick = { navController.navigate(NavRoutes.MyPostEditorNavObject()) },
                onRegistrationNeeded = { navController.navigate(NavRoutes.SingUpNavObject) },
                onEnter = { navController.navigate(NavRoutes.LoginNavObject) },
                onMyPostsClick = {
                    navController.navigate(
                        NavRoutes.MyPostsNavObject(
                            uid = ""
                        )
                    )
                },
                onSavedClick = {
                },
            )
        }
        composable<NavRoutes.MyPostsNavObject> {
            MyPostsScreen(
                onEditClick = { book ->
                    navController.navigate(NavRoutes.MyPostEditorNavObject(bookKey = book.key))
                },
                onAddClick = {
                    navController.navigate(NavRoutes.MyPostEditorNavObject(bookKey = ""))
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable<NavRoutes.MyPostEditorNavObject> { navEntry ->
            val navData = navEntry.toRoute<NavRoutes.MyPostEditorNavObject>()
            val homeEntry = navController.getBackStackEntry<NavRoutes.HomeDataObject>()
            val homeViewModel: HomeViewModel = hiltViewModel(homeEntry)
            MyPostEditorScreen(
                bookKey = navData.bookKey,
                onSaved = {
                    homeViewModel.refreshBooks()
                    navController.popBackStack<NavRoutes.HomeDataObject>(inclusive = false)
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable<NavRoutes.AdminPanelNavObject> {
            AdminPanelScreen(
                onAddBookClick = {
                    navController.navigate(NavRoutes.MyPostEditorNavObject(bookKey = ""))
                },
                onModerationClick = {
                    navController.navigate(NavRoutes.ModerationNavObject)
                }
            )
        }
        composable<NavRoutes.ModerationNavObject> {
            ModerationScreen()
        }
        composable<NavRoutes.SettingsNavObject> { navEntry ->
            val homeEntry = navController.getBackStackEntry<NavRoutes.HomeDataObject>()
            val homeViewModel: HomeViewModel = hiltViewModel(homeEntry)
            SettingsScreen(
                viewModelHome = homeViewModel,
                user = homeViewModel.headerUser.value,
                onBackClick = {
                    navController.popBackStack()
                },
                onMyPostsClick = {
                    navController.navigate(NavRoutes.MyPostsNavObject(uid = ""))
                },
                onAddPostClick = {
                    navController.navigate(NavRoutes.MyPostEditorNavObject(bookKey = ""))
                },
                onCloseAccountClick = {
                    navController.popBackStack(
                        NavRoutes.LoginNavObject,
                        inclusive = false
                    )
                }
            )
        }

        // В NavigationNavGraph.kt
        composable<NavRoutes.DetailNavObject> { navEntry ->
            val navData = navEntry.toRoute<NavRoutes.DetailNavObject>()
            Log.d(
                "MyLog",
                "2. Навигация открыла DetailScreen. Получен bookId = '${navData.bookId}'"
            )

            DetailScreen(
                key = navData.bookId, // <-- ПЕРЕДАЕМ ТОЛЬКО КЛЮЧ
                onBackPressed = { navController.popBackStack() },
                onNavigateToReviews = { /* TODO */ },
                onCommentClick = {
                    navController.navigate(NavRoutes.CommentsNavData(bookId = navData.bookId))
                },
                viewModel = hiltViewModel(),
                onRelatedBookClick = { relatedBook ->
                    // Здесь либо тоже передавайте relatedBook.key,
                    // либо оставьте toParallaxNavObject(), если он передает ключ
                    navController.navigate(relatedBook.toParallaxNavObject())
                }
            )
        }

        composable<NavRoutes.CommentsNavData> { navEntry ->
            val navData = navEntry.toRoute<NavRoutes.CommentsNavData>()
            CommentsScreen(navObject = navData)
        }
    }
    // Оставляем ТОЛЬКО ОДИН диалог (удалите второй, если он у вас остался)
    if (showExitDialog) {
        AlertDialog(
            onDismissRequest = { showExitDialog = false },
            title = {
                Text(
                    text = stringResource(R.string.exit_app_title),
                    fontSize = 20.sp,
                    color = Color.Black
                )
            },
            text = {
                Text(
                    text = stringResource(R.string.exit_app_message),
                    color = Color.Black,
                    fontSize = 16.sp
                )
            },
            confirmButton = {
                // Оборачиваем в Row, чтобы кнопки стояли аккуратно в ряд с отступом
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = onExitApp,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = DrawerColorBlue // Или ButtonColorBlue, как вы хотите
                        )
                    ) {
                        Text(stringResource(R.string.exit_app_confirm))
                    }

                    Button(
                        onClick = { showExitDialog = false },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Orange // Цвет второй кнопки
                        )
                    ) {
                        Text(stringResource(R.string.cancel))
                    }
                }
            }
        )
    }
}










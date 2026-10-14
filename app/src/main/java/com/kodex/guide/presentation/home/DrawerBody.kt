package com.kodex.guide.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddHomeWork
import androidx.compose.material.icons.filled.Agriculture
import androidx.compose.material.icons.filled.Article
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Celebration
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.CrueltyFree
import androidx.compose.material.icons.filled.ElectricalServices
import androidx.compose.material.icons.filled.Login
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.MiscellaneousServices
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.LocalGroceryStore
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.kodex.bookmarketcompose.R
import com.kodex.guide.domain.model.BookCategories
import com.kodex.guide.domain.model.UserRole

private data class DrawerCategoryItem(
    val icon: ImageVector,
    val category: BookCategories,
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
    showMyPosts: Boolean = true,
    onMyPostsClick: () -> Unit = {},
    onCloseDrawer: () -> Unit = {}
) {
    val userRole by viewModelHome.userRole.collectAsState()
    val isAdmin by viewModelHome.isAdminState.collectAsState()
    val isAuthorized by viewModelHome.isAuthorized.collectAsState()
    val categoryCounts by viewModelHome.categoryCounts.collectAsState()

    val categoryList = stringArrayResource(id = R.array.category_drawer)
    val categoryAdmin = stringArrayResource(id = R.array.category_admin)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            // Categories with counts
            DrawerCategoryItem(
                icon = Icons.Default.CrueltyFree,
                category = BookCategories.ANIMALS,
                categoryName = categoryList[0],
                postCount = categoryCounts[BookCategories.ANIMALS] ?: 0,
                onItemClick = {
                    onCategoryClick(BookCategories.ANIMALS)
                    onCloseDrawer()
                }
            )
            DrawerCategoryItem(
                icon = Icons.Default.Celebration,
                category = BookCategories.PLANTS,
                categoryName = categoryList[1],
                postCount = categoryCounts[BookCategories.PLANTS] ?: 0,
                onItemClick = {
                    onCategoryClick(BookCategories.PLANTS)
                    onCloseDrawer()
                }
            )
            DrawerCategoryItem(
                icon = Icons.Default.CleaningServices,
                category = BookCategories.WORK,
                categoryName = categoryList[2],
                postCount = categoryCounts[BookCategories.WORK] ?: 0,
                onItemClick = {
                    onCategoryClick(BookCategories.WORK)
                    onCloseDrawer()
                }
            )
            DrawerCategoryItem(
                icon = Icons.Default.MiscellaneousServices,
                category = BookCategories.SERVICES,
                categoryName = categoryList[3],
                postCount = categoryCounts[BookCategories.SERVICES] ?: 0,
                onItemClick = {
                    onCategoryClick(BookCategories.SERVICES)
                    onCloseDrawer()
                }
            )
            DrawerCategoryItem(
                icon = Icons.Default.AddHomeWork,
                category = BookCategories.REAL_ESTATE,
                categoryName = categoryList[4],
                postCount = categoryCounts[BookCategories.REAL_ESTATE] ?: 0,
                onItemClick = {
                    onCategoryClick(BookCategories.REAL_ESTATE)
                    onCloseDrawer()
                }
            )
            DrawerCategoryItem(
                icon = Icons.Default.Agriculture,
                category = BookCategories.AUTO,
                categoryName = categoryList[5],
                postCount = categoryCounts[BookCategories.AUTO] ?: 0,
                onItemClick = {
                    onCategoryClick(BookCategories.AUTO)
                    onCloseDrawer()
                }
            )
            DrawerCategoryItem(
                icon = Icons.Default.ElectricalServices,
                category = BookCategories.ELECTRONICS,
                categoryName = categoryList[6],
                postCount = categoryCounts[BookCategories.ELECTRONICS] ?: 0,
                onItemClick = {
                    onCategoryClick(BookCategories.ELECTRONICS)
                    onCloseDrawer()
                }
            )
            DrawerCategoryItem(
                icon = Icons.Default.AutoAwesome,
                category = BookCategories.ENTERTAINMENTS,
                categoryName = categoryList[7],
                postCount = categoryCounts[BookCategories.ENTERTAINMENTS] ?: 0,
                onItemClick = {
                    onCategoryClick(BookCategories.ENTERTAINMENTS)
                    onCloseDrawer()
                }
            )
            DrawerCategoryItem(
                icon = Icons.Default.Article,
                category = BookCategories.NEWS,
                categoryName = categoryList[8],
                postCount = categoryCounts[BookCategories.NEWS] ?: 0,
                onItemClick = {
                    onCategoryClick(BookCategories.NEWS)
                    onCloseDrawer()
                }
            )
            DrawerCategoryItem(
                icon = Icons.Default.MoreHoriz,
                category = BookCategories.OTHER,
                categoryName = categoryList[9],
                postCount = categoryCounts[BookCategories.OTHER] ?: 0,
                onItemClick = {
                    onCategoryClick(BookCategories.OTHER)
                    onCloseDrawer()
                }
            )
            DrawerCategoryItem(
                icon = Icons.Default.Build,
                category = BookCategories.CONSTRUCTION,
                categoryName = categoryList[10],
                postCount = categoryCounts[BookCategories.CONSTRUCTION] ?: 0,
                onItemClick = {
                    onCategoryClick(BookCategories.CONSTRUCTION)
                    onCloseDrawer()
                }
            )
            DrawerCategoryItem(
                icon = Icons.Default.LocalGroceryStore,
                category = BookCategories.FOOD,
                categoryName = categoryList[11],
                postCount = categoryCounts[BookCategories.FOOD] ?: 0,
                onItemClick = {
                    onCategoryClick(BookCategories.FOOD)
                    onCloseDrawer()
                }
            )

            Spacer(modifier = Modifier.height(15.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(Color.LightGray.copy(alpha = 0.3f))
            )
            Spacer(modifier = Modifier.height(15.dp))

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

            if (userRole.hasAccessTo(UserRole.ANONYMOUS)) {
                DrawerMenuItem(
                    iconDrawableId = Icons.Default.Add,
                    text = stringResource(id = R.string.create_post),
                    onItemClick = {
                        onAddBookClick()
                        onCloseDrawer()
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

                if (showMyPosts && userRole.hasAccessTo(UserRole.USER)) {
                    DrawerMenuItem(
                        iconDrawableId = Icons.Default.Person,
                        text = categoryAdmin[5],
                        onItemClick = {
                            onMyPostsClick()
                            onCloseDrawer()
                        }
                    )
                }
                Spacer(modifier = Modifier.height(46.dp))
            }
        }
    }
}

@Composable
fun DrawerCategoryItem(
    icon: ImageVector,
    category: BookCategories,
    categoryName: String,
    postCount: Int,
    onItemClick: () -> Unit
) {
    DrawerMenuItem(
        iconDrawableId = icon,
        text = "$categoryName",
        suffixText = if (postCount > 0) postCount.toString() else null,
        onItemClick = onItemClick
    )
}

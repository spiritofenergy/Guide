package com.kodex.guide.presentation.home

import android.util.Log
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.kodex.bookmarketcompose.R

import com.kodex.guide.ui.theme.DarkBlue
import com.kodex.guide.ui.theme.PurpleGrey80
import com.kodex.guide.domain.model.BookCategories

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainTopBar(

    category: BookCategories,
    viewModel: HomeViewModel = hiltViewModel(),
    onSearch: (String) -> Unit,
    onTab: () -> Unit,
    onFilter: () -> Unit,
    onMenu: () -> Unit,
) {
    var targetState by remember { mutableStateOf(false) }
    var queryText by remember { mutableStateOf("") }
    var expandedState by remember { mutableStateOf(false) }
    val categoryList = stringArrayResource(id = R.array.category_array)

    // 1. Создаем FocusRequester для управления фокусом
    val focusRequester = remember { FocusRequester() }

    Crossfade(targetState) { target ->
        if (target) {
            // 2. Запрашиваем фокус, когда SearchBar появляется в композиции
            // Это заставит систему автоматически показать клавиатуру
            LaunchedEffect(Unit) {
                focusRequester.requestFocus()
            }
            SearchBar(
                modifier = Modifier.fillMaxWidth(),
                inputField = {
                    SearchBarDefaults.InputField(
                        // 3. Передаем FocusRequester в модификатор InputField
                        modifier = Modifier.focusRequester(focusRequester),
                        colors = TextFieldDefaults.colors(
                            focusedTrailingIconColor = DarkBlue,
                            unfocusedTrailingIconColor = DarkBlue
                        ),
                        query = queryText,
                        placeholder = {
                            Text(text = "Search...")
                        },

                        onQueryChange = { text ->
                            queryText = text
                            Log.d("MyLog", "Query onQueryChange text: $text")
                        },

                        onSearch = { text ->
                            onSearch(text)
                            Log.d("MyLog", "Query onSearch text: $text")


                        },
                        expanded = false,
                        onExpandedChange = { exp ->
                            //  expandedState = exp
                        },
                        trailingIcon = {
                            IconButton(
                                onClick = {
                                    expandedState = false
                                    targetState = false
                                    queryText = ""
                                    onSearch("")
                                }
                            ) {
                                Icon(
                                    Icons.Default.Close,
                                    contentDescription = "Close"
                                )
                            }
                        }
                    )
                },
                expanded = expandedState,
                onExpandedChange = { expo ->
                    expandedState = expo
                },
                content = {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(25) {
                            Text(
                                text = "Сообщение № $it",
                                modifier = Modifier.padding(10.dp)
                            )
                        }
                    }
                }
            )
        } else {
            TopAppBar(
                title = {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = {
                            onMenu()
                        }) {
                            Icon(
                                Icons.Default.Menu,
                                contentDescription = "Burger"
                            )
                        }

                        // ✅ Берем название из массива по id категории
                        Text(
                            text = categoryList[category.id],
                            maxLines = 1
                        )
                    }
                },

                actions = {
                    IconButton(onClick = {
                        targetState = true
                    }) {
                        Icon(
                            Icons.Default.Search,
                            contentDescription = "Search"
                        )
                    }

                    IconButton(onClick = {
                        onTab()
                    }) {
                        if (!viewModel.showTabOneOrTo.value) {
                            Icon(
                                painter = painterResource(R.drawable.litle_cards),
                                contentDescription = "List"
                            )
                        } else {
                            Icon(
                                painter = painterResource(R.drawable.big_cards),
                                contentDescription = "List"
                            )
                        }
                    }
                    IconButton(onClick = {
                        onFilter()
                    }) {
                        Icon(
                            painter = painterResource(R.drawable.filter_alt),
                            contentDescription = "Filter"
                        )
                    }

                },

                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = PurpleGrey80,
                    titleContentColor = DarkBlue,
                    actionIconContentColor = DarkBlue
                )
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ShowMainTopBar() {

}

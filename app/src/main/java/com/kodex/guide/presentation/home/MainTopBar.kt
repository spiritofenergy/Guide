package com.kodex.guide.presentation.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.kodex.bookmarketcompose.R
import com.kodex.guide.domain.model.BookCategories
import com.kodex.guide.ui.theme.ButtonColorBlue
import com.kodex.guide.ui.theme.DarkBlue
import com.kodex.guide.ui.theme.GuideTheme
import com.kodex.guide.ui.theme.Orange

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainTopBar(
    category: BookCategories,
    isListLayout: Boolean = false,
    onSearch: (String) -> Unit,
    onTab: () -> Unit,
    onFilter: () -> Unit,
    onMenu: () -> Unit,
    isCollapsed: Boolean = false,
    isSavedSelected: Boolean = false
) {
    var searchMode by remember { mutableStateOf(false) }
    var queryText by remember { mutableStateOf("") }
    var wasCollapsed by remember { mutableStateOf(isCollapsed) }
    val categoryList = stringArrayResource(id = R.array.category_top)
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current
    val context = LocalContext.current
    
    // Читаем showDrawer из preferences
    val pref: HomeViewModel = hiltViewModel()
    val currentShowDrawer by pref.showDrawer.collectAsStateWithLifecycle()

    LaunchedEffect(searchMode, isCollapsed) {
        if (searchMode && !isCollapsed) {
            focusRequester.requestFocus()
        }
    }

    LaunchedEffect(isCollapsed) {
        if (wasCollapsed && !isCollapsed) {
            queryText = ""
            onSearch("")
        }
        wasCollapsed = isCollapsed
    }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp),
        color = ButtonColorBlue.copy(alpha = 0.6f),
        shadowElevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = if (currentShowDrawer) 0.dp else 24.dp,
                    end = 8.dp,
                    top = 50.dp,
                    bottom = 6.dp
                ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)

        ) {
            if (currentShowDrawer) {
                IconButton(onClick = onMenu) {
                    Icon(
                        imageVector = Icons.Default.Menu,
                        contentDescription = "Открыть меню",
                        tint = Color.White
                    )
                }
            }

            Box(modifier = Modifier.weight(1f)) {
                Crossfade(
                    targetState = isCollapsed || searchMode,
                    animationSpec = tween(durationMillis = 450),
                    label = "topBarContent"
                ) { isCompact ->
                    if (isCompact) {
                        TextField(
                            value = queryText,
                            onValueChange = { queryText = it },
                            modifier = Modifier
                                .height(45.dp)
                                .fillMaxWidth()
                                .then(
                                    if (searchMode && !isCollapsed) {
                                        Modifier.focusRequester(focusRequester)
                                    } else {
                                        Modifier
                                    }
                                ),
                            placeholder = {
                                Text("", color = DarkBlue.copy(alpha = 0.6f))
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = null,
                                    tint = DarkBlue
                                )
                            },
                            trailingIcon = {
                                Row {
                                    // Кнопка очистки
                                    if (queryText.isNotEmpty()) {
                                        IconButton(
                                            onClick = {
                                                queryText = ""
                                                onSearch("")
                                                if (!isCollapsed) {
                                                    searchMode = false
                                                    keyboardController?.hide()
                                                }
                                            }
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Close,
                                                contentDescription = "Очистить поиск",
                                                tint = DarkBlue
                                            )
                                        }
                                    }
                                    // Кнопка голосового ввода
                                    VoiceInputButton { text ->
                                        queryText = text
                                        onSearch(text)
                                    }
                                }
                            },
                            singleLine = true,
                            shape = RoundedCornerShape(28.dp),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White,
                                disabledContainerColor = Color.White,
                                focusedTextColor = DarkBlue,
                                unfocusedTextColor = DarkBlue,
                                cursorColor = Orange,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                                disabledIndicatorColor = Color.Transparent
                            ),
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                            keyboardActions = KeyboardActions(
                                onSearch = {
                                    onSearch(queryText)
                                    keyboardController?.hide()
                                }
                            )
                        )
                    } else {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (isSavedSelected) {
                                    categoryList.getOrElse(11) { categoryList.last() }
                                } else {
                                    categoryList.getOrElse(category.id) { categoryList.last() }
                                },
                                modifier = Modifier.weight(1f),
                                maxLines = 1,
                                fontSize = 20.sp,
                                color = Color.White
                            )

                            IconButton(onClick = { searchMode = true }) {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = "Поиск",
                                    tint = Color.White
                                )
                            }

                            IconButton(onClick = onTab) {
                                Icon(
                                    painter = painterResource(
                                        if (isListLayout) {
                                            R.drawable.big_cards
                                        } else {
                                            R.drawable.litle_cards
                                        }
                                    ),
                                    contentDescription = "Изменить вид",
                                    tint = Color.White,
                                    modifier = Modifier.size(24.dp)
                                )
                            }

                            IconButton(onClick = onFilter) {
                                Icon(
                                    painter = painterResource(R.drawable.filter_alt),
                                    contentDescription = "Фильтр",
                                    tint = Color.White
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun VoiceInputButton(
    onResult: (String) -> Unit
) {
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == android.app.Activity.RESULT_OK) {
            val matches = result.data?.getStringArrayListExtra(
                android.speech.RecognizerIntent.EXTRA_RESULTS
            )
            matches?.firstOrNull()?.let { text ->
                onResult(text)
            }
        }
    }

    IconButton(
        onClick = {
            val intent = android.content.Intent(android.speech.RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(android.speech.RecognizerIntent.EXTRA_LANGUAGE_MODEL, android.speech.RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                putExtra(android.speech.RecognizerIntent.EXTRA_LANGUAGE, "ru-RU")
                putExtra(android.speech.RecognizerIntent.EXTRA_PROMPT, "Говорите...")
            }
            launcher.launch(intent)
        }
    ) {
        Icon(
            imageVector = Icons.Default.Mic,
            contentDescription = "Голосовой ввод",
            tint = Orange
        )
    }
}

@Preview(showBackground = true, name = "Main top bar")
@Composable
fun ShowMainTopBar() {
    GuideTheme {
        MainTopBar(
            category = BookCategories.OTHER,
            isListLayout = false,
            onSearch = {},
            onTab = {},
            onFilter = {},
            onMenu = {},
            isSavedSelected = false
        )
    }
}

@Preview(showBackground = true, name = "Collapsed top bar")
@Composable
fun ShowCollapsedMainTopBar() {
    GuideTheme {
        MainTopBar(
            category = BookCategories.OTHER,
            isCollapsed = true,
            onSearch = {},
            onTab = {},
            onFilter = {},
            onMenu = {}
        )
    }
}

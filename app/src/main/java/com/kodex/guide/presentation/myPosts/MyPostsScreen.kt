package com.kodex.guide.presentation.myPosts

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Publish
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.kodex.guide.domain.model.Book
import com.kodex.guide.ui.theme.ButtonColorBlue
import com.kodex.guide.ui.theme.Orange
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyPostsScreen(
    viewModel: MyPostsViewModel = hiltViewModel(),
    onEditClick: (Book) -> Unit,
    onAddClick: () -> Unit,
    onBack: () -> Unit
) {
    val posts by viewModel.myPosts.collectAsState()
    val context = LocalContext.current

    // ✅ Показываем события ViewModel (тосты)
    LaunchedEffect(Unit) {
        viewModel.events.collectLatest { event ->
            when (event) {
                is MyPostsViewModel.MyPostsEvent.Toast ->
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()

                 is MyPostsViewModel.MyPostsEvent.RestoreDraft -> TODO()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Мои объявления") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null)
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onAddClick() },
                containerColor = Orange.copy(alpha = 0.6F),
                contentColor = Color.White,

                ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Добавить объявление"
                )
            }
        }
    ) { padding ->
        if (posts.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "У вас пока нет объявлений",
                        fontSize = 16.sp,
                        color = Color.Gray
                    )
                    Spacer(Modifier.height(8.dp))
                    Button(modifier = Modifier,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF2196F3), // цвет фона (например, синий)
                            contentColor = Color.White          // цвет текста и иконки
                        ),
                        onClick = onAddClick) {
                        Text("Создать первое")
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(posts, key = { it.key }) { book ->
                    MyPostCard(
                        book = book,
                        onEdit = { onEditClick(book) },
                        onUpload = { viewModel.upload(book) },
                        onDelete = { viewModel.delete(book) }
                    )
                }
            }
        }
    }
}

@Composable
fun MyPostCard(
    book: Book,
    onEdit: () -> Unit,
    onUpload: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),   // ✅ было: fillMaxW idth
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = book.title.ifEmpty { "Без названия" },
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                StatusBadge(isUploaded = book.isUploaded)
            }
            Spacer(Modifier.height(4.dp))
            Text(
                // ✅ было: book.de scription
                text = book.description.take(100) + if (book.description.length > 100) "..." else "",
                fontSize = 14.sp,
                color = Color.Gray
            )
            Spacer(Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(                      // ✅ было: OutlinedBut ton
                    onClick = onEdit,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Orange, // цвет фона (например, синий)
                        contentColor = Color.White          // цвет текста и иконки
                    )
                ) {
                    Icon(Icons.Default.Edit, contentDescription = null)
                    Spacer(Modifier.width(4.dp))
                    Text("Ред.")
                }
                if (!book.isUploaded) {
                    Button(
                        onClick = onUpload,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF2196F3), // цвет фона (например, синий)
                            contentColor = Color.White          // цвет текста и иконки
                        )
                    ) {
                        Icon(Icons.Default.Publish, contentDescription = null)
                        Spacer(Modifier.width(4.dp))
                        Text("Опубл.")
                    }
                } else {
                    Spacer(Modifier.weight(1f))
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, tint = Orange, contentDescription = null)
                }
            }
        }
    }
}

@Composable
fun StatusBadge(isUploaded: Boolean) {
    val bg = if (isUploaded) Color(0xFF10B981).copy(alpha = 0.15f)
    else Color(0xFFF59E0B).copy(alpha = 0.15f)
    val text = if (isUploaded) "Опубликован" else "Черновик"
    val color = if (isUploaded) Color(0xFF10B981) else Color(0xFFF59E0B)
    Surface(shape = MaterialTheme.shapes.small, color = bg) {
        Text(
            text = text,
            fontSize = 11.sp,
            color = color,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}


// Предпросмотр экрана с несколькими объявлениями
@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, name = "With posts")
@Composable
fun MyPostsScreenWithPostsPreview() {
    // Фиктивные книги для демонстрации
    val sampleBooks = listOf(
        Book(
            key = "1",
            title = "Война и мир",
            description = "Роман-эпопея Льва Толстого о жизни русского общества в эпоху наполеоновских войн. Огромное произведение с множеством персонажей.",
            isUploaded = true
        ),
        Book(
            key = "2",
            title = "Преступление и наказание",
            description = "Роман Фёдора Достоевского о студенте Раскольникове и его теории о «праве на кровь по совести».",
            isUploaded = false
        ),
        Book(
            key = "3",
            title = "Мастер и Маргарита",
            description = "Роман Михаила Булгакова, сочетающий сатиру, фантастику и любовную линию.",
            isUploaded = true
        )
    )

    // Обёртка в тему приложения (если используется своя тема – замените на KodexGuideTheme)
    MaterialTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Мои объявления") },
                    navigationIcon = {
                        IconButton(onClick = {}) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Назад")
                        }
                    }
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = {},
                    containerColor = Orange.copy(alpha = 0.6F),
                    contentColor = Color.White,
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Добавить объявление")
                }
            }
        ) { padding ->
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(sampleBooks, key = { it.key }) { book ->
                    MyPostCard(
                        book = book,
                        onEdit = {},
                        onUpload = {},
                        onDelete = {}
                    )
                }
            }
        }
    }
}

// Предпросмотр пустого состояния
@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, name = "Empty")
@Composable
fun MyPostsScreenEmptyPreview() {
    MaterialTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Мои объявления") },
                    navigationIcon = {
                        IconButton(onClick = {}) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Назад")
                        }
                    }
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = {},
                    containerColor = Orange.copy(alpha = 0.6F),
                    contentColor = Color.White,
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Добавить объявление")
                }
            }
        ) { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center

            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "У вас пока нет объявлений",
                        fontSize = 16.sp,
                        color = Color.Gray
                    )
                    Spacer(Modifier.height(8.dp))
                    Button(onClick = {}) {
                        Text("Создать первое")
                    }
                }
            }
        }
    }
}

// Дополнительно – предпросмотр отдельной карточки
@Preview(showBackground = true, name = "Card")
@Composable
fun MyPostCardPreview() {
    MaterialTheme {
        Surface(modifier = Modifier.padding(16.dp)) {
            MyPostCard(
                book = Book(
                    key = "1",
                    title = "Пример книги",
                    description = "Краткое описание книги, которое обрезается по длине, если превышает 100 символов.",
                    isUploaded = false
                ),
                onEdit = {},
                onUpload = {},
                onDelete = {}
            )
        }
    }
}
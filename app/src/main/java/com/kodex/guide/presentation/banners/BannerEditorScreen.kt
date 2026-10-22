package com.kodex.guide.presentation.banners

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.rememberAsyncImagePainter
import com.kodex.guide.domain.model.Book
import com.kodex.guide.ui.theme.Orange

/**
 * Экран создания баннера - выбор поста из своих объявлений
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BannerEditorScreen(
    viewModel: BannerViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val myPosts by viewModel.myPosts.collectAsStateWithLifecycle()
    var selectedPost by remember { mutableStateOf<Book?>(null) }
    var showPostPicker by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Создать баннер") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, null)
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "Выберите пост для баннера",
                style = MaterialTheme.typography.titleLarge
            )
            
            Text(
                text = "Баннер будет вести на этот пост",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Кнопка выбора поста
            Button(
                onClick = { showPostPicker = true },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Icon(Icons.Default.AutoAwesome, null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Выбрать из своих объявлений")
            }
            
            // Выбранный пост
            if (selectedPost != null) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showPostPicker = true },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = selectedPost!!.title,
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            if (selectedPost!!.price > 0) {
                                Text(
                                    text = "${selectedPost!!.price} ₽",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                        }
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Выбрано",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Кнопка сохранения
            Button(
                onClick = {
                    if (selectedPost == null) {
                        Toast.makeText(context, "Выберите пост", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    
                    viewModel.saveBanner(
                        postKey = selectedPost!!.key,
                        imageUrl = selectedPost!!.imageUrl
                    )
                    Toast.makeText(context, "Баннер создан", Toast.LENGTH_SHORT).show()
                    onBack()
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = selectedPost != null,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Orange
                )
            ) {
                Icon(Icons.Default.Star, null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Создать баннер")
            }
        }
    }

    // Диалог выбора поста
    if (showPostPicker) {
        PostPickerDialog(
            posts = myPosts,
            onPostSelected = { post ->
                selectedPost = post
                showPostPicker = false
            },
            onDismiss = { showPostPicker = false }
        )
    }
}

@Composable
fun PostPickerDialog(
    posts: List<Book>,
    onPostSelected: (Book) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Выберите пост") },
        text = {
            if (posts.isEmpty()) {
                Text("У вас пока нет объявлений")
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 400.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(posts) { post ->
                        PostItem(post) {
                            onPostSelected(post)
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Отмена")
            }
        }
    )
}

@Composable
fun PostItem(post: Book, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Миниатюра поста
            if (post.imageUrl.isNotEmpty()) {
                Image(
                    painter = rememberAsyncImagePainter(post.imageUrl),
                    contentDescription = null,
                    modifier = Modifier
                        .size(50.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = post.title,
                    style = MaterialTheme.typography.bodyLarge,
                    maxLines = 1
                )
                if (post.price > 0) {
                    Text(
                        text = "${post.price} ₽",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

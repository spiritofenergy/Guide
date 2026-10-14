package com.kodex.guide.presentation.myPosts

import android.widget.Toast
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.collectLatest
import coil.compose.AsyncImage
import com.kodex.bookmarketcompose.R
import com.kodex.guide.domain.model.BookCategories
import com.kodex.guide.domain.model.PostEditorState
import com.kodex.guide.ui.theme.Orange
import com.kodex.guide.presentation.components.ActionSheetButton
import com.kodex.guide.ui.theme.ButtonColorBlue
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyPostEditorScreen(
    viewModel: MyPostsViewModel = hiltViewModel(),
    bookKey: String,
    onSaved: () -> Unit,
    onBack: () -> Unit
) {
    val validationErrors by viewModel.validationErrors.collectAsState()
    val isSavingOrUploading by viewModel.isSavingOrUploading.collectAsStateWithLifecycle()
    val isPublishing by viewModel.isPublishing.collectAsStateWithLifecycle()
    if (validationErrors.isNotEmpty()) {
        AlertDialog(
            onDismissRequest = { viewModel.clearValidationErrors() },
            title = {
                Text(
                    "Проверьте данные",
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                )
            },
            text = {
                Column {
                    validationErrors.forEach { error ->
                        Text("• $error", modifier = Modifier.padding(vertical = 2.dp))
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { viewModel.clearValidationErrors() }) {
                    Text("Понятно", color = ButtonColorBlue)
                }
            }
        )
    }

    val draft by viewModel.latestDraft.collectAsStateWithLifecycle()
    val editPost by viewModel.editPost.collectAsState()
    val categoryNames = stringArrayResource(id = R.array.category_add)

    // Состояние формы
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var telephone by remember { mutableStateOf("") }
    var category by remember { mutableStateOf(BookCategories.SERVICES) }
    var categoryExpanded by remember { mutableStateOf(false) }
    var village by remember { mutableStateOf("") }
    var street by remember { mutableStateOf("") }
    var house by remember { mutableStateOf("") }
    var flat by remember { mutableStateOf("") }
    var delivery by remember { mutableStateOf(false) }
    var payment by remember { mutableStateOf(false) }
    var location by remember { mutableStateOf(false) }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var existingImageUrl by remember { mutableStateOf("") }

    val scrollState = rememberScrollState()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.uiEvents.collectLatest { event ->
            when (event) {
                is MyPostsEvent.Toast ->
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()

                is MyPostsEvent.NavigateBack -> onSaved()
                is MyPostsEvent.RestoreDraft -> Unit
            }
        }
    }

    // Лаунчеры
    val photoUri = remember {
        val file = File(context.cacheDir, "camera_photo_${System.currentTimeMillis()}.jpg")
        FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
    }

    val takePicture =
        rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success) imageUri = photoUri
        }

    val pickImage =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            imageUri = uri
        }

    // Загрузка данных
    LaunchedEffect(draft, bookKey) {
        if (bookKey.isEmpty() && draft != null && title.isBlank()) {
            draft?.let { d ->
                title = d.title; description = d.description; price = d.price
                telephone = d.telephone; village = d.village; street = d.street
                house = d.house; flat = d.flat; category = d.category
                delivery = d.delivery; payment = d.payment; location = d.location
                existingImageUrl = d.existingImageUrl
            }
        }
    }

    LaunchedEffect(editPost) {
        editPost?.let { b ->
            title = b.title; description = b.description; price = b.price.toString()
            telephone = b.telephone; village = b.village; street = b.street
            house = b.house; flat = b.flat; category = b.categoryIndex
            delivery = b.delivery; payment = b.payment; location = b.location
            existingImageUrl = b.imageUrl
        }
    }

    // Автосохранение
    LaunchedEffect(
        title,
        description,
        price,
        telephone,
        village,
        street,
        house,
        flat,
        category,
        delivery,
        payment,
        location,
        existingImageUrl
    ) {
        if (bookKey.isEmpty()) {
            viewModel.autoSaveDraft(
                PostEditorState(
                    title, description, price, telephone, village, street, house, flat,
                    category, delivery, payment, location, imageUri, existingImageUrl
                )
            )
        }
    }

    LaunchedEffect(bookKey) { viewModel.loadForEdit(bookKey) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (bookKey.isEmpty()) "Новое объявление" else "Редактирование") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.Default.ArrowBack,
                            null
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(scrollState)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Фото
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                when {
                    imageUri != null -> AsyncImage(
                        model = imageUri,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop,

                    )

                    existingImageUrl.isNotEmpty() && !existingImageUrl.startsWith("http") -> {
                        val bitmap = decodeBase64ToBitmap(existingImageUrl)
                        if (bitmap != null) Image(
                            bitmap = bitmap.asImageBitmap(),
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                        else Image(
                            painter = painterResource(id = R.drawable.placeholder_color),
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }

                    existingImageUrl.isNotEmpty() -> AsyncImage(
                        model = existingImageUrl,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )

                    else -> Image(
                        painter = painterResource(id = R.drawable.placeholder_color),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
            }

            PhotoSection(
                onGalleryClick = { pickImage.launch("image/*") },
                onCameraClick = { takePicture.launch(photoUri) }
            )

            OutlinedTextField(
                value = title,
                onValueChange = { title = it.capitalizeFirst() },
                label = { Text("Заголовок") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            OutlinedTextField(
                value = description,
                onValueChange = { description = it.capitalizeFirst() },
                label = { Text("Описание") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                maxLines = 5
            )

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = price,
                    onValueChange = { price = it.filter { c -> c.isDigit() } },
                    label = { Text("Цена") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
                OutlinedTextField(
                    value = telephone,
                    onValueChange = { telephone = it.capitalizeFirst() },
                    label = { Text("Телефон") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
            }

            CategoryDropdown(
                category,
                categoryExpanded,
                categoryNames,
                onCategoryChange = { category = it; categoryExpanded = false },
                onExpandedChange = { categoryExpanded = it })

            AddressSection(
                village,
                { village = it.capitalizeFirst() },
                street,
                { street = it.capitalizeFirst() },
                house,
                { house = it },
                flat,
                { flat = it },
                location,
                { location = it })

            OptionsSection(delivery, { delivery = it }, payment, { payment = it })

            Spacer(modifier = Modifier.height(16.dp))

            Spacer(modifier = Modifier.height(16.dp))

            // ✅ ДВЕ КНОПКИ ВНИЗУ (вместо BottomSheet)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Вспомогательная лямбда для сбора текущего состояния
                val getCurrentState: () -> PostEditorState = {
                    PostEditorState(
                        title = title,
                        description = description,
                        price = price,
                        telephone = telephone,
                        village = village,
                        street = street,
                        house = house,
                        flat = flat,
                        category = category,
                        delivery = delivery,
                        payment = payment,
                        location = location,
                        imageUri = imageUri,
                        existingImageUrl = existingImageUrl
                    )
                }

                // Кнопка "Сохранить как черновик"
                ActionSheetButton(
                    text = if (isSavingOrUploading && !isPublishing) "Сохранение…" else "Сохранить",
                    icon = Icons.Default.Star,
                    containerColor = Orange,
                    enabled = !isSavingOrUploading,
                    isLoading = isSavingOrUploading && !isPublishing,
                    loadingText = "Сохранение…",
                    onClick = {
                        viewModel.savePost(state = getCurrentState(), publish = false)
                    },
                    modifier = Modifier.weight(1f)
                )

                // Кнопка "Опубликовать"
                ActionSheetButton(
                    text = if (isSavingOrUploading && isPublishing) "Отправка…" else "Отправить",
                    icon = Icons.Default.Star,
                    containerColor = ButtonColorBlue,
                    enabled = !isSavingOrUploading,
                    isLoading = isSavingOrUploading && isPublishing,
                    loadingText = "Отправка…",
                    onClick = {
                        viewModel.savePost(state = getCurrentState(), publish = true)
                    },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}


// Вспомогательный компонент для выпадающего списка
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryDropdown(
    category: BookCategories,
    expanded: Boolean,
    names: Array<String>,
    onCategoryChange: (BookCategories) -> Unit,
    onExpandedChange: (Boolean) -> Unit
) {
    val availableCategories = BookCategories.entries.filter { it.id in names.indices }

    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = onExpandedChange) {
        OutlinedTextField(
            value = names.getOrNull(category.id) ?: names.lastOrNull().orEmpty(),
            onValueChange = {},
            readOnly = true,
            label = { Text("Категория") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { onExpandedChange(false) }) {
            availableCategories.forEach { c ->
                DropdownMenuItem(text = { Text(names[c.id]) }, onClick = { onCategoryChange(c) })
            }
        }
    }
}

fun decodeBase64ToBitmap(base64String: String): android.graphics.Bitmap? {
    return try {
        val decodedBytes = Base64.decode(base64String, Base64.DEFAULT)
        BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

fun String.capitalizeFirst(): String {
    return if (isEmpty()) this else this[0].uppercase() + this.substring(1)
}
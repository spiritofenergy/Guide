package com.kodex.guide.presentation.myPosts

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import coil.compose.AsyncImage
import com.kodex.bookmarketcompose.R
import com.kodex.guide.domain.model.Book
import com.kodex.guide.domain.model.BookCategories
import kotlinx.coroutines.flow.collectLatest
import android.graphics.BitmapFactory
import android.util.Base64
import androidx.activity.result.launch
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Publish
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.core.content.FileProvider
import com.kodex.guide.domain.model.PostEditorState
import com.kodex.guide.presentation.components.ActionSheetButton
import com.kodex.guide.ui.theme.ButtonColorBlue
import com.kodex.guide.ui.theme.Orange
import java.io.File

private val OptionalMark = Color(0xFF3B82F6) // синяя звёздочка «опционально»

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyPostEditorScreen(
    viewModel: MyPostsViewModel = hiltViewModel(),
    bookKey: String,
    onSaved: () -> Unit,
    onBack: () -> Unit
) {

    val validationErrors by viewModel.validationErrors.collectAsState()

    // ✅ Диалог ошибок валидации
    if (validationErrors.isNotEmpty()) {
        AlertDialog(
            onDismissRequest = { viewModel.clearValidationErrors() },
            title = { Text("Проверьте данные", fontWeight = FontWeight.Bold) },
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
            },
            containerColor = MaterialTheme.colorScheme.surface
        )
    }


            // ... все поля формы ...

            // Кнопки BottomSheet вызывают viewModel.savePost(...)
            // Пример вызова из BottomSheet:
            /*
            OutlinedButton(onClick = {
                viewModel.savePost(currentState, imageUri, publish = false)
            }) { Text("Сохранить черновик") }
            */



    val editPost by viewModel.editPost.collectAsState()
    val categoryNames = stringArrayResource(id = R.array.category_array)

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
    var showBottomSheet by remember { mutableStateOf(false) }
    var hasReachedEnd by remember { mutableStateOf(false) } // ✅ Флаг: достигли ли конца скролла
    // ✅ Состояние скролла
    val scrollState = rememberScrollState()
// ✅ Получаем context

// В начале Composable
    val context = LocalContext.current
    val photoUri = remember {
        val file = File(context.cacheDir, "camera_photo_${System.currentTimeMillis()}.jpg")
        FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )
    }
    // ✅ Uri для камеры (временный файл)
    val cameraImageUri = remember {
        val file = File(context.cacheDir, "camera_photo_${System.currentTimeMillis()}.jpg")
        FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )
    }

    // ✅ Лаунчер для камеры
    val takePicture = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            imageUri = photoUri
        }
    }

    LaunchedEffect(bookKey) { viewModel.loadForEdit(bookKey) }
    LaunchedEffect(bookKey) {
        if (bookKey.isEmpty()) {
            viewModel.loadSavedDraft() // Загружаем черновик только для новых постов
        } else {
            viewModel.loadForEdit(bookKey)
        }
    }
    // ✅ 1. Восстановление данных из черновика
   /* LaunchedEffect(Unit) {
        viewModel.events.collectLatest { event ->
            when (event) {
                is MyPostsEvent.Toast -> { *//* показываем тост *//* }
                is MyPostsEvent.RestoreDraft -> {
                    // Заполняем локальные переменные только если они еще пустые
                    // (чтобы не перезаписать данные, если пользователь уже начал вводить)
                    if (title.isBlank()) title = event.draft.title
                    if (description.isBlank()) description = event.draft.description
                    if (price.isBlank()) price = event.draft.price.toString()
                    telephone = event.draft.telephone
                    village = event.draft.village
                    street = event.draft.street
                    house = event.draft.house
                    flat = event.draft.flat
                    category = BookCategories.fromId(event.draft.categoryId)
                    delivery = event.draft.delivery
                    payment = event.draft.payment
                    location = event.draft.location
                    existingImageUrl = event.draft.imageUrl
                }
            }
        }
    }*/
// ✅ 2. Автосохранение при уходе со экрана
    DisposableEffect(Unit) {
        onDispose {
            // Собираем текущее состояние и сохраняем
            val currentState = PostEditorState(
                title = title, description = description, price = price,
                telephone = telephone, village = village, street = street,
                house = house, flat = flat, category = category,
                delivery = delivery, payment = payment, location = location,
                imageUri = imageUri, existingImageUrl = existingImageUrl
            )
            viewModel.saveCurrentAsDraft(currentState)
        }
    }
    // ✅ Отслеживаем окончание скролла
    LaunchedEffect(scrollState.value) {
        // Проверяем, достигли ли конца скролла
        if (scrollState.value >= scrollState.maxValue && scrollState.maxValue > 0) {
            if (!hasReachedEnd && !showBottomSheet) {
                hasReachedEnd = true
                showBottomSheet = true
            }
        } else {
            // Если пользователь проскроллил вверх - сбрасываем флаг
            if (scrollState.value < scrollState.maxValue - 100) {
                hasReachedEnd = false
            }
        }
    }
    // ✅ Загружаем черновик только для НОВОГО объявления
    LaunchedEffect(bookKey) {
        if (bookKey.isEmpty()) {
            viewModel.loadSavedDraft()
        } else {
            viewModel.loadForEdit(bookKey)
        }
    }

    // ✅ Автосохранение при уходе со экрана
    DisposableEffect(Unit) {
        onDispose {
            val currentState = PostEditorState(
                title = title, description = description, price = price,
                telephone = telephone, village = village, street = street,
                house = house, flat = flat, category = category,
                delivery = delivery, payment = payment, location = location,
                imageUri = imageUri, existingImageUrl = existingImageUrl
            )
            viewModel.saveCurrentAsDraft(currentState)
        }
    }

  /*  LaunchedEffect(Unit) {
        viewModel.events.collectLatest { event ->
            if (event is MyPostsEvent.Toast && event.message in listOf(
                    "Сохранено на устройстве", "Опубликовано"
                )
            ) onSaved()
        }
    }*/

    // подставляем данные при редактировании
    LaunchedEffect(editPost) {
        editPost?.let { b ->
            title = b.title
            description = b.description
            price = b.price.toString()
            telephone = b.telephone
            village = b.village
            street = b.street
            house = b.house
            flat = b.flat
            category = b.categoryIndex
            delivery = b.delivery
            payment = b.payment
            location = b.location
            existingImageUrl = b.imageUrl
        }
    }

    val pickImage = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? -> imageUri = uri }

    fun buildBook(): Book = (editPost ?: Book()).copy(
        title = title,
        description = description,
        price = price.toIntOrNull() ?: 0,
        telephone = telephone,
        village = village,
        street = street,
        house = house,
        flat = flat,
        categoryIndex = category,
        delivery = delivery,
        payment = payment,
        location = location
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (bookKey.isEmpty()) "Новое объявление" else "Редактирование") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null)
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(scrollState)  // ✅ Используем scrollState

                //.verticalScroll(rememberScrollState())   // ✅ скроллинг
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // --- ФОТО: placeholder.png пока фото не выбрано ---
            // Декодируем Base64 в Bitmap (если это Base64)
            val decodedBitmap = remember(existingImageUrl) {
                if (existingImageUrl.isNotEmpty() &&
                    !existingImageUrl.startsWith("http") &&
                    !existingImageUrl.startsWith("content://") &&
                    !existingImageUrl.startsWith("file://")) {
                    decodeBase64ToBitmap(existingImageUrl)
                } else null
            }
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
                        contentScale = ContentScale.Crop
                    )
                    decodedBitmap != null -> {
                        // ✅ Показываем декодированный Bitmap
                        Image(
                            bitmap = decodedBitmap.asImageBitmap(),
                            contentDescription = "Текущее фото",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                    existingImageUrl.isNotEmpty() && existingImageUrl.startsWith("http") -> {
                        // Если это URL (например, из Firebase Storage)
                        AsyncImage(
                            model = existingImageUrl,
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                    else -> Image(
                        painter = painterResource(id = R.drawable.placeholder_color), // ✅ заглушка
                        contentDescription = "Заглушка",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
            }

            // ✅ Две кнопки в одну линию: Галерея и Камера
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Кнопка "Выбрать фото" (галерея)
                OutlinedButton(
                    onClick = { pickImage.launch("image/*") },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = Orange
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.PhotoCamera,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text("Выбрать фото")
                }

                // ✅ Кнопка "Камера"
                OutlinedButton(
                    onClick = { takePicture.launch(photoUri) },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = ButtonColorBlue
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.CameraAlt,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text("Камера")
                }
            }


            // --- основные поля ---
            OutlinedTextField(
                value = title, onValueChange = { title = it },
                label = { Text("Заголовок") },
                modifier = Modifier.fillMaxWidth(), singleLine = true
            )
            OutlinedTextField(
                value = description, onValueChange = { description = it },
                label = { Text("Описание") },
                modifier = Modifier.fillMaxWidth().height(120.dp), maxLines = 5
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = price,
                    onValueChange = { price = it.filter { c -> c.isDigit() } },
                    label = { Text("Цена") },
                    modifier = Modifier.weight(1f), singleLine = true
                )
                OutlinedTextField(
                    value = telephone, onValueChange = { telephone = it },
                    label = { Text("Телефон") },
                    modifier = Modifier.weight(1f), singleLine = true
                )
            }

            // --- КАТЕГОРИЯ (выпадающий список) ---
            ExposedDropdownMenuBox(
                expanded = categoryExpanded,
                onExpandedChange = { categoryExpanded = it }
            ) {
                OutlinedTextField(
                    value = categoryNames[category.id],
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Категория") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded)
                    },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = categoryExpanded,
                    onDismissRequest = { categoryExpanded = false }
                ) {
                    BookCategories.entries.forEach { c ->
                        DropdownMenuItem(
                            text = { Text(categoryNames[c.id]) },
                            onClick = {
                                category = c
                                categoryExpanded = false
                            }
                        )
                    }
                }
            }

            // --- АДРЕС ---
            Text("Адрес", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            OutlinedTextField(
                value = village, onValueChange = { village = it },
                label = { Text("Станица / населённый пункт") },
                modifier = Modifier.fillMaxWidth(), singleLine = true
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = location,
                    colors = CheckboxDefaults.colors(checkedColor = OptionalMark),
                    onCheckedChange = { location = it })
                Text("Показать на карте")
            }
            OutlinedTextField(
                value = street, onValueChange = { street = it },
                label = {
                    Row { Text("Улица"); Text(" *", color = OptionalMark) } // ✅ опционально
                },
                modifier = Modifier.fillMaxWidth(), singleLine = true
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = house, onValueChange = { house = it },
                    label = {
                        Row { Text("Дом"); Text(" *", color = OptionalMark) }
                    },
                    modifier = Modifier.weight(1f), singleLine = true
                )
                OutlinedTextField(
                    value = flat, onValueChange = { flat = it },
                    label = {
                        Row { Text("Квартира"); Text(" *", color = OptionalMark) }
                    },
                    modifier = Modifier.weight(1f), singleLine = true
                )
            }

            // --- ЧЕКБОКСЫ в одну строку ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp) // отступ между элементами
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = delivery,
                        colors = CheckboxDefaults.colors(checkedColor = OptionalMark),
                        onCheckedChange = { delivery = it })
                    Text("Доставка")
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = payment,
                        colors = CheckboxDefaults.colors(checkedColor = OptionalMark),
                        onCheckedChange = { payment = it })
                    Text("Оплата картой")
                }
            }
        }
    }

// ✅ BOTTOM SHEET в стиле PaymentBottomSheet
    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = {
                showBottomSheet = false
                hasReachedEnd = false
            },
            sheetState = rememberModalBottomSheetState(),
            contentColor = Color(0xFF212121)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Заголовок в стиле PaymentBottomSheet
                Text(
                    text = "Публикация объявления",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )


                Spacer(modifier = Modifier.height(12.dp))

                // Кнопки в одну линию
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Кнопка "Сохранить" - Outlined стиль
                    ActionSheetButton(
                        text = "Сохранить",
                        icon = Icons.Default.Star,
                        containerColor = Orange,
                        modifier = Modifier.weight(1f),
                        onClick = {
                            showBottomSheet = false
                            hasReachedEnd = false
                            // ✅ 1. Собираем текущее состояние формы в объект
                            val currentState = PostEditorState(
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
                                imageUri = imageUri, // Новое фото из камеры/галереи
                                existingImageUrl = existingImageUrl // Старое фото, если новое не выбрано
                            )

                            // ✅ 2. Передаем объект и флаг публикации (false = черновик)
                            viewModel.savePost(state = currentState, publish = false)
                        },
                    )
                    ActionSheetButton(
                       // text = stringResource(id = R.string.call),
                        text = "Отправить",
                        icon = Icons.Default.Call,
                        containerColor = ButtonColorBlue,
                        modifier = Modifier.weight(1f),
                        onClick = {
                            showBottomSheet = false
                            hasReachedEnd = false
                            // ✅ Собираем состояние (можно вынести в отдельную функцию, чтобы не дублировать код)
                            val currentState = PostEditorState(
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

                            // ✅ 3. Передаем объект и флаг публикации (true = сразу на сервер)
                            viewModel.savePost(state = currentState, publish = true)
                        }
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}


// Функция декодирования Base64 → Bitmap
fun decodeBase64ToBitmap(base64String: String): android.graphics.Bitmap? {
    return try {
        val decodedBytes = Base64.decode(base64String, Base64.DEFAULT)
        BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}
// Предпросмотр пустого редактора (новое объявление)
@Preview(showBackground = true, name = "Editor Empty")
@Composable
fun MyPostEditorScreenEmptyPreview() {
    PreviewEditorContent(
        title = "",
        description = "",
        price = "",
        telephone = "",
        village = "",
        street = "",
        house = "",
        flat = "",
        category = BookCategories.SERVICES,
        delivery = false,
        payment = false,
        location = false,
        imageUri = null,
        existingImageUrl = ""
    )
}

// Предпросмотр редактора с заполненными данными (редактирование)
@Preview(showBackground = true, name = "Editor Filled")
@Composable
fun MyPostEditorScreenFilledPreview() {
    PreviewEditorContent(
        title = "Война и мир",
        description = "Роман-эпопея Льва Толстого о жизни русского общества в эпоху наполеоновских войн.",
        price = "500",
        telephone = "+7 999 123-45-67",
        village = "Станица Красная",
        street = "Ленина",
        house = "15",
        flat = "42",
        category = BookCategories.ALL,
        delivery = true,
        payment = false,
        location = true,
        imageUri = null,
        existingImageUrl = "" // для демонстрации можно указать URL картинки
    )
}

// Вспомогательная функция, повторяющая UI экрана редактора без ViewModel
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PreviewEditorContent(
    title: String,
    description: String,
    price: String,
    telephone: String,
    village: String,
    street: String,
    house: String,
    flat: String,
    category: BookCategories,
    delivery: Boolean,
    payment: Boolean,
    location: Boolean,
    imageUri: Uri?,
    existingImageUrl: String
) {
    // Используем тему приложения, если она определена, иначе MaterialTheme
    MaterialTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(if (title.isEmpty()) "Новое объявление" else "Редактирование") },
                    navigationIcon = {
                        IconButton(onClick = {}) {
                            Icon(Icons.Default.ArrowBack, contentDescription = null)
                        }
                    }
                )
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Блок фото
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
                            contentScale = ContentScale.Crop
                        )
                        existingImageUrl.isNotEmpty() -> AsyncImage(
                            model = existingImageUrl,
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                        else -> Image(
                            painter = painterResource(id = R.drawable.placeholder_color),
                            contentDescription = "Заглушка",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                }
                OutlinedButton(
                    onClick = {},
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.PhotoCamera, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Выбрать фото")
                }

                // Основные поля
                OutlinedTextField(
                    value = title,
                    onValueChange = {},
                    label = { Text("Заголовок") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = {},
                    label = { Text("Описание") },
                    modifier = Modifier.fillMaxWidth().height(120.dp),
                    maxLines = 5
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = price,
                        onValueChange = {},
                        label = { Text("Цена") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = telephone,
                        onValueChange = {},
                        label = { Text("Телефон") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }

                // Категория (упрощённо – только для предпросмотра)
                OutlinedTextField(
                    value = category.name, // или используйте строки из ресурсов, если нужно
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Категория") },
                    modifier = Modifier.fillMaxWidth()
                )

                // Адрес
                Text("Адрес", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                OutlinedTextField(
                    value = village,
                    onValueChange = {},
                    label = { Text("Станица / населённый пункт") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                OutlinedTextField(
                    value = street,
                    onValueChange = {},
                    label = {
                        Row { Text("Улица"); Text(" *", color = OptionalMark) }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = house,
                        onValueChange = {},
                        label = {
                            Row { Text("Дом"); Text(" *", color = OptionalMark) }
                        },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = flat,
                        onValueChange = {},
                        label = {
                            Row { Text("Квартира"); Text(" *", color = OptionalMark) }
                        },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }

                // Чекбоксы
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = delivery, onCheckedChange = {})
                    Text("Доставка")
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = payment, onCheckedChange = {})
                    Text("Оплата картой")
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = location, onCheckedChange = {})
                    Text("Показать на карте")
                }

                // Кнопка открытия BottomSheet
                Button(
                    onClick = {},
                    modifier = Modifier.fillMaxWidth()
                ) { Text("Продолжить") }
            }
        }
    }
}
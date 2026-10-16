package com.kodex.guide.presentation.settingsScreen

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kodex.bookmarketcompose.R
import com.kodex.guide.domain.model.AddressData
import com.kodex.guide.domain.model.PersonalData
import com.kodex.guide.domain.model.User
import com.kodex.guide.domain.model.UserRole
import com.kodex.guide.presentation.home.HomeViewModel
import com.kodex.guide.presentation.home.PaymentBottomSheet
import com.kodex.guide.presentation.home.ShowTariffDialog
import com.kodex.guide.ui.dialods.AccountDialog
import com.kodex.guide.ui.dialods.SettingsRestartDialog
import com.kodex.guide.ui.theme.ButtonColorBlue
import com.kodex.guide.ui.theme.Red

@Composable
fun SettingsScreen(
    viewModelHome: HomeViewModel,
    user: User?,
    viewModel: SettingsViewModel = hiltViewModel(),
    onBackClick: () -> Unit = {},
    onMyPostsClick: () -> Unit = {},
    onAddPostClick: () -> Unit = {},
    onCloseAccountClick: () -> Unit = {}
) {
    val isAuthorized by viewModelHome.isAuthorized.collectAsStateWithLifecycle()
    val userRole by viewModelHome.userRole.collectAsStateWithLifecycle()
    var dialogData by remember { mutableStateOf(AccountDialogData()) }
    val dropDownMenuSelectedOptions = remember {
        mutableStateOf(
            mutableMapOf(
                DropDownMenuType.IMAGE_FORMAT to 0,
                DropDownMenuType.IMAGE_QUALITY to 0,
                DropDownMenuType.IMAGE_SIZE to 0
            )
        )
    }
    var showConfirmDeleteDialog by remember { mutableStateOf(false) }
    var showDrawerRestartDialog by remember { mutableStateOf(false) }
    var pendingDrawerState by remember { mutableStateOf<Boolean?>(null) }
    val context = LocalContext.current
    val categoryAdmin = stringArrayResource(id = R.array.category_admin)
    val settings_address = stringResource(R.string.settings_address)
    val nameLabel = stringResource(R.string.name)
    val phone_number = stringResource(R.string.phone_number)
    val personal_data = stringResource(R.string.settings_personal_data)
    val password = stringResource(R.string.password)
    val delete_account = stringResource(R.string.settings_delete_account)
    // Отслеживаем showAuthDialog
    val showAuthDialog by viewModelHome.showAuthDialog

    LaunchedEffect(Unit) {
        viewModel.getSettings()
        viewModel.settingsBundleState.collect { settingsData ->
            dropDownMenuSelectedOptions.value[DropDownMenuType.IMAGE_FORMAT] =
                settingsData.userSettingsData.imageFormat
            dropDownMenuSelectedOptions.value[DropDownMenuType.IMAGE_QUALITY] =
                settingsData.userSettingsData.quality
            dropDownMenuSelectedOptions.value[DropDownMenuType.IMAGE_SIZE] =
                settingsData.userSettingsData.size
        }
    }

    SettingsRestartDialog(
        showDialog = showConfirmDeleteDialog,
        onDismiss = { showConfirmDeleteDialog = false },
        onConfirm = {},
        title = stringResource(R.string.attention),
        message = stringResource(R.string.deleted_account_massage),
        confirmButtonText = stringResource(R.string.delete)
    )

    SettingsRestartDialog(
        showDialog = showDrawerRestartDialog,
        onDismiss = {
            showDrawerRestartDialog = false
            pendingDrawerState = null
        },
        onConfirm = {
            pendingDrawerState?.let { viewModelHome.setShowDrawer(it) }
            showDrawerRestartDialog = false
            pendingDrawerState = null
        },
        title = "Боковое меню",
        message = "Чтобы изменения вступили в силу, перезапустите приложение.",
        confirmButtonText = "OK"
    )

    AccountDialog(
        dialogData = dialogData,
        onConfirm = { fieldValuesList, dialogType ->
            dialogData = AccountDialogData(showDialog = false)
            when (dialogType) {
                DialogType.PERSONAL_DATA -> {
                    viewModel.newPersonalData = PersonalData(
                        name = fieldValuesList[0],
                        phone = fieldValuesList[1]
                    )
                }

                DialogType.ADDRESS -> {
                    viewModel.newAddressData = AddressData(
                        city = fieldValuesList[0],
                        street = fieldValuesList[1],
                        flat = fieldValuesList[2],
                        postCode = fieldValuesList[3]
                    )
                }

                DialogType.PASSWORD -> {
                    viewModel.resetPassword(fieldValuesList[0])
                }

                DialogType.DELETE_ACCOUNT -> {
                    if (fieldValuesList[0].isEmpty() || fieldValuesList[1].isEmpty()) {
                        android.widget.Toast.makeText(
                            context, R.string.the_fields_on_may_be_empty,
                            android.widget.Toast.LENGTH_LONG
                        ).show()
                    } else {
                        viewModel.emailToDelete = fieldValuesList[0]
                        viewModel.passwordToDelete = fieldValuesList[1]
                        showConfirmDeleteDialog = true
                    }
                }

                else -> {}
            }
        },
        onDismiss = { dialogData = AccountDialogData(showDialog = false) }
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // === HEADER ===
        Surface(
            modifier = Modifier
                .fillMaxWidth(),
            color = ButtonColorBlue.copy(alpha = 0.7f),
            shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Назад",
                            tint = Color.White
                        )
                    }
                }

                val userName = user?.userName?.takeIf { it.isNotBlank() }
                val email = user?.email.orEmpty()
                val role = user?.role ?: UserRole.ANONYMOUS

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Image(
                        modifier = Modifier
                            .size(120.dp),
                        painter = painterResource(id = R.drawable.emblem),
                        contentDescription = ""
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    if (!userName.isNullOrEmpty()) {
                        Text(
                            text = userName,
                            color = Color.White,
                            fontSize = 18.sp
                        )
                    }
                    if (email.isNotEmpty()) {
                        Text(
                            text = email,
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 14.sp
                            ),
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                    Text(
                        text = "Тариф: ${getRoleDisplayName(role)}",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontSize = 16.sp
                        ),
                        color = Color.White
                    )
                }
            }
        }

        // === MENU ITEMS ===
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Personal Data
            item {
                SettingsMenuItem(
                    icon = Icons.Default.Person,
                    title = stringResource(R.string.settings_personal_data),
                    onClick = {
                        dialogData = AccountDialogData(
                            title = personal_data,
                            dialogType = DialogType.PERSONAL_DATA,
                            showDialog = true,
                            fieldLabel = listOf(nameLabel, phone_number),
                            fieldValues = listOf(
                                viewModel.oldPersonalData.name,
                                viewModel.oldPersonalData.phone
                            )
                        )
                    }
                )
            }
            // Address
            item {
                SettingsMenuItem(
                    icon = Icons.Default.LocationOn,
                    title = stringResource(R.string.settings_address),
                    onClick = {
                        dialogData = AccountDialogData(
                            title = settings_address,
                            dialogType = DialogType.ADDRESS,
                            showDialog = true,
                            fieldLabel = context.resources.getStringArray(R.array.address_array)
                                .toList(),
                            fieldValues = listOf(
                                viewModel.oldAddressData.city,
                                viewModel.oldAddressData.street,
                                viewModel.oldAddressData.flat,
                                viewModel.oldAddressData.postCode
                            )
                        )
                    }
                )
            }

            // Password
            item {
                SettingsMenuItem(
                    icon = Icons.Default.Lock,
                    title = stringResource(R.string.password),
                    onClick = {
                        dialogData = AccountDialogData(
                            title = password,
                            dialogType = DialogType.PASSWORD,
                            showDialog = true,
                            fieldLabel = listOf("email"),
                            fieldValues = listOf("")
                        )
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
            }

            // Output (Create Post)
            item {
                SettingsMenuItem(
                    icon = Icons.Default.Add,
                    title = stringResource(R.string.create_post),
                    onClick = {
                        onAddPostClick()
                    }
                )
            }

            item {
                SettingsMenuItem(
                    icon = if (isAuthorized) Icons.Default.Logout else Icons.Default.Login,
                    title = if (isAuthorized) categoryAdmin[3] else categoryAdmin[2],
                    onClick = {
                        if (isAuthorized) {
                            viewModelHome.showLogoutTariffDialog()
                        } else {
                            viewModelHome.onAuthButtonClick()
                        }
                    }
                )
            }
            // My Posts
            item {
                SettingsMenuItem(
                    icon = Icons.Default.AutoAwesomeMotion,
                    title = categoryAdmin[5],
                    onClick = {
                        onMyPostsClick()
                    }
                )
            }

            // Drawer Menu Toggle
            item {
                val showDrawer by viewModelHome.showDrawer.collectAsState()
                SettingsMenuToggle(
                    icon = Icons.Default.Menu,
                    title = "Боковое меню",
                    isChecked = showDrawer,
                    onCheckedChange = { newValue ->
                        pendingDrawerState = newValue
                        showDrawerRestartDialog = true
                    }
                )
            }


            // Delete Account
            item {
                SettingsMenuItem(
                    icon = Icons.Default.Delete,
                    title = stringResource(R.string.settings_delete_account),
                    onClick = {
                        dialogData = AccountDialogData(
                            title = delete_account,
                            dialogType = DialogType.DELETE_ACCOUNT,
                            showDialog = true,
                            fieldLabel = context.resources.getStringArray(R.array.delete_account_array)
                                .toList(),
                            fieldValues = listOf("", "")
                        )
                    },
                    titleColor = Color.Red
                )
            }
        }

        // === SAVE BUTTON ===
        /* Button(
             onClick = {
                 viewModel.newUserSettingsData = UserSettingsData(
                     imageFormat = dropDownMenuSelectedOptions.value[DropDownMenuType.IMAGE_FORMAT] ?: 0,
                     quality = dropDownMenuSelectedOptions.value[DropDownMenuType.IMAGE_QUALITY] ?: 0,
                     size = dropDownMenuSelectedOptions.value[DropDownMenuType.IMAGE_SIZE] ?: 0
                 )
                 viewModel.saveSettings()
             },
             modifier = Modifier
                 .fillMaxWidth()
                 .padding(horizontal = 16.dp, vertical = 16.dp),
             colors = ButtonDefaults.buttonColors(containerColor = ButtonColorBlue),
             shape = RoundedCornerShape(12.dp)
         ) {
             Text("Сохранить")
         }*/

        // Показываем TariffDialog
        ShowTariffDialog(viewModelHome, userRole)

        if (viewModelHome.showPaymentSheet.value) {
            PaymentBottomSheet(
                viewModel = viewModelHome,
                onDismiss = { viewModelHome.showPaymentSheet.value = false },
                onPaymentSuccess = {
                    // Можно показать Toast или обновить UI
                    Toast.makeText(context, "Роль обновлена!", Toast.LENGTH_SHORT)
                        .show()
                }
            )
        }
    }
}


fun getRoleDisplayName(role: UserRole): String {
    return when (role) {
        UserRole.ANONYMOUS -> "Гостевой"
        UserRole.USER -> "Пользователь"
        UserRole.BUSINESS -> "Бизнес"
        UserRole.PREMIUM -> "Премиум"
        UserRole.ADMIN -> "Администратор"
    }
}

@Composable
fun SettingsMenuItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    onClick: () -> Unit,
    titleColor: Color = Color.DarkGray
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White)
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = ButtonColorBlue,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = title,
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            color = titleColor,
            modifier = Modifier.weight(1f)
        )
        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = Color.Gray,
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
fun SettingsMenuToggle(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White)
            .clickable { onCheckedChange(!isChecked) }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = ButtonColorBlue,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = title,
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            color = Color.DarkGray,
            modifier = Modifier.weight(1f)
        )
        androidx.compose.material3.Switch(
            checked = isChecked,
            onCheckedChange = onCheckedChange,
            colors = androidx.compose.material3.SwitchDefaults.colors(
                checkedThumbColor = ButtonColorBlue,
                checkedTrackColor = ButtonColorBlue.copy(alpha = 0.5f),
                uncheckedThumbColor = ButtonColorBlue,
                uncheckedTrackColor = ButtonColorBlue.copy(alpha = 0.5f)
            )
        )
    }
}

@Composable
@Preview(showBackground = true)
fun ShowSettingsScreen() {
    val mockUser = User(
        uid = "test_uid",
        email = "test@example.com",
        userName = "Иван Иванов",
        role = UserRole.USER
    )
    // SettingsScreen(user = mockUser)
}

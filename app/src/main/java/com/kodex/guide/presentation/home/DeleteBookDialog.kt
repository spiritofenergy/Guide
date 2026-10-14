package com.kodex.guide.presentation.home

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.kodex.bookmarketcompose.R
import com.kodex.guide.ui.dialods.SettingsRestartDialog

@Composable
fun DeleteBookDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    SettingsRestartDialog(
        showDialog = true,
        onDismiss = onDismiss,
        title = stringResource(id = R.string.attention),
        message = stringResource(id = R.string.want_to_delete_this_message),
        onConfirm = onConfirm
    )
}
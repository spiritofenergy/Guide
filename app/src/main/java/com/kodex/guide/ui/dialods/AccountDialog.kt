package com.kodex.guide.ui.dialods

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
 import com.kodex.guide.ui.theme.DrawerColorBlue
 import com.kodex.guide.presentation.settingsScreen.AccountDialogData
import com.kodex.guide.presentation.settingsScreen.DialogType

@Composable
fun AccountDialog(
    dialogData: AccountDialogData,
    onDismiss: () -> Unit,
    onConfirm: (List<String>, DialogType? ) -> Unit,

    ) {
    if (dialogData.showDialog) {
        val fieldLabelList = remember {
            mutableStateListOf(*dialogData.fieldValues.toTypedArray())
        }

        AlertDialog(
            onDismissRequest = {
                onDismiss()
            },
            confirmButton = {
                Button(
                    onClick = {
                        onConfirm(fieldLabelList, dialogData.dialogType)
                    }, colors = ButtonDefaults.buttonColors(
                        containerColor = DrawerColorBlue
                    )
                ) {
                    Text(text = "Ok")
                }

                Button(
                    onClick = {
                        onDismiss()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = DrawerColorBlue
                    )
                ) {
                    Text(text = "Отмена")

                }
            },
            title = {
                Text(
                    text = dialogData.title ,
                    fontSize = 20.sp,
                    color = Color.Black
                )
            },
            text = {
                Column(Modifier.fillMaxWidth()) {
                dialogData.fieldLabel.forEachIndexed { index, name ->
                    TextField(
                        value = fieldLabelList[index],
                        onValueChange = {text->
                            fieldLabelList[index] = text
                        },
                        label = {
                            Text(
                                text = name)
                        }
                    )
                }
            }
            }
        )
    }
}




/*
@Composable
@Preview(showBackground = true)
fun ShowAccountDialog() {
    AccountDialog(

        onDismiss = {},
        onConfirm = {},
        dialogData = AccountDialogData(
            title = "Account Settings",
            fieldLabel = listOf("Personal Data", "Address", "Password")
        )
    )
}*/


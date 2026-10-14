package com.kodex.guide.ui.dialods

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.kodex.guide.ui.theme.DrawerColorBlue

@Composable
fun SettingsRestartDialog(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    title: String = "Reset Password",
    message: String = "",
    confirmButtonText: String = "Ok",

) {
    if (showDialog) {
        AlertDialog(
            onDismissRequest = {
                onDismiss()
            },
            confirmButton = {
                Button(
                    onClick = {
                        onConfirm()
                    }, colors = ButtonDefaults.buttonColors(
                        containerColor = DrawerColorBlue
                    )
                ) {
                    Text(text = confirmButtonText)
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
                    text = title,
                    fontSize = 20.sp,
                    color = Color.Black
                )
            },
            text = {
                Text(
                    text = message,
                    color = Color.Black,
                    fontSize = 16.sp
                )

            }
        )
    }
}

@Composable
@Preview(showBackground = true)
fun ShowMyDialog() {

}
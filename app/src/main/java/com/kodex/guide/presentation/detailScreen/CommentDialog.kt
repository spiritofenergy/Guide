package com.kodex.guide.presentation.detailScreen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.kodex.guide.domain.model.RatingData
import com.kodex.guide.ui.theme.DrawerColorBlue
import com.kodex.guide.presentation.castom.toFormattedDate

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CommentDialog(
    showDialog: Boolean,
    onDismiss: () -> Unit,
   // onConfirm: () -> Unit,
    ratingData: RatingData,
    confirmButtonText: String = "Yes",
    confirmButton: String = "No"
) {
    if (showDialog) {
        AlertDialog(
            onDismissRequest = {
                onDismiss()
            },
            confirmButton = {
                Button(onClick = {
                    onDismiss()
                }, colors = ButtonDefaults.buttonColors(
                    containerColor = DrawerColorBlue
                )) {
                    Text(text = confirmButton)
                }

                Button(onClick = {
                   // onConfirm()
                }, colors = ButtonDefaults.buttonColors(
                    containerColor = DrawerColorBlue
                )) {
                    Text(text = confirmButtonText)
                }
            },
            title = {
                Column (modifier = Modifier.fillMaxWidth()){
                   StarsIndicator(rating = ratingData.rating ?: 1)
                  //  Spacer(Modifier.height(4.dp))
                    Text(
                        text = ratingData.name,
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                    Text(
                        text = ratingData.timestamp.toFormattedDate(),
                        color = Color.Black,
                        fontSize = 14.sp
                    )
                }
            },
            text = {
                Text(
                    text = ratingData.message,
                    color = Color.Black,
                    fontSize = 16.sp
                )

            },
        )
    }
}
@RequiresApi(Build.VERSION_CODES.O)
@Composable
@Preview(showBackground = true)
fun ShowCommentDialog(){
    CommentDialog(
        showDialog = true,
        onDismiss = {},
       // onConfirm = {},
        ratingData = RatingData(
            name = "User",
            rating = 5,
            message = "Very good book",
            lastRating = 0
        )
        )
}
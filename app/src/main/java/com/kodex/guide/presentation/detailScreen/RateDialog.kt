package com.kodex.guide.presentation.detailScreen

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kodex.guide.domain.model.RatingData
import com.kodex.guide.ui.theme.DrawerColorBlue
import com.kodex.guide.ui.theme.Orange


@Preview(showBackground = true)
@Composable
fun RateDialog(
    ratingData: RatingData = RatingData(),
    onDismiss: () -> Unit = {},
    onSubmit: (RatingData) -> Unit = { _ ->},
    show: Boolean = false

) {
    val context = LocalContext.current
    var selectedRate by remember { mutableIntStateOf(0) }
    var messageState by remember { mutableStateOf("") }
    selectedRate = ratingData.rating ?: 1
    messageState = ratingData.message

    if (show) {
        AlertDialog(
            onDismissRequest = {
                onDismiss()
                Toast.makeText(context, "Спасибо за оценку", Toast.LENGTH_SHORT).show()
            },
            title = {
                Text(text = "Rate this Book")
            },
            text = {
                Column (modifier = Modifier.fillMaxWidth() ){
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        for (rating in 1..5) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clickable {
                                        selectedRate = rating
                                    }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = "Star",
                                    tint = if (rating <= selectedRate) {
                                        Orange
                                    } else {
                                        Color.Gray
                                    }
                                )
                            }
                        }
                    }
                    TextField(
                        value = messageState,
                        onValueChange = { newValue->
                            if (newValue.length <= 200) {
                                messageState = newValue
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(90.dp),
                        placeholder = {
                            Text(text = "Message...")
                        },
                        maxLines = 10
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onSubmit(ratingData.copy(
                            rating = selectedRate,
                            message = messageState,
                            lastRating = ratingData.rating ?: 0
                            ))
                    },colors = ButtonDefaults.buttonColors(
                        containerColor = DrawerColorBlue
                    )
                ) {
                    Text(text = "Submit")
                }
            }
        )
    }
}
@Preview(showBackground = true)
@Composable
fun ShowRatingData(){
  /*  RateDialog(
        show = true,
        onSubmit = { rating, message ->
            println("Rating: $rating, Message: $message")
        },
        onDismiss = {}
    )*/
}




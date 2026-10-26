package com.kodex.guide.ui.dialods

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kodex.guide.R
import com.kodex.guide.domain.model.RatingData
import com.kodex.guide.ui.theme.DrawerColorBlue


@Composable
    fun DialogComments(
    showDialog: Boolean,
    onDismiss: ()-> Unit,
    onConfirm: () -> Unit,
    ratingData: RatingData = RatingData(),
    confirmButtonText: String = stringResource(R.string.yes )
    ) {
        if (showDialog){
            AlertDialog(
                onDismissRequest = {
                    onDismiss()
                },
                confirmButton ={
                    Button(onClick = {
                        onConfirm()
                    }, colors = ButtonDefaults.buttonColors(
                        containerColor = DrawerColorBlue
                    )) {
                        Text(text = confirmButtonText)
                    }
                },
                title = {
                    Column(Modifier.fillMaxWidth()) {
                       // StarsIndicator(rating = ratingData.rating.id)
                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = ratingData.name,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black,
                            fontSize = 16.sp
                        )
                    }
                },
                text = {
                    Text(
                        text = ratingData.message,
                        color = Color.Black,
                        fontSize = 16.sp
                    )
                }
            )
        }
}
@Composable
@Preview(showBackground = true)
fun PrevDialogComments() {
    DialogComments(
        ratingData = RatingData(
            name = "my_adress@mail.ru",
            rating = 5,
            message = "Good very book!"
        ),
        onDismiss = {},
        onConfirm = {},
        showDialog = true,
        confirmButtonText = "Yes"
    )
}

/*showDialog: Boolean,
    onDismiss: ()-> Unit,
    ratingData: RatingData = RatingData(),
    confirmButtonText: String = stringResource(R.string.yes )*/
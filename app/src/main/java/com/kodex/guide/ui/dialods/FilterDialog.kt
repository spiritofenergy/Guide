package com.kodex.guide.ui.dialods

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.kodex.bookmarketcompose.R
import com.kodex.guide.presentation.castom.PricePickerThumb
import com.kodex.guide.presentation.castom.RadioButtonSet
import com.kodex.guide.presentation.home.HomeViewModel
import com.kodex.guide.ui.theme.DrawerColorBlue

@Composable
@Preview(showBackground = true)
fun FilterDialog(
    showDialog: Boolean = false,
    onDismiss: () -> Unit = {},
    onConfirm: () -> Unit = {},
    title: String = "Order by:",
    confirmButtonText: String = "Ok",
    viewModel: HomeViewModel = hiltViewModel()

) {

    if (showDialog) {
        AlertDialog(
            onDismissRequest = {
                onDismiss()
            },
            confirmButton = {
                Button(onClick = {
                    onConfirm()
                    viewModel.setFilter()
                },colors = ButtonDefaults.buttonColors(
                    containerColor = DrawerColorBlue
                )) {
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
                    Text(text = stringResource(R.string.cancel))
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
                val orderBySelection = stringArrayResource(R.array.order_by)[0]
                Column(modifier = Modifier.fillMaxWidth()) {
                    RadioButtonSet(
                        isFilterByTitle = viewModel.isFilterByTitle.value,
                    ) { option ->
                        viewModel.isFilterByTitle.value = option == orderBySelection
                    }

                    if (!viewModel.isFilterByTitle.value) {
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = "Price range:",
                            color = Color.Black,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(5.dp))

                        PricePickerThumb(
                            priceValue = viewModel.minPriceValue.floatValue,
                            title = "Min",
                            onValueChange = { value ->
                                viewModel.minPriceValue.floatValue = value
                            }
                        )
                        Spacer(modifier = Modifier.width(5.dp))

                        PricePickerThumb(
                            priceValue = viewModel.maxPriceValue.floatValue,
                            title = "Max",
                            onValueChange = { value ->
                                viewModel.maxPriceValue.floatValue = value
                            }
                        )
                    }
                }
            }
        )
    }
}

@Composable
@Preview(showBackground = true)
fun ShowFilterDialog() {
}

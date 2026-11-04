package com.kodex.guide.presentation.login

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.kodex.guide.ui.theme.ButtonColor
import com.kodex.guide.R
@Composable
fun RoundedCornerTextField (
    maxLines: Int = 1,
    singleLine: Boolean = true,
    isPassword: Boolean = false,
    text: String,
    label: String,
    onValueChange: (String) -> Unit
) {
    var isPasswordVisible by remember {
        mutableStateOf(false)
    }

    TextField(
        value = text,
        onValueChange = {
        onValueChange(it)
    },
        shape = RoundedCornerShape(20.dp),
        colors = TextFieldDefaults.colors(
            unfocusedContainerColor = Color.White,
            focusedContainerColor = Color.White,
            unfocusedIndicatorColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent
        ),
        modifier = Modifier
            .fillMaxWidth()
            .border(2.dp, ButtonColor, RoundedCornerShape(20.dp)),
        label = {
            Text(text = label, color =  Color.Gray)
        },
        singleLine = singleLine,
        maxLines = maxLines,
        visualTransformation = if (isPasswordVisible || !isPassword) {
            VisualTransformation.None
        }else{
            PasswordVisualTransformation()
        },
        trailingIcon = {
           if (isPassword) IconButton(
                onClick = {
                    isPasswordVisible = !isPasswordVisible
                }
            ) {
                Icon(
                    painter =  if (isPasswordVisible) {
                        painterResource(id = R.drawable.visibility_off)
                    }else{
                        painterResource(id = R.drawable.visibility)
                    },
                    contentDescription = ""
                )
            }
        }
    )
}
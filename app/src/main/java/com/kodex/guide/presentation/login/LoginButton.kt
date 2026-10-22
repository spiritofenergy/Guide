package com.kodex.guide.presentation.login

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kodex.guide.ui.theme.ButtonColor

@Composable
fun LoginButton(
    modifier: Modifier = Modifier,
    text: String,
    showLoadIndicator: Boolean = false,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        enabled = enabled && !showLoadIndicator,
        modifier = modifier.fillMaxWidth(0.5f),
        colors = ButtonDefaults.buttonColors(
            containerColor = ButtonColor
        )
    ){
        if (showLoadIndicator){
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp)
            )
        }else {
            Text(text = text)
        }
        }
}
package com.kodex.guide.presentation.banners

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit

@Composable
fun TextWithShadow(
    text: String,
    fontSize: TextUnit,
    modifier: Modifier = Modifier,
    color: Color = Color.White,
    outlineColor: Color = Color.Black,
    outlineWidth: Float = 6f,      // ~2-3dp в px
    shadowBlur: Float = 8f,
) {
    Box(modifier = modifier) {
        Text(
            text = text,
            fontSize = fontSize,
            color = outlineColor,
            style = LocalTextStyle.current.copy(
                fontWeight = FontWeight.Bold,
                drawStyle = Stroke(
                    width = outlineWidth,
                    join = StrokeJoin.Round,
                    cap = StrokeCap.Round
                )
            )
        )

        Text(
            text = text,
            fontSize = fontSize,
            color = color,
            style = LocalTextStyle.current.copy(
                fontWeight = FontWeight.Bold,
                shadow = Shadow(
                    color = outlineColor.copy(alpha = 0.6f),
                    offset = Offset(0f, 0f),
                    blurRadius = shadowBlur
                )
            )
        )

    }
}
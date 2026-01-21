package com.github.tanmayvaity.bluebytes.feature.feature_home.presentation.Components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.github.tanmayvaity.bluebytes.R
import com.github.tanmayvaity.bluebytes.ui.theme.BlueBytesTheme

@Composable
fun EmptyDevicesPlaceholder(
    modifier: Modifier = Modifier,
    message: String = "No devices nearby",
    @DrawableRes iconRes: Int = R.drawable.ic_no,
    strokeColor: Color = MaterialTheme.colorScheme.onSurfaceVariant.copy(0.5f),
    strokeWidth: Dp = 1.dp,
    dashInterval: Dp = 10.dp
) {
    val stroke = remember(strokeWidth, dashInterval) {
        Stroke(
            width = strokeWidth.value,
            pathEffect = PathEffect.dashPathEffect(
                floatArrayOf(dashInterval.value, dashInterval.value),
                0f
            )
        )
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(150.dp)
            .drawBehind {
                drawRoundRect(
                    color = strokeColor,
                    style = stroke,
                    cornerRadius = CornerRadius(16.dp.toPx())
                )
            },
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(8.dp)
        ) {
            Icon(
                painter = painterResource(iconRes),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = message,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
            )
        }
    }
}

@Preview
@Composable
private fun EmptyDevicesPlaceholderPreview() {
    BlueBytesTheme {
        EmptyDevicesPlaceholder()
    }

}
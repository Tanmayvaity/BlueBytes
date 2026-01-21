package com.github.tanmayvaity.bluebytes.core.presentation

import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.github.tanmayvaity.bluebytes.ui.theme.BlueBytesTheme

@Composable
private fun LoadingIndicator(
    strokeWidth : Dp = 3.dp,
    trackColor : Color = MaterialTheme.colorScheme.secondaryContainer,
    size : Dp = 64.dp
) {
    CircularProgressIndicator(
        strokeWidth = strokeWidth,
        trackColor = trackColor,
        modifier = Modifier.size(size)
    )
}

@Preview
@Composable
fun LoadingIndicatorPreview(modifier: Modifier = Modifier) {
    BlueBytesTheme {
        LoadingIndicator()
    }
}
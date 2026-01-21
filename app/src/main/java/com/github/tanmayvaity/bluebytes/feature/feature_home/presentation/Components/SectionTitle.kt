package com.github.tanmayvaity.bluebytes.feature.feature_home.presentation.Components

import androidx.annotation.StringRes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.github.tanmayvaity.bluebytes.R
import com.github.tanmayvaity.bluebytes.ui.theme.BlueBytesTheme

@Composable
fun SectionTitle(
    @StringRes titleRes: Int,
    modifier: Modifier = Modifier
) {
    Text(
        text = stringResource(titleRes),
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = modifier
    )
}


@Preview
@Composable
private fun SectionTitlePreview() {
    BlueBytesTheme {
        SectionTitle(
            titleRes = R.string.unknown_device
        )
    }
}
package com.github.tanmayvaity.bluebytes.feature.feature_home.presentation.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.tanmayvaity.bluebytes.R
import com.github.tanmayvaity.bluebytes.ui.theme.BlueBytesTheme

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun MainFab(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    fabText: String,
    fabIcon: ImageVector,
) {
    ExtendedFloatingActionButton(
        onClick = onClick,
        elevation = FloatingActionButtonDefaults.elevation(0.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = fabIcon,
                contentDescription = stringResource(R.string.search_icon)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = fabText
            )
        }
    }
}

@Preview
@Composable
fun MainFabPreview(modifier: Modifier = Modifier) {
    BlueBytesTheme {
        MainFab(
            fabText = stringResource(R.string.start_scanning),
            fabIcon = Icons.Default.Search
        )
    }
}


package com.github.tanmayvaity.bluebytes.feature.feature_settings.presentation

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val darkModePref by viewModel.isDarkMode.collectAsStateWithLifecycle()
    val isDarkMode = darkModePref ?: isSystemInDarkTheme()

    SettingsScreenContent(
        modifier = modifier,
        isDarkMode = isDarkMode,
        onDarkModeChange = viewModel::setDarkMode
    )
}

@Composable
private fun SettingsScreenContent(
    isDarkMode: Boolean,
    modifier: Modifier = Modifier,
    onDarkModeChange: (Boolean) -> Unit = {}
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Dark mode",
                style = MaterialTheme.typography.titleMedium
            )
            Switch(
                checked = isDarkMode,
                onCheckedChange = onDarkModeChange
            )
        }
    }
}

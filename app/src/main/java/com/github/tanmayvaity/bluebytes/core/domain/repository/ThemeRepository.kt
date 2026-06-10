package com.github.tanmayvaity.bluebytes.core.domain.repository

import kotlinx.coroutines.flow.Flow

interface ThemeRepository {
    /** null = follow the system setting (no explicit choice made yet). */
    val isDarkMode: Flow<Boolean?>
    suspend fun setDarkMode(enabled: Boolean)
}

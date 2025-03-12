package com.example.facenote.core.data.repository

import com.example.facenote.core.model.ThemeConfig
import kotlinx.coroutines.flow.Flow

interface SettingRepository {
	suspend fun setTheme(theme: ThemeConfig)
	fun getTheme(): Flow<ThemeConfig>
}
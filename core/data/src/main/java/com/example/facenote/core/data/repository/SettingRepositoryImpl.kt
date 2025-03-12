package com.example.facenote.core.data.repository

import com.example.facenote.core.datastore.FaceNotePreferencesDataSource
import com.example.facenote.core.model.ThemeConfig
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SettingRepositoryImpl @Inject constructor(
	private val preferencesDataStore: FaceNotePreferencesDataSource
): SettingRepository {
	override suspend fun setTheme(theme: ThemeConfig) {
		preferencesDataStore.setTheme(theme)
	}

	override fun getTheme(): Flow<ThemeConfig> {
		return preferencesDataStore.getTheme()
	}
}
package com.example.facenote.core.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.example.facenote.core.model.ThemeConfig
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FaceNotePreferencesDataSource @Inject constructor(
	private val userPreferences: DataStore<Preferences>
) {
	suspend fun setTheme(theme: ThemeConfig){
		userPreferences.edit { prefs ->
			prefs[PreferencesKey.THEME_CONFIG] = when(theme){
				ThemeConfig.FOLLOW_SYSTEM -> 0
				ThemeConfig.LIGHT -> 1
				ThemeConfig.DARK -> 2
			}
		}
	}

	fun getTheme(): Flow<ThemeConfig> {
		return  userPreferences.data.map { prefs ->
			when(prefs[PreferencesKey.THEME_CONFIG]){
				1 -> ThemeConfig.LIGHT
				2 -> ThemeConfig.DARK
				else -> ThemeConfig.FOLLOW_SYSTEM
			}
		}
	}

	suspend fun setDriveFolderId(folderId: String){
		userPreferences.edit { prefs ->
			prefs[PreferencesKey.DRIVE_FOLDER_ID] = folderId
		}
	}

	fun getDriveFolderId(): Flow<String?>{
		return userPreferences.data.map { prefs ->
			prefs[PreferencesKey.DRIVE_FOLDER_ID]
		}
	}

	suspend fun setLastBackup(lastBackup:Long){
		userPreferences.edit { prefs ->
			prefs[PreferencesKey.LAST_BACKUP] = lastBackup
		}
	}

	fun getLastBackup(): Flow<Long?>{
		return userPreferences.data.map { prefs ->
			prefs[PreferencesKey.LAST_BACKUP]
		}
	}
}
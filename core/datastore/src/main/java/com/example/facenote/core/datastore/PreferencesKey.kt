package com.example.facenote.core.datastore

import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

object PreferencesKey {
	val THEME_CONFIG = intPreferencesKey("themeConfig")
	val DRIVE_FOLDER_ID = stringPreferencesKey("driveFolderId")
	val LAST_BACKUP = longPreferencesKey("lastBackup")
}
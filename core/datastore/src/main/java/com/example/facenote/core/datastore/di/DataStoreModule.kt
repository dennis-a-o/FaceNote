package com.example.facenote.core.datastore.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import java.io.File
import javax.inject.Singleton

private const val USER_PREFERENCES = "facenote_user_preferences.preferences_pb"

@Module
@InstallIn(SingletonComponent::class)
object DataStoreModule {

	@Singleton
	@Provides
	internal fun providesFaceNotePreferencesDataStore(
		@ApplicationContext context: Context
	): DataStore<Preferences> {
		return PreferenceDataStoreFactory.create(
			corruptionHandler = ReplaceFileCorruptionHandler {  emptyPreferences() },
			scope = CoroutineScope(Dispatchers.IO + SupervisorJob()),
			produceFile = { File(context.dataDir, USER_PREFERENCES) }
		)
	}
}
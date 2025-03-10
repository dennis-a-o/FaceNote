package com.example.facenote.core.datastore

import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.emptyPreferences
import com.example.facenote.core.model.ThemeConfig
import junit.framework.TestCase.assertFalse
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File

class FaceNotePreferencesDataSourceTest {
	@OptIn(ExperimentalCoroutinesApi::class)
	private val testScope = TestScope(UnconfinedTestDispatcher() )

	private lateinit var subject: FaceNotePreferencesDataSource

	@get:Rule
	val tmpFolder: TemporaryFolder = TemporaryFolder.builder().assureDeletion().build()

	@Before
	fun setUp() {
		subject = FaceNotePreferencesDataSource(
			userPreferences = PreferenceDataStoreFactory.create(
				corruptionHandler = ReplaceFileCorruptionHandler { emptyPreferences() },
				scope = testScope,
				produceFile = { File(tmpFolder.newFile("facenote_user_preferences.preferences_pb"),"") }
			)
		)
	}

	@Test
	fun shouldFollowSystemThemeByDefault(){
		testScope.runTest {
			assert(subject.getTheme().first() == ThemeConfig.FOLLOW_SYSTEM)
		}
	}

	@Test
	fun shouldUseDarkThemeWhenSet(){
		testScope.runTest {
			subject.setTheme(ThemeConfig.DARK)
			assert(subject.getTheme().first() == ThemeConfig.DARK)
		}
	}

	@Test
	fun shouldUseRightThemeWhenSet(){
		testScope.runTest {
			subject.setTheme(ThemeConfig.DARK)
			assertFalse(subject.getTheme().first() == ThemeConfig.LIGHT)
		}
	}
}
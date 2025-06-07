package com.example.facenote.feature.note_search

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performTextInput
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.facenote.ui_test_hilt_manifest.HiltComponentActivity
import dagger.hilt.android.testing.BindValue
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

@HiltAndroidTest
class NoteSearchScreenKtTest {
	@get:Rule(order = 0)
	val hiltRule = HiltAndroidRule(this)

	@BindValue
	@get:Rule(order = 1)
	val tmpFolder: TemporaryFolder = TemporaryFolder.builder().assureDeletion().build()

	@get:Rule(order = 2)
	val composeTestRule = createAndroidComposeRule<HiltComponentActivity>()

	@Before
	fun setUp() {
		hiltRule.inject()
	}

	@Test
	fun whenNoSearchResult_showEmptyState(){
		composeTestRule.apply{
			setContent {
				NoteSearchScreen(hiltViewModel(), onNavigateBack = {}, onNavigateToNoteEditor = { _, _ -> })
			}

			onNodeWithTag("noSearchResult").assertExists()
		}
	}

	@Test
	fun whenSearchFieldNotEmpty_showClearButton(){
		composeTestRule.apply {
			setContent {
				NoteSearchScreen(hiltViewModel(), onNavigateBack = {}, onNavigateToNoteEditor = { _, _ -> })
			}

			onNodeWithTag("searchInput").performTextInput("test")
			onNodeWithTag("clearQuery").assertExists()
		}
	}
}
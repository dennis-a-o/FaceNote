package com.example.facenote.feature.notes

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.example.facenote.ui_test_hilt_manifest.HiltComponentActivity
import dagger.hilt.android.testing.BindValue
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

@HiltAndroidTest
class NotesScreenKtTest {
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
	fun noteListWhenInitialized(){
		composeTestRule.apply {
			setContent {
				NotesScreen({ _, _ -> }, {}, {}, {}, {}, {})
			}

			onNodeWithTag("notesList").assertExists()
		}
	}

	@Test
	fun navigation_whenMenuClicked_showNavigationDrawer(){
		composeTestRule.apply{
			setContent {
				NotesScreen({ _, _ -> }, {}, {}, {}, {}, {})
			}

			onNodeWithTag("showDrawerButton").performClick()
			onNodeWithTag("notesDrawer").assertExists()
		}
	}

	@Test
	fun createNote_whenAddNewNote_showNoteTypeOptions(){
		composeTestRule.apply {
			setContent {
				NotesScreen({ _, _ -> }, {}, {}, {}, {}, {})
			}

			onNodeWithTag("floatingButton").performClick()
			onNodeWithText("List").assertExists()
			onNodeWithText("Text").assertExists()
		}
	}

}
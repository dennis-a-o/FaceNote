package com.example.facenote.feature.note_editor

import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
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
class NoteEditorScreenKtTest {
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
	fun initially_showEmptyEdior(){
		composeTestRule.apply {
			setContent {
				NoteEditorScreen({},{_,_,_->},{_,_->}, hiltViewModel())
			}

			onNodeWithTag("titleTextField").assertTextContains("")
			onNodeWithTag("richTextEditor").assertTextContains("")
		}
	}

	@Test
	fun onAddImage_showOptionBottomSheet(){
		composeTestRule.apply {
			setContent {
				NoteEditorScreen({},{_,_,_->},{_,_->}, hiltViewModel())
			}

			onNodeWithTag("addImageButton").performClick()
			onNodeWithTag("addImageBottomSheet").assertExists()
		}
	}

	@Test
	fun onAddBackground_showBackgroundBottomSheet(){
		composeTestRule.apply {
			setContent {
				NoteEditorScreen({},{_,_,_->},{_,_->}, hiltViewModel())
			}

			onNodeWithTag("backgroundButton").performClick()
			onNodeWithTag("backgroundBottomSheet").assertExists()
		}
	}

	@Test
	fun onAddFormat_showFormatBottomSheet(){
		composeTestRule.apply {
			setContent {
				NoteEditorScreen({},{_,_,_->},{_,_->}, hiltViewModel())
			}

			onNodeWithTag("formatButton").performClick()
			onNodeWithTag("formatBottomSheet").assertExists()
		}
	}
}
package com.example.facenote.feature.backup

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class BackupScreenKtTest {
	@get:Rule
	val composeTestRule = createComposeRule()

	@Test
	fun backupView_should_show_GoogleSignIn_button_when_not_signedIn(){
		val state = BackupState()
		composeTestRule.setContent {
			BackupView(state,{},{},{},{},{})
		}

		composeTestRule.onNodeWithText("Google SignIn").assertExists()
	}

	@Test
	fun verify_restoreView_shows_no_backups_found_when_there_is_no_backup_files() {
		composeTestRule.setContent {
			RestoreView(BackupState(), {}, {}, {}, {})
		}
		composeTestRule.onNodeWithText("No backups found").assertExists()

	}
}


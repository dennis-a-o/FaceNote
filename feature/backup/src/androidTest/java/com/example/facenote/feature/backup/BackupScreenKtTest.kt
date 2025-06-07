package com.example.facenote.feature.backup

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

class BackupScreenKtTest {
	@get:Rule(order = 0)
	val tmpFolder: TemporaryFolder = TemporaryFolder.builder().assureDeletion().build()

	@get:Rule(order = 1)
	val composeTestRule = createAndroidComposeRule<ComponentActivity>()

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
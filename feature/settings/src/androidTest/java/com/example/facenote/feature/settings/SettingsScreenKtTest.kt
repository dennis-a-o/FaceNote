package com.example.facenote.feature.settings

import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import com.example.facenote.ui_test_hilt_manifest.HiltComponentActivity
import dagger.hilt.android.testing.BindValue
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

@HiltAndroidTest
class SettingsScreenKtTest {
	@get:Rule(order = 0)
	val hiltRule = HiltAndroidRule(this)

	@BindValue
	@get:Rule(order = 1)
	val tmpFolder: TemporaryFolder = TemporaryFolder.builder().assureDeletion().build()

	@get:Rule(order = 2)
	val composeTestRule = createAndroidComposeRule<HiltComponentActivity>()

	@Before
	fun setup() {
		hiltRule.inject()
	}

	@Test
	fun when_loaded_default_settings_is_displayed() {
		composeTestRule.setContent {
			SettingsScreen({})
		}

		composeTestRule.onNodeWithTag("RadioButton:FOLLOW_SYSTEM").assertExists()
		composeTestRule.onNodeWithTag("RadioButton:LIGHT").assertExists()
		composeTestRule.onNodeWithTag("RadioButton:DARK").assertExists()

	}

	@Test
	fun when_no_theme_default_theme_is_selected(){
		composeTestRule.setContent {
			SettingsScreen({})
		}

		composeTestRule.onNodeWithTag("RadioButton:FOLLOW_SYSTEM").assertIsSelected()
	}

}
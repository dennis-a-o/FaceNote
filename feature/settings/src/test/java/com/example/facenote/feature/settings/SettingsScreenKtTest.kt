package com.example.facenote.feature.settings

import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onRoot
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.facenote.ui_test_hilt_manifest.HiltComponentActivity
import com.github.takahirom.roborazzi.captureRoboImage
import dagger.hilt.android.testing.BindValue
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import org.robolectric.annotation.LooperMode

@RunWith(AndroidJUnit4::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(application = HiltTestApplication::class, sdk = [34], qualifiers = "w610dp-h1000dp-480dpi")
@LooperMode(LooperMode.Mode.PAUSED)
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
	fun captureSettingScreen(){
		composeTestRule.setContent {
			SettingsScreen({})
		}

 		composeTestRule.onRoot().captureRoboImage(
			 filePath = "src/test/screenshots/captureSettingScreen.png"
		 )
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
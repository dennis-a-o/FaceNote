package com.example.facenote

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.espresso.Espresso
import androidx.test.espresso.NoActivityResumedException
import dagger.hilt.android.testing.BindValue
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

@HiltAndroidTest
class NavigationTest {

	@get:Rule(order = 0)
	val hiltRule = HiltAndroidRule(this)

	@BindValue
	@get:Rule(order = 1)
	val tmpFolder: TemporaryFolder = TemporaryFolder.builder().assureDeletion().build()

	@get:Rule(order = 2)
	val composeTestRule = createAndroidComposeRule<MainActivity>()

	@Before
	fun setup() = hiltRule.inject()

	@Test
	fun firstScreen_isNotes(){
		composeTestRule.apply {
			onNodeWithTag("notesScreen").assertExists()
		}
	}

	@Test
	fun navigationDrawer_whenNavigateToOtherScreenThenBack_returnToPreviousScreen(){
		composeTestRule.apply {
			onNodeWithTag("showDrawerButton").performClick()

			onNodeWithText("Setting").performClick()

			Espresso.pressBack()

			onNodeWithTag("notesScreen").assertExists()
		}
	}

	@Test(expected = NoActivityResumedException::class)
	fun homeDestination_back_quitsApp(){
		composeTestRule.apply {
			//GIVEN you are at start destination
			onNodeWithTag("notesScreen").assertExists()
			//WHEN user go back
			Espresso.pressBack()
			//THEN app quits
		}
	}
}
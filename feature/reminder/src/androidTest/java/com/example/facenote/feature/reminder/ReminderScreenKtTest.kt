package com.example.facenote.feature.reminder

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.facenote.ui_test_hilt_manifest.HiltComponentActivity
import com.jakewharton.threetenabp.AndroidThreeTen
import dagger.hilt.android.testing.BindValue
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

@HiltAndroidTest
class ReminderScreenKtTest {
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
		AndroidThreeTen.init(composeTestRule.activity)
	}

	@Test
	fun whenSelectDate_showCalenderDialog(){
		composeTestRule.apply{
			setContent {
				ReminderScreen(hiltViewModel()) { }
			}

			onNodeWithTag("datePicker").performClick()
			onNodeWithTag("reminderDatePickerDialog").assertExists()
		}
	}

	@Test
	fun whenSelectTime_showTimeDialog(){
		composeTestRule.apply{
			setContent {
				ReminderScreen(hiltViewModel()) { }
			}

			onNodeWithTag("timePicker").performClick()
			onNodeWithTag("reminderTimePickerDialog").assertExists()
		}
	}

	@Test
	fun whenClickRepeatInterval_showDropdownOptions(){
		composeTestRule.apply{
			setContent {
				ReminderScreen(hiltViewModel()) { }
			}

			onNodeWithTag("repeatInterval").performClick()
			onNodeWithText("None").assertExists()
			onNodeWithText("Daily").assertExists()
			onNodeWithText("Weekly").assertExists()
			onNodeWithText("Monthly").assertExists()
			onNodeWithText("Yearly").assertExists()
		}
	}
}
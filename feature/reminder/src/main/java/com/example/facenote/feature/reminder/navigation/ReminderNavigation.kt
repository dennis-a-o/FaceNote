package com.example.facenote.feature.reminder.navigation

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.facenote.feature.reminder.ReminderScreen
import com.example.facenote.feature.reminder.ReminderViewModel

const val NOTE_ID = "noteId"
const val REMIND_AT = "remindAt"
const val REMINDER_ROUTE_BASE = "reminder"
const val REMINDER_ROUTE = "$REMINDER_ROUTE_BASE/{$NOTE_ID}/{$REMIND_AT}"

fun NavController.navigateToReminder(
	noteId: Long,
	remindAt: Long = 0L,
	navOptions: NavOptions? = null
){
	val route = "$REMINDER_ROUTE_BASE/$noteId/$remindAt"
	navigate(route,navOptions)
}

fun NavGraphBuilder.reminderScreen(
	onNavigateBack: () -> Unit
){
	composable(
		route = REMINDER_ROUTE,
		arguments = listOf(
			navArgument(NOTE_ID){
				type = NavType.LongType
			},
			navArgument(REMIND_AT){
				type = NavType.LongType
			}
		)
	){ navBackStackEntry ->
		val viewModel: ReminderViewModel = hiltViewModel(navBackStackEntry)
		ReminderScreen(
			viewModel = viewModel,
			onNavigateBack = onNavigateBack
		)
	}
}
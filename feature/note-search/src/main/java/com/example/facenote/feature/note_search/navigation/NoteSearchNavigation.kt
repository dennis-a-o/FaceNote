package com.example.facenote.feature.note_search.navigation

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.facenote.feature.note_search.NoteSearchScreen
import com.example.facenote.feature.note_search.NoteSearchViewModel

const val NOTE_STATE = "noteState"
const val NOTE_SEARCH_ROUTE_BASE = "note_search"
const val NOTE_SEARCH_ROUTE = "$NOTE_SEARCH_ROUTE_BASE/{$NOTE_STATE}"

fun  NavController.navigateToNoteSearch(
	noteState: String? = null,
	navOptions: NavOptions? = null
) {
	val route = "$NOTE_SEARCH_ROUTE_BASE/$noteState"
	navigate(route,navOptions)
}

fun NavGraphBuilder.noteSearchScreen(
	onNavigateBack: () -> Unit,
	onNavigateToNoteEditor: (Long, Boolean) -> Unit
){
	composable (
		route = NOTE_SEARCH_ROUTE,
		arguments = listOf(
			navArgument(NOTE_STATE){
				nullable = true
				defaultValue = null
				type = NavType.StringType
			}
		)
	) { backStackEntry->
		val viewModel: NoteSearchViewModel = hiltViewModel(backStackEntry)
		NoteSearchScreen(
			viewModel = viewModel,
			onNavigateBack  = onNavigateBack,
			onNavigateToNoteEditor = onNavigateToNoteEditor
		)
	}
}
package com.example.facenote.feature.notes.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.example.facenote.feature.notes.NotesScreen

const val NOTES_ROUTE = "notes_route"

fun NavController.navigateTONotesRoute(
	navOptions: NavOptions? = null
){
	navigate(NOTES_ROUTE,navOptions)
}

fun NavGraphBuilder.notesScreen(
	onNavigateToNoteEditor: (Long, Boolean) -> Unit,
	onNavigateToNoteSearch: (String?) -> Unit,
	onNavigateToArchive: () -> Unit,
	onNavigateToTrash: () -> Unit,
	onNavigateToSetting: () -> Unit,
	onNavigateToBackUp: () -> Unit,
){
	composable(
		route = NOTES_ROUTE,
	){
		NotesScreen(
			onNavigateToNoteEditor = onNavigateToNoteEditor,
			onNavigateToNoteSearch = onNavigateToNoteSearch,
			onNavigateToArchive = onNavigateToArchive,
			onNavigateToTrash = onNavigateToTrash,
			onNavigateToSetting = onNavigateToSetting,
			onNavigateToBackUp = onNavigateToBackUp,
		)
	}
}
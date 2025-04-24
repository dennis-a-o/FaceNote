package com.example.facenote.feature.archive.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.example.facenote.feature.archive.ArchiveScreen

const val NOTE_ARCHIVE_ROUTE = "archive"

fun NavController.navigateToArchive(
	navOptions: NavOptions? = null
){
	navigate(NOTE_ARCHIVE_ROUTE, navOptions)
}

fun NavGraphBuilder.archiveScreen(
	onNavigateBack: () -> Unit,
	onNavigateToNoteEditor: (Long, Boolean) -> Unit,
	onNavigateToNoteSearch: (String) -> Unit
){
	composable (
		route = NOTE_ARCHIVE_ROUTE
	){
		ArchiveScreen(
			onNavigateBack = onNavigateBack,
			onNavigateToNoteEditor = onNavigateToNoteEditor,
			onNavigateToNoteSearch = onNavigateToNoteSearch
		)
	}
}
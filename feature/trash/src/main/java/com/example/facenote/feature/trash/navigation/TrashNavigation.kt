package com.example.facenote.feature.trash.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.example.facenote.feature.trash.TrashScreen

const val NOTE_TRASH_ROUTE = "trash"

fun NavController.navigateToTrash(
	navOptions: NavOptions? = null
){
	navigate(NOTE_TRASH_ROUTE, navOptions)
}

fun NavGraphBuilder.trashScreen(
	onNavigateBack: () -> Unit,
	onNavigateToNoteEditor: (Long, Boolean) -> Unit,
){
	composable (
		route = NOTE_TRASH_ROUTE
	){
		TrashScreen(
			onNavigateBack = onNavigateBack,
			onNavigateToNoteEditor = onNavigateToNoteEditor
		)
	}
}
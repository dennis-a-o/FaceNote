package com.example.facenote.feature.note_editor.navigation

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.example.facenote.feature.note_editor.NoteEditorScreen
import com.example.facenote.feature.note_editor.NoteEditorVIewModel

const val NOTE_ID = "noteId"
const val IS_CHECKLIST= "isCheckList"
const val NOTE_EDITOR_ROUTE_BASE = "note_editor_route"
private const val DEEP_LINK_SCHEME_AND_HOST = "app://com.example.facenote"
const val NOTE_EDITOR_ROUTE = "$NOTE_EDITOR_ROUTE_BASE/{$NOTE_ID}/{$IS_CHECKLIST}"

fun NavController.navigateToNoteEditor(
	noteId: Long = -1,
	isCheckList: Boolean = false,
	navOptions: NavOptions? = null
){
	val route ="${NOTE_EDITOR_ROUTE_BASE}/$noteId/$isCheckList"
	navigate(route,navOptions)
}

fun NavGraphBuilder.noteEditorScreen(
	onNavigateBack:() -> Unit,
	onNavigateToNoteGallery: (Long, Int, String) -> Unit,
	onNavigateToReminder: (Long,Long) -> Unit
){
	composable(
		route = NOTE_EDITOR_ROUTE,
		arguments = listOf(
			navArgument(NOTE_ID){
				type = NavType.LongType
			},
			navArgument(IS_CHECKLIST){
				type = NavType.BoolType
			}
		),
		deepLinks = listOf(
			navDeepLink{
				uriPattern = "$DEEP_LINK_SCHEME_AND_HOST/$NOTE_EDITOR_ROUTE_BASE/{$NOTE_ID}/{$IS_CHECKLIST}"
			}
		)
	){  backStackEntry ->
		val viewModel: NoteEditorVIewModel = hiltViewModel(backStackEntry)

		NoteEditorScreen(
			onNavigateBack = onNavigateBack,
			onNavigateToNoteGallery = onNavigateToNoteGallery,
			onNavigateToReminder = onNavigateToReminder,
			viewModel = viewModel
		)
	}
}
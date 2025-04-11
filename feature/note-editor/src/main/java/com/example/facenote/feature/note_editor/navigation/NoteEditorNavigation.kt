package com.example.facenote.feature.note_editor.navigation

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.facenote.feature.note_editor.NoteEditorScreen
import com.example.facenote.feature.note_editor.NoteEditorVIewModel

const val NOTE_ID = "noteId"
const val IS_CHECKLIST= "isCheckList"
const val NOTE_EDITOR_ROUTE = "note_editor_route"

fun NavController.navigateToNoteEditor(
	noteId: Long? = null,
	isCheckList: Boolean = false,
	navOptions: NavOptions
){
	val route = if(noteId != null) {
		"${NOTE_EDITOR_ROUTE}?${NOTE_ID}=$noteId?${IS_CHECKLIST}=$isCheckList"
	} else {
		NOTE_EDITOR_ROUTE
	}
	navigate(route,navOptions)
}

fun NavGraphBuilder.noteEditorScreen(
	onNavigateBack:() -> Unit,
	onNavigateToNoteGallery: (Long, Int, String) -> Unit
){
	composable(
		route = NOTE_EDITOR_ROUTE,
		/*arguments = listOf(
			navArgument(NOTE_ID){
				defaultValue = -1
				type = NavType.LongType
			},
			navArgument(IS_CHECKLIST){
				defaultValue = false
				type = NavType.BoolType
			}
		)*/
	){  backStackEntry ->
		val viewModel: NoteEditorVIewModel = hiltViewModel(backStackEntry)

		NoteEditorScreen(
			onNavigateBack = onNavigateBack,
			onNavigateToNoteGallery = onNavigateToNoteGallery,
			viewModel = viewModel
		)
	}
}
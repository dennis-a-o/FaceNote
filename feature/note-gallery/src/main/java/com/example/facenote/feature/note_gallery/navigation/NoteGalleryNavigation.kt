package com.example.facenote.feature.note_gallery.navigation

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.facenote.feature.note_gallery.NoteGalleryScreen
import com.example.facenote.feature.note_gallery.NoteGalleryViewModel

const val NOTE_ID = "noteId"
const val SELECTED_IMAGE_INDEX = "selectedImageIndex"
const val NOTE_STATE = "noteState"
const val NOTE_GALLERY_ROUTE_BASE = "note_gallery_route"
const val NOTE_GALLERY_ROUTE = "$NOTE_GALLERY_ROUTE_BASE/{$NOTE_ID}/{$SELECTED_IMAGE_INDEX}/{$NOTE_STATE}"

fun NavController.navigateToNoteGallery(
	noteId: Long,
	selectedImageIndex: Int,
	noteState: String,
	navOptions: NavOptions? = null
){
	val route = "$NOTE_GALLERY_ROUTE_BASE/${noteId}/${selectedImageIndex}/${noteState}"
	navigate(route, navOptions)
}

fun NavGraphBuilder.noteGalleryScreen(
	onNavigateBack: () -> Unit
){
	composable(
		route = NOTE_GALLERY_ROUTE,
		arguments = listOf(
			navArgument(NOTE_ID){
				type = NavType.LongType
			},
			navArgument(SELECTED_IMAGE_INDEX){
				type = NavType.IntType
			},
			navArgument(NOTE_STATE){
				type = NavType.StringType
			}
		)
	){ backStackEntry ->
		val viewModel: NoteGalleryViewModel = hiltViewModel(backStackEntry)
		NoteGalleryScreen(onNavigateBack = onNavigateBack, viewModel = viewModel)
	}
}
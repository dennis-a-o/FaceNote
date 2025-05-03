package com.example.facenote.core.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.example.facenote.feature.archive.navigation.archiveScreen
import com.example.facenote.feature.archive.navigation.navigateToArchive
import com.example.facenote.feature.note_editor.navigation.navigateToNoteEditor
import com.example.facenote.feature.note_editor.navigation.noteEditorScreen
import com.example.facenote.feature.note_gallery.navigation.navigateToNoteGallery
import com.example.facenote.feature.note_gallery.navigation.noteGalleryScreen
import com.example.facenote.feature.note_search.navigation.navigateToNoteSearch
import com.example.facenote.feature.note_search.navigation.noteSearchScreen
import com.example.facenote.feature.notes.navigation.NOTES_ROUTE
import com.example.facenote.feature.notes.navigation.notesScreen
import com.example.facenote.feature.reminder.navigation.navigateToReminder
import com.example.facenote.feature.reminder.navigation.reminderScreen
import com.example.facenote.feature.settings.navigation.navigateToSettings
import com.example.facenote.feature.settings.navigation.settingsScreen
import com.example.facenote.feature.trash.navigation.navigateToTrash
import com.example.facenote.feature.trash.navigation.trashScreen

@Composable
fun FaceNoteNavHost(
	modifier: Modifier = Modifier,
	navHostController: NavHostController = rememberNavController(),
	startDestination: String = NOTES_ROUTE,
){
	NavHost(
		navController = navHostController,
		startDestination = startDestination,
		modifier = modifier
	){
		notesScreen(
			onNavigateToNoteEditor = navHostController::navigateToNoteEditor,
			onNavigateToNoteSearch = navHostController::navigateToNoteSearch,
			onNavigateToArchive = navHostController::navigateToArchive,
			onNavigateToTrash = navHostController::navigateToTrash,
			onNavigateToSetting = navHostController::navigateToSettings,
			onNavigateToBackUp = {}
		)

		noteEditorScreen(
			onNavigateBack = { navHostController.navigateUp() },
			onNavigateToNoteGallery = {noteId,selectedImageIndex, noteState->
				navHostController.navigateToNoteGallery(
					noteId,
					selectedImageIndex,
					noteState
				)
			},
			onNavigateToReminder = navHostController::navigateToReminder,
		)

		noteGalleryScreen(
			onNavigateBack = { navHostController.navigateUp() },
		)

		noteSearchScreen(
			onNavigateBack = { navHostController.navigateUp() },
			onNavigateToNoteEditor =  navHostController::navigateToNoteEditor
		)

		archiveScreen(
			onNavigateBack = { navHostController.navigateUp() },
			onNavigateToNoteEditor = navHostController::navigateToNoteEditor,
			onNavigateToNoteSearch = navHostController::navigateToNoteSearch
		)

		trashScreen(
			onNavigateBack = { navHostController.navigateUp() },
			onNavigateToNoteEditor = navHostController::navigateToNoteEditor
		)

		settingsScreen(
			onNavigateBack = { navHostController.navigateUp() }
		)

		reminderScreen {navHostController.navigateUp() }
	}

}
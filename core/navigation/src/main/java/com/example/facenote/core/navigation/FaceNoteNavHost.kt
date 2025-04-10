package com.example.facenote.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.example.facenote.feature.note_editor.navigation.NOTE_EDITOR_ROUTE
import com.example.facenote.feature.note_editor.navigation.noteEditorScreen

@Composable
fun FaceNoteNavHost(
	modifier: Modifier = Modifier,
	navHostController: NavHostController = rememberNavController(),
	startDestination: String = NOTE_EDITOR_ROUTE,
){
	NavHost(
		navController = navHostController,
		startDestination = startDestination,
		modifier = modifier
	){
		noteEditorScreen(
			onNavigate = {}
		)
	}
}
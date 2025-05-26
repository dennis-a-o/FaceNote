package com.example.facenote.feature.backup.navigation

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import androidx.work.Operation
import com.example.facenote.feature.backup.BackupScreen
import com.example.facenote.feature.backup.BackupViewModel

const val IN_PROGRESS= "inProgress"
const val SHOW_BACKUP_FILES = "showBackupFiles"
const val BACKUP_ROUTE_ROOT = "backup"
const val DEEP_LINK_SCHEME_AND_HOST = "app://com.example.facenote"
const val BACKUP_ROUTE = "$BACKUP_ROUTE_ROOT/{$IN_PROGRESS}/{$SHOW_BACKUP_FILES}"


fun NavController.navigateToBackup(
	inProgress: Boolean = false,
	showBackupFiles: Boolean = false,
	navOptions: NavOptions? = null
){
	val route = "$BACKUP_ROUTE_ROOT/$inProgress/$showBackupFiles"

	navigate(route, navOptions)
}

fun NavGraphBuilder.backupScreen(
	onNavigateBack: () -> Unit
) {
	composable(
		route = BACKUP_ROUTE,
		arguments = listOf(
			navArgument (IN_PROGRESS){
				type = NavType.BoolType
			},
			navArgument (SHOW_BACKUP_FILES){
				type = NavType.BoolType
			}
		),
		deepLinks = listOf(
			navDeepLink{
				uriPattern = "$DEEP_LINK_SCHEME_AND_HOST/$BACKUP_ROUTE"
			}
		)
	) { backStackEntry ->

		val viewModel: BackupViewModel = hiltViewModel(backStackEntry)

		BackupScreen(
			onNavigateBack = onNavigateBack,
			viewModel = viewModel
		)
	}
}

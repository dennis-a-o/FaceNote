package com.example.facenote.feature.settings.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.example.facenote.feature.settings.SettingsScreen

const val SETTING_ROUTE = "setting"

fun NavController.navigateToSettings(
	navOptions: NavOptions? = null
){
	navigate(SETTING_ROUTE, navOptions)
}

fun NavGraphBuilder.settingsScreen(
	onNavigateBack: () -> Unit
){
	composable(route = SETTING_ROUTE){
		SettingsScreen(onNavigateBack = onNavigateBack)
	}
}
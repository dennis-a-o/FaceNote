package com.example.facenote

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import com.example.facenote.core.model.ThemeConfig
import com.example.facenote.core.ui.theme.FaceNoteTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
	private val viewModel: MainActivityViewModel by viewModels()

	override fun onCreate(savedInstanceState: Bundle?) {
		val splashScreen = installSplashScreen()
		enableEdgeToEdge()
		super.onCreate(savedInstanceState)

		var uiState: MainActivityUiState by mutableStateOf(MainActivityUiState.Loading)

		lifecycleScope.launch {
			viewModel.uiState.onEach { uiState = it }.collect()
		}

		splashScreen.setKeepOnScreenCondition{
			when(uiState){
				MainActivityUiState.Loading -> true
				is MainActivityUiState.Success -> false
			}
		}

		setContent {
			FaceNoteTheme(
				darkTheme = shouldUseDarkTheme(uiState)
			) {
				FaceNoteApp()
			}
		}
	}

	@Composable
	private fun shouldUseDarkTheme(uiState: MainActivityUiState): Boolean = when(uiState){
		MainActivityUiState.Loading -> isSystemInDarkTheme()
		is MainActivityUiState.Success -> when(uiState.themeConfig){
			ThemeConfig.FOLLOW_SYSTEM -> isSystemInDarkTheme()
			ThemeConfig.DARK -> true
			ThemeConfig.LIGHT -> false
		}
	}
}



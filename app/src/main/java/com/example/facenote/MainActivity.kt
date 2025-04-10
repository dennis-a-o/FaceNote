package com.example.facenote

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import com.example.facenote.core.ui.theme.FaceNoteTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
	override fun onCreate(savedInstanceState: Bundle?) {
		installSplashScreen()
		enableEdgeToEdge()
		super.onCreate(savedInstanceState)
		setContent {
			FaceNoteTheme(
				darkTheme = isSystemInDarkTheme()
			) {
				FaceNoteApp()
			}
		}
	}
}


package com.example.facenote.core.ui.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag

@Composable
fun NoteProgressIndicator(){
	Box (
		modifier = Modifier
			.testTag("progressIndicator")
			.fillMaxSize(),
		contentAlignment = Alignment.Center
	){
		CircularProgressIndicator()
	}
}
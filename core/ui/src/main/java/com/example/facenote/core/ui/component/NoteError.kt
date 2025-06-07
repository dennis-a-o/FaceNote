package com.example.facenote.core.ui.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp

@Composable
fun NoteError(
	title: String = "Error",
	message: String = "Something went wrong",
	onAction: () -> Unit = {}
) {
	Box (
		modifier = Modifier
			.testTag("error")
			.fillMaxSize()
			.fillMaxHeight(),
		contentAlignment = Alignment.Center
	){
		Column (
			horizontalAlignment = Alignment.CenterHorizontally
		){
			Text(
				text = title,
				style = MaterialTheme.typography.titleMedium
			)
			Spacer(Modifier.height(8.dp))
			Text(
				text = message,
				style = MaterialTheme.typography.bodyMedium
			)
			Spacer(Modifier.height(8.dp))
			Button(onClick = { onAction() }) {
				Text(text = "Retry")
			}
		}

	}
}
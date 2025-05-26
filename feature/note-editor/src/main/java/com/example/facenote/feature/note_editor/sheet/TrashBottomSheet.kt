package com.example.facenote.feature.note_editor.sheet

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.facenote.core.ui.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrashBottomSheet(
	onDismiss: () -> Unit,
	onRestore: ()-> Unit,
	onDeleteForever: () -> Unit
){
	var showConfirmDialog by remember { mutableStateOf(false)  }

	if(showConfirmDialog) {
		AlertDialog(
			onDismissRequest = { showConfirmDialog = false; },
			confirmButton = {
				TextButton(onClick = { showConfirmDialog = false; onDeleteForever() }) {
					Text(stringResource(R.string.ok))
				}
			},
			dismissButton = {
				TextButton(onClick = { showConfirmDialog = false; onDismiss() }) {
					Text(stringResource(R.string.cancel))
				}
			},
			text = { Text(stringResource(R.string.delete_this_note)) }
		)
	}

	ModalBottomSheet(
		onDismissRequest = onDismiss,
	) {
		Column(
			modifier = Modifier.fillMaxWidth()
		) {
			Row (
				modifier = Modifier
					.clickable {
						onRestore()
					}
					.padding(16.dp)
					.fillMaxWidth()
				,
				verticalAlignment = Alignment.CenterVertically
			){
				Icon(
					painter = painterResource(id = R.drawable.ic_restore),
					contentDescription = ""
				)
				Spacer(Modifier.width(8.dp))
				Text(
					text = stringResource(R.string.restore),
					style = MaterialTheme.typography.bodyLarge
				)
			}
			Row (
				modifier = Modifier
					.clickable {
						showConfirmDialog = true
					}
					.padding(16.dp)
					.fillMaxWidth(),
				verticalAlignment = Alignment.CenterVertically
			){
				Icon(
					painter = painterResource(id = R.drawable.ic_image_outlined),
					contentDescription = ""
				)
				Spacer(Modifier.width(8.dp))
				Text(
					text = stringResource(R.string.delete_forever),
					style = MaterialTheme.typography.bodyLarge
				)
			}
		}
	}
}
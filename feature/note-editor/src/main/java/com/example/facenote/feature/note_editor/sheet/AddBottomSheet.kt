package com.example.facenote.feature.note_editor.sheet

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.facenote.core.ui.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddBottomSheet(
	onDismiss: () -> Unit,
	onClickAddImage: () -> Unit,
	onClickTakePhoto: () -> Unit
) {
	ModalBottomSheet(
		onDismissRequest = onDismiss,
	) {
		Column(
			modifier = Modifier.fillMaxWidth()
		) {
			Row (
				modifier = Modifier
					.clickable {
						onClickTakePhoto()
					}
					.padding(16.dp)
					.fillMaxWidth()
					,
				verticalAlignment = Alignment.CenterVertically
			){
				Icon(
					painter = painterResource(id = R.drawable.ic_photo_camera_outlined ),
					contentDescription = ""
				)
				Spacer(Modifier.width(8.dp))
				Text(
					text = "Take photo",
					style = MaterialTheme.typography.bodyLarge
				)
			}
			Row (
				modifier = Modifier
					.clickable {
						onClickAddImage()
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
					text = "Add image",
					style = MaterialTheme.typography.bodyLarge
				)
			}
		}
	}
}
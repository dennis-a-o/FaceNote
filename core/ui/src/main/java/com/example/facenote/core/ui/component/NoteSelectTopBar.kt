package com.example.facenote.core.ui.component

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.facenote.core.ui.R
import com.example.facenote.core.ui.model.SelectState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteSelectTopBar(
	selectState: SelectState,
	onCancel: () -> Unit,
	onClickPin: () -> Unit,
	onClickArchive:() -> Unit,
	onClickDelete:() -> Unit
){
	TopAppBar(
		title = { },
		modifier = Modifier.shadow(1.dp),
		navigationIcon = {
			Row (verticalAlignment = Alignment.CenterVertically){
				IconButton(onClick = onCancel) {
					Icon(
						painter = painterResource(R.drawable.ic_close),
						contentDescription = "Cancel"
					)
				}
				Spacer(Modifier.width(8.dp))
				Text(text = "${selectState.selected.size}")
			}
		},
		actions = {
			IconButton(onClick = onClickPin) {
				if (selectState.pin) {
					Icon(
						painter = painterResource(R.drawable.ic_push_pin_outlined),
						contentDescription = "Pin"
					)
				} else {
					Icon(
						painter = painterResource(R.drawable.ic_push_pin_filled),
						contentDescription = "Pin",
						tint = MaterialTheme.colorScheme.primary
					)
				}
			}
			IconButton(onClick = onClickArchive) {
				Icon(
					painter = painterResource(R.drawable.ic_archive_outline),
					contentDescription = "Archive"
				)
			}
			IconButton(onClick = onClickDelete) {
				Icon(
					painter = painterResource(R.drawable.ic_delete_outline),
					contentDescription = "Delete"
				)
			}
		}
	)
}
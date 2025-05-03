package com.example.facenote.core.ui.component

import androidx.annotation.FloatRange
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ElevatedFilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.DefaultShadowColor
import androidx.compose.ui.graphics.RenderEffect
import androidx.compose.ui.graphics.Shader
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.example.facenote.core.ui.R
import com.example.facenote.core.ui.model.NoteUi
import com.example.facenote.core.ui.util.AssetsUtil
import com.example.facenote.core.ui.util.DateTimeUtil

@Composable
fun NoteItem(
	note: NoteUi,
	onClick: (NoteUi) -> Unit,
	onLongClick: (NoteUi) -> Unit,
	isSelected: Boolean,
	modifier: Modifier = Modifier,
){
	Box (
		modifier = modifier
			.clip(RoundedCornerShape(16.dp))
			.border(0.1.dp, Color.Black.copy(alpha = 0.2f), RoundedCornerShape(16.dp))
			.background(
				color =  MaterialTheme.colorScheme.surface,
				shape = RoundedCornerShape(16.dp)
			)
			.border(
				width = if (isSelected) 4.dp else 0.dp,
				color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Unspecified,
				shape = RoundedCornerShape(16.dp)
			)
			.combinedClickable(
				onClick = {
					onClick(note)
				},
				onLongClick = {
					onLongClick(note)
				}
			)
			.fillMaxWidth()
			.wrapContentHeight()
	){
		if (note.background.isNotEmpty()) {
			AssetsUtil.rememberBitmapFromAsset(note.background)?.asImageBitmap()
				?.let { bitmap ->
					Image(
						bitmap = bitmap,
						contentDescription = "",
						modifier = Modifier
							.matchParentSize()
							.clip(RoundedCornerShape(16.dp)),
						contentScale = ContentScale.FillBounds
					)
				}
		}else if(note.color.toArgb() != 0){
			Box(
				modifier = Modifier
					.background(note.color.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
					.matchParentSize()
			)
		}

		Column (
			modifier = Modifier
				.fillMaxWidth()
				.padding(16.dp)
		){
			Text(
				text = note.title,
				style = MaterialTheme.typography.titleMedium,
				maxLines = 3,
				overflow = TextOverflow.Ellipsis
			)
			Spacer(Modifier.height(8.dp))
			Text(
				text = note.content,
				style = MaterialTheme.typography.bodyMedium.copy(
					color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
				),
				maxLines = 9,
				overflow = TextOverflow.Ellipsis
			)
			note.remindAt?.let {
				ElevatedFilterChip(
					selected = true,
					onClick = { },
					enabled = false,
					label = {
						Text(
							text = DateTimeUtil.millisToTextFormat(note.remindAt),
							style = MaterialTheme.typography.bodySmall.copy(
								textDecoration =if(note.isReminded) TextDecoration.LineThrough else TextDecoration.None
							),
							maxLines = 1,
							overflow = TextOverflow.Ellipsis
						)
					},
					leadingIcon = {
						Icon(
							painter = painterResource(R.drawable.ic_alarm),
							contentDescription = null,
							modifier = Modifier.size(16.dp)
						)
					}
				)
			}
		}
		if(note.isPinned){
			Icon(
				painter = painterResource(R.drawable.ic_push_pin_filled),
				contentDescription = "",
				modifier = Modifier
					.padding(top = 8.dp, end = 8.dp)
					.align(Alignment.TopEnd)
					.zIndex(2f)
					.rotate(45f),
				tint = MaterialTheme.colorScheme.primary
			)
		}
	}
}

@Preview(showSystemUi = true)
@Composable
private fun NoteItemPreview(){
	NoteItem(
		note = NoteUi(
			id = 1,
			title = "Hello world",
			content = "The is hello world The is hello world The is hello worldThe is hello world The is hello world The is hello world The is hello world",
		),
		onClick = {},
		onLongClick = {},
		isSelected = true
	)
}
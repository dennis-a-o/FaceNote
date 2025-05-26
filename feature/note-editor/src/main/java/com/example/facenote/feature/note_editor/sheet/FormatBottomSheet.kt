package com.example.facenote.feature.note_editor.sheet

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.facenote.core.ui.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormatBottomSheet(
	onDismiss: () ->Unit,
	onSelectStyle: (SpanStyle) -> Unit
) {
	ModalBottomSheet(
		onDismissRequest = onDismiss,
	) {
		Column (
			modifier = Modifier
				.fillMaxWidth()
				.padding(16.dp)
		){
			Text(
				text = stringResource(R.string.format),
				style = MaterialTheme.typography.titleLarge,
			)
			Spacer(Modifier.height(16.dp))
			Row(
				modifier = Modifier
					.horizontalScroll(rememberScrollState()),
				verticalAlignment = Alignment.CenterVertically
			) {
				TextTypography(onSelect = onSelectStyle)
				VerticalDivider(Modifier.height(24.dp))
				Spacer(Modifier.width(8.dp))
				TextStyle(onSelectStyle = onSelectStyle)
			}
			Spacer(Modifier.height(16.dp))
			TextColor(onSelect = onSelectStyle)
			Spacer(Modifier.height(16.dp))
			TextBackgroundColor(onSelect = onSelectStyle)
			Spacer(Modifier.height(16.dp))
		}
	}
}

@Composable
private fun TextTypography(
	onSelect: (SpanStyle) -> Unit
){
	var selected by remember { mutableIntStateOf(2) }
	val typography = arrayOf("H1","H2","Aa")
	Row {
		typography.forEachIndexed{index, name ->
			FilterChip(
				selected = selected == index,
				onClick = {
					selected = index
					when(name){
						"H1" -> onSelect(SpanStyle(fontSize = 24.sp))
						"H2" -> onSelect(SpanStyle(fontSize = 20.sp))
						else -> onSelect(SpanStyle(fontSize = 16.sp))
					}
				},
				label = { Text(text = name) },
			)
			Spacer(Modifier.width(8.dp))
		}
	}
}

@Composable
private fun TextStyle(
	onSelectStyle: (SpanStyle) -> Unit
){
	var selectedBold by remember { mutableStateOf(false) }
	var selectedItalic by remember { mutableStateOf(false) }
	var selectedUnderline by remember { mutableStateOf(false) }
	Row {
		FilterChip(
			selected = selectedBold,
			onClick = {
				selectedBold = !selectedBold
				onSelectStyle(SpanStyle(fontWeight = if(selectedBold){ FontWeight.Bold }else{ FontWeight.Normal }))
			},
			label = {
				Text(
					text = "B",
					fontWeight = FontWeight.Bold
				)
			},
		)
		Spacer(Modifier.width(8.dp))
		FilterChip(
			selected = selectedItalic,
			onClick = {
				selectedItalic = !selectedItalic
				onSelectStyle(SpanStyle(fontStyle = if(selectedItalic){ FontStyle.Italic }else{ FontStyle.Normal }))
			},
			label = {
				Text(
					text = "I",
					fontStyle = FontStyle.Italic
				)
			}
		)
		Spacer(Modifier.width(8.dp))
		FilterChip(
			selected = selectedUnderline,
			onClick = {
				selectedUnderline = !selectedUnderline
				onSelectStyle(SpanStyle(textDecoration = if(selectedUnderline){ TextDecoration.Underline }else{ TextDecoration.None }))
			},
			label = {
				Text(
					text = "U",
					textDecoration = TextDecoration.Underline
				)
			}
		)
	}
}

@Composable
private fun TextColor(
	onSelect: (SpanStyle) -> Unit
) {
	val colors = arrayOf(
		Color(0xFF000000),
		Color(0xFFA5A5A5),
		Color(0xFFFFFFFF),
		Color(0xFFFF0000),
		Color(0xFFFF9800),
		Color(0xFFFFEB3B),
		Color(0xFF4CAF50),
		Color(0xFF3F51B5),
	)
	var selected by remember { mutableIntStateOf(0) }
	val colorScheme =  MaterialTheme.colorScheme

	Row (
		modifier = Modifier
			.horizontalScroll(rememberScrollState()),
		verticalAlignment = Alignment.CenterVertically
	){
		IconButton(
			onClick ={},
			modifier = Modifier.size(40.dp),
		){
			Text(
				text = "A",
				fontSize = 24.sp,
				fontWeight = FontWeight.Bold,
				textDecoration = TextDecoration.Underline
			)
		}
		colors.forEachIndexed{index,color ->
			Spacer(Modifier.width(16.dp))
			IconButton(
				onClick = {
					if (selected == index){

						selected = -1
						onSelect(SpanStyle(color = colorScheme.onBackground))
					} else {
						selected = index
						onSelect(SpanStyle(color = color))
					}
				},
				modifier = Modifier
					.shadow(elevation = 1.dp, shape = CircleShape)
					.size(32.dp)
					.background(color = color),
			) {
				if (selected == index){
					Icon(
						painter = painterResource(R.drawable.ic_check),
						contentDescription = "",
						tint = Color.White.copy(alpha = 0.8f)
					)
				}
			}
		}
	}
}

@Composable
private fun TextBackgroundColor(
	onSelect: (SpanStyle) -> Unit
) {
	val colors = arrayOf(
		Color(0xFFA489A8),
		Color(0xFFA5A5A5),
		Color(0xFFF8C06C),
		Color(0xFFFFF27F),
		Color(0xFFFFFFFF),
		Color(0xFFFC8181),
		Color(0xFF5E6BB6),
		Color(0xFF6DAD70),
	)
	var selected by remember { mutableIntStateOf(0) }

	Row (
		modifier = Modifier
			.horizontalScroll(rememberScrollState()),
		verticalAlignment = Alignment.CenterVertically
	){
		IconButton(
			onClick ={},
			modifier = Modifier.size(40.dp),
		){
			Box (
				contentAlignment = Alignment.Center
			){
				Box(
					modifier = Modifier
						.height(12.dp)
						.width(24.dp)
						.background(color = colors.getOrElse(selected) { Color.Transparent })
				)
				Text(
					text = "A",
					modifier = Modifier,
					fontSize = 24.sp,
					fontWeight = FontWeight.Bold,
					lineHeight = 24.sp
				)
			}
		}
		colors.forEachIndexed{index,color ->
			Spacer(Modifier.width(16.dp))
			IconButton(
				onClick = {
					if (selected == index){
						selected = -1
						onSelect(SpanStyle(background = Color.Transparent))
					} else {
						selected = index
						onSelect(SpanStyle(background = color))
					}
				},
				modifier = Modifier
					.shadow(elevation = 1.dp, shape = CircleShape)
					.size(32.dp),
				colors = IconButtonDefaults .iconButtonColors( containerColor = color),
			) {
				if (selected == index){
					Icon(
						painter = painterResource(R.drawable.ic_check),
						contentDescription = "",
						tint = Color.White.copy(alpha = 0.8f)
					)
				}
			}
		}
	}
}
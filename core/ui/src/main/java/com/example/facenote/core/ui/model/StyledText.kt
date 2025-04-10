package com.example.facenote.core.ui.model

import androidx.compose.ui.graphics.Color

data class StyledText(
	val text:String,
	val styles: List<TextStyleItem>
)

data class TextStyleItem(
	val start: Int = 0,
	val end: Int = 0,
	val style: FaceNoteTextStyle = FaceNoteTextStyle()
)

data class FaceNoteTextStyle(
	val isBold: Boolean = false,
	val isItalic: Boolean = false,
	val isUnderlined: Boolean = false,
	val isLineThrough: Boolean = false,
	val isHeading1: Boolean = false,
	val isHeading2: Boolean = false,
	val textColor: Color = Color.Unspecified,
	val textBackground: Color = Color.Unspecified
)

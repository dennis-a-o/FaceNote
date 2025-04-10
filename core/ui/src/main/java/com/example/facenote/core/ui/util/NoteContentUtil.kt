package com.example.facenote.core.ui.util

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.sp
import com.example.facenote.core.ui.model.CheckListItem
import com.example.facenote.core.ui.model.FaceNoteTextStyle
import com.example.facenote.core.ui.model.StyledText
import com.example.facenote.core.ui.model.TextStyleItem
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


object NoteContentUtil {
	fun checkListToJson(checkList: List<CheckListItem>): String {
		return try {
			Gson().toJson(checkList)
		} catch (e: Exception) {
			e.printStackTrace()
			""
		}
	}

	fun annotatedStringToJson(annotatedString: AnnotatedString): String {
		return try {
			val styles = mutableListOf<TextStyleItem>()
			annotatedString.spanStyles.forEach {
				styles.add(
					TextStyleItem(
						start = it.start,
						end = it.end,
						style = FaceNoteTextStyle(
							isBold = when(it.item.fontWeight){
								FontWeight.Bold -> true
								else -> false
							},
							isItalic = when(it.item.fontStyle){
								FontStyle.Italic -> true
								else -> false
							},
							isUnderlined = when(it.item.textDecoration){
								TextDecoration.Underline -> true
								else -> false
							},
							isHeading1 = when(it.item.fontSize){
								24.sp -> true
								else -> false
							},
							isHeading2 = when(it.item.fontSize){
								20.sp -> true
								else -> false
							},
							textColor = it.item.color,
							textBackground = it.item.background
						)
					)
				)
			}
			val styledText = StyledText(text = annotatedString.text, styles = styles)

			Gson().toJson(styledText)
		} catch (e: Exception) {
			e.printStackTrace()
			""
		}
	}

	fun jsonToCheckList(json: String): List<CheckListItem> {
		return try {
			val type = object : TypeToken<List<CheckListItem>>() {}.type
			Gson().fromJson(json, type)
		} catch (e: Exception) {
			e.printStackTrace()
			emptyList()
		}
	}

	fun jsonToAnnotatedString(json: String): AnnotatedString {
		return try {
			val type = object : TypeToken<StyledText>() {}.type
			val styleText = Gson().fromJson<StyledText>(json,type)

			 buildAnnotatedString {
				append(styleText.text)
				styleText.styles.forEach {
					addStyle(
						start = it.start,
						end = it.end,
						style = SpanStyle(
							fontWeight = if(it.style.isBold) FontWeight.Bold else FontWeight.Normal,
							fontStyle = if (it.style.isItalic) FontStyle.Italic else FontStyle.Normal,
							fontSize = when{
								it.style.isHeading1 -> 24.sp
								it.style.isHeading2 -> 20.sp
								else -> 16.sp
							},
							textDecoration = when{
								it.style.isUnderlined -> TextDecoration.Underline
								it.style.isLineThrough -> TextDecoration.LineThrough
								else -> TextDecoration.None
							},
							color = it.style.textColor,
							background = it.style.textBackground
						)
					)
				}
			}
		} catch (e: Exception) {
			e.printStackTrace()
			AnnotatedString("")
		}
	}
}


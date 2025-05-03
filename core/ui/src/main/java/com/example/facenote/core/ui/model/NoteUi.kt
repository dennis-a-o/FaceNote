package com.example.facenote.core.ui.model

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.example.facenote.core.model.Note
import com.example.facenote.core.model.NoteState
import com.example.facenote.core.ui.util.NoteContentUtil

data class NoteUi(
	val id: Long,
	val title: String = "",
	val content: String,
	val color: Color = Color.Unspecified,
	val background: String ="",
	val createdAt: Long = 0,
	val updatedAt: Long =0,
	val remindAt: Long? = null,
	val isReminded: Boolean =false,
	val trashedAt: Long? = null,
	val isPinned: Boolean = false,
	val isLocked: Boolean = false,
	val isChecklist: Boolean = false,
	val state: String = NoteState.NORMAL.getName()
)

fun Note.toNoteUi() = NoteUi(
	id = id,
	title = title,
	content = if (isChecklist){
		var text = ""
		NoteContentUtil.jsonToCheckList(content).forEach { text += "[] ${it.content}\n" }
		text
	}else{
		NoteContentUtil.jsonToAnnotatedString(content).text
	},
	color = Color(color),
	background = background,
	createdAt = createdAt,
	updatedAt = updatedAt,
	remindAt = remindAt,
	isReminded = isReminded,
	trashedAt = trashedAt,
	isPinned = isPinned,
	isLocked = isLocked,
	isChecklist = isChecklist,
	state = state
)

fun NoteUi.toNote() = Note(
	id = id,
	content = content,
	title = title,
	color = color.toArgb(),
	background = background,
	createdAt = createdAt,
	updatedAt = updatedAt,
	remindAt = remindAt,
	isReminded = isReminded,
	trashedAt = trashedAt,
	isPinned = isPinned,
	isLocked = isLocked,
	isChecklist = isChecklist,
	state = state
)

package com.example.facenote.core.ui.model

data class SelectState(
	val selected: List<NoteUi> = emptyList(),
	val isSelecting: Boolean = false,
	val pin: Boolean = true
)

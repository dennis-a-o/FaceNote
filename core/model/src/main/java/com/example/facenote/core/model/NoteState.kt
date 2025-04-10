package com.example.facenote.core.model

enum class NoteState {
	NORMAL,
	ARCHIVE,
	TRASH;

	fun getName(): String = when(this){
			NORMAL -> "Normal"
			ARCHIVE -> "Archive"
			TRASH -> "Trash"
		}
}
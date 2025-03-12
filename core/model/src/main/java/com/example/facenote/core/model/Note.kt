package com.example.facenote.core.model

data class Note(
	val id: Long,
	val title: String,
	val content: String,
	val color: String,
	val background: String,
	val createdAt: Long,
	val updatedAt: Long,
	val trashedAt: Long?,
	val isPinned: Boolean,
	val isLocked: Boolean,
	val isChecklist: Boolean,
	val state: String
)

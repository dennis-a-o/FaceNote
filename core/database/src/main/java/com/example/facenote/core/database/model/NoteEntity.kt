package com.example.facenote.core.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
	tableName = "note"
)
data class NoteEntity(
	@PrimaryKey(autoGenerate = true)
	val id: Long,

	@ColumnInfo(name = "title")
	val title: String,

	@ColumnInfo(name = "content")
	val content: String,

	@ColumnInfo(name = "color", defaultValue = "0")
	val color: Long,

	@ColumnInfo("background", defaultValue = "")
	val background: String,

	@ColumnInfo(name = "createdAt",defaultValue = "CURRENT_TIMESTAMP")
	val createdAt: Long = System.currentTimeMillis(),

	@ColumnInfo(name = "updatedAt", defaultValue = "CURRENT_TIMESTAMP")
	val updatedAt: Long = System.currentTimeMillis(),

	@ColumnInfo(name = "deletedAt")
	val deletedAt: Long? = null,

	@ColumnInfo(name = "isPinned")
	val isPinned: Boolean = false,

	@ColumnInfo(name = "isArchived")
	val isArchived: Boolean = false,

	@ColumnInfo(name = "isDeleted")
	val isDeleted: Boolean = false,
)

package com.example.facenote.core.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.facenote.core.model.Note
/*
* Defines FaceNote note resource
* */
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

	@ColumnInfo(name = "color")
	val color: Int = 0,

	@ColumnInfo("background")
	val background: String = "",

	@ColumnInfo(name = "createdAt",defaultValue = "CURRENT_TIMESTAMP")
	val createdAt: Long = System.currentTimeMillis(),

	@ColumnInfo(name = "updatedAt", defaultValue = "CURRENT_TIMESTAMP")
	val updatedAt: Long = System.currentTimeMillis(),

	@ColumnInfo(name = "remindAt")
	val remindAt: Long? = null,

	@ColumnInfo(name = "isReminded")
	val isReminded: Boolean = false,

	@ColumnInfo(name = "trashedAt")
	val trashedAt: Long? = null,

	@ColumnInfo(name = "isPinned")
	val isPinned: Boolean = false,

	@ColumnInfo(name = "isLocked")
	val isLocked: Boolean = false,

	@ColumnInfo(name = "isChecklist")
	val isChecklist: Boolean = false,

	@ColumnInfo(name = "state")
	val state: String = "Normal"
)

fun NoteEntity.asExternalModel() = Note(
	id = id,
	title = title,
	content = content,
	createdAt = createdAt,
	color = color,
	background = background,
	updatedAt = updatedAt,
	remindAt = remindAt,
	isReminded = isReminded,
	trashedAt = trashedAt,
	isPinned = isPinned,
	isLocked = isLocked,
	isChecklist = isChecklist,
	state = state
)

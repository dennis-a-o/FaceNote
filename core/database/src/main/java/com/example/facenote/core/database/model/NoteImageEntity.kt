package com.example.facenote.core.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.example.facenote.core.model.NoteImage

@Entity(
	tableName = "noteImage",
	foreignKeys = [
		ForeignKey(
			entity = NoteEntity::class,
			parentColumns = ["id"],
			childColumns = ["noteId"],
			onDelete = ForeignKey.CASCADE
		)
	]
)
data class NoteImageEntity(
	@PrimaryKey(autoGenerate = true)
	val id: Long,

	@ColumnInfo(name = "noteId")
	val noteId: Long,

	@ColumnInfo( name = "filePath")
	val filePath: String,
)

fun NoteImageEntity.asExtenalModel() = NoteImage(
	id = id,
	noteId = noteId,
	filePath = filePath
)

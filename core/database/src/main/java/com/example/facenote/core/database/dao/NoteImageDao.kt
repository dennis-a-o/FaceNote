package com.example.facenote.core.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.facenote.core.database.model.NoteImageEntity
import kotlinx.coroutines.flow.Flow
@Dao
interface NoteImageDao {
	@Insert(onConflict = OnConflictStrategy.REPLACE)
	suspend fun createNoteImage(noteImage: NoteImageEntity)

	@Insert(onConflict = OnConflictStrategy.REPLACE)
	suspend fun createNoteImages(noteImages: List<NoteImageEntity>)

	@Query("SELECT * FROM noteImage WHERE id = :id")
	fun getNoteImage(id: Long): Flow<NoteImageEntity>

	@Query("SELECT * FROM noteImage WHERE noteId = :noteId")
	fun getNoteImages(noteId: Long): Flow<List<NoteImageEntity>>

	@Delete
	suspend fun deleteNoteImage(noteImage: NoteImageEntity)

	@Delete
	suspend fun deleteNoteImages(noteImage: List<NoteImageEntity>)

	@Query("DELETE FROM noteImage WHERE noteId = :noteId")
	suspend fun deleteNoteImageByNoteId(noteId: Long)
}
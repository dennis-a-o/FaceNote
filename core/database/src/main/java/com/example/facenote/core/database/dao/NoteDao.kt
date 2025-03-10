package com.example.facenote.core.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.facenote.core.database.model.NoteEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {
	@Insert(onConflict = OnConflictStrategy.REPLACE)
	fun createNote(note: NoteEntity)

	@Insert(onConflict = OnConflictStrategy.REPLACE)
	fun createNotes(notes: List<NoteEntity>)

	@Update
	fun updateNote(note: NoteEntity)

	@Query("SELECT * FROM note WHERE id = :id")
	fun getNote(id: Long): Flow<NoteEntity>

	@Query(
		"""
		SELECT * FROM note WHERE isArchived = 0 AND isDeleted = 0 LIMIT :limit OFFSET :offset
		"""
	)
	fun getNotes(limit: Int, offset: Int): Flow<List<NoteEntity>>

	@Query("""
		SELECT * FROM note WHERE title LIKE '%' || :query || '%' 
		OR content LIKE '%' || :query || '%' 
		AND  isArchived = 0 AND isDeleted = 0 LIMIT :limit OFFSET :offset
	""")
	fun searchNotes(query: String,limit: Int, offset: Int): Flow<List<NoteEntity>>

	@Delete
	fun deleteNote(note: NoteEntity)

	@Delete
	fun deleteNotes(notes: List<NoteEntity>)
}
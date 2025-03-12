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
	suspend fun createNote(vararg note: NoteEntity): LongArray

	@Update
	suspend fun updateNote(note: NoteEntity)

	@Query("SELECT * FROM note WHERE id = :id")
	fun getNote(id: Long): Flow<NoteEntity>

	@Query("SELECT * FROM note WHERE state = :state  LIMIT :limit OFFSET :offset")
	fun getNotes(state: String, limit: Int, offset: Int): Flow<List<NoteEntity>>

	@Query("""
		SELECT * FROM note WHERE title LIKE '%' || :query || '%' 
		OR content LIKE '%' || :query || '%' 
		AND  state = :state  LIMIT :limit OFFSET :offset
	""")
	fun searchNotes(query: String,state: String,limit: Int, offset: Int): Flow<List<NoteEntity>>

	@Query("UPDATE note SET isPinned = :isPinned  WHERE id IN (:id)")
	suspend fun pinNotes(id: List<Long>, isPinned: Boolean)

	@Query("UPDATE note SET state = :state WHERE id IN (:id)")
	suspend fun updateNoteState(id: List<Long>, state: String)

	@Delete
	suspend fun deleteNote(note: NoteEntity)

	@Delete
	suspend fun deleteNotes(notes: List<NoteEntity>)
}
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
	fun getNote(id: Long): Flow<NoteEntity?>

	@Query("""
		SELECT * FROM note WHERE state = :state  
		ORDER BY isPinned DESC, createdAt DESC 
		LIMIT :limit OFFSET :offset""")
	 fun getNotes(state: String, limit: Int, offset: Int): Flow<List<NoteEntity>>

	@Query("""
		SELECT * FROM note WHERE (title LIKE '%' || :query || '%' 
		OR content LIKE '%' || :query || '%' )
		AND state = :state  LIMIT :limit OFFSET :offset
	""")
	fun searchNotes(query: String, state: String,limit: Int, offset: Int): Flow<List<NoteEntity>>

	@Query("UPDATE note SET isPinned = :isPinned  WHERE id IN (:id)")
	suspend fun pinNotes(id: List<Long>, isPinned: Boolean)

	@Query("UPDATE note SET state = :state WHERE id IN (:id)")
	suspend fun updateNoteState(id: List<Long>, state: String)

	@Query("SELECT id FROM note WHERE state = 'Trash'")
	fun getTrashNoteIdList(): Flow<List<Long>>

	@Query("UPDATE note SET remindAt = :remindAt WHERE id = :noteId")
	suspend fun setNoteReminder(noteId: Long, remindAt: Long)

	@Query("UPDATE note SET remindAt = null, isReminded = 0 WHERE id = :noteId")
	suspend fun clearNoteReminder(noteId: Long)

	@Query("UPDATE note SET isReminded = 1 WHERE id = :noteId")
	suspend fun setNoteReminderDone(noteId: Long)

	@Query("DELETE FROM note WHERE state = 'Trash'")
	suspend fun deleteTrashNote()

	@Delete
	suspend fun deleteNote(note: NoteEntity)

	@Delete
	suspend fun deleteNotes(notes: List<NoteEntity>)
}
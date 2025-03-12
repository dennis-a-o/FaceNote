package com.example.facenote.core.data.repository

import com.example.facenote.core.database.dao.NoteDao
import com.example.facenote.core.database.dao.NoteImageDao
import com.example.facenote.core.database.model.NoteEntity
import com.example.facenote.core.database.model.NoteImageEntity
import com.example.facenote.core.model.Note
import com.example.facenote.core.model.NoteImage
import com.example.facenote.core.storage.ImageStorage
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class NoteRepositoryImpl @Inject constructor(
	private val noteDao: NoteDao,
	private val noteImageDao: NoteImageDao,
	private val imageStorage: ImageStorage
):NoteRepository {
	override suspend fun addNote(note: Note): LongArray {
		return noteDao.createNote(note.toEntity())
	}

	override suspend fun updateNote(note: Note) {
		noteDao.updateNote(note.toEntity())
	}

	override suspend fun updateNoteState(noteIds: List<Long>, state: String) {
		noteDao.updateNoteState(noteIds, state)
	}

	override suspend fun addNoteImages(noteImage: List<NoteImage>) {
		TODO("Not yet implemented")
	}

	override suspend fun pinNotes(noteIds: List<Long>, isPinned: Boolean) {
		TODO("Not yet implemented")
	}

	override fun getNotes() {
		TODO("Not yet implemented")
	}

	override fun getArchivedNotes() {
		TODO("Not yet implemented")
	}

	override fun getTrashNotes() {
		TODO("Not yet implemented")
	}

	override fun getNoteDetail(id: Long): Flow<Note> {
		TODO("Not yet implemented")
	}

	override fun getNoteImages(noteId: Long): Flow<List<NoteImage>> {
		TODO("Not yet implemented")
	}

	override fun getNoteImage(id: Long): Flow<NoteImage> {
		TODO("Not yet implemented")
	}

	override fun searchNotes(query: String) {
		TODO("Not yet implemented")
	}

	override suspend fun deleteNote(note: Note) {
		TODO("Not yet implemented")
	}

	override suspend fun deleteNotes(notes: List<Note>) {
		TODO("Not yet implemented")
	}

	override suspend fun deleteNoteImage(noteImage: NoteImage) {
		TODO("Not yet implemented")
	}

	private fun Note.toEntity() = NoteEntity(
		id = id,
		title = title,
		content = content,
		color = color,
		background = background,
		createdAt = createdAt,
		updatedAt  = updatedAt,
		trashedAt = trashedAt,
		isPinned = isPinned,
		isLocked = isLocked,
		isChecklist = isChecklist,
		state = state
	)

	private fun NoteImage.toEntity() = NoteImageEntity(
		id = id,
		noteId = noteId,
		filePath = filePath
	)
}
package com.example.facenote.core.data.repository

import com.example.facenote.core.model.Note
import com.example.facenote.core.model.NoteImage
import kotlinx.coroutines.flow.Flow

interface NoteRepository {
	suspend fun addNote(note:Note): LongArray
	suspend fun updateNote(note:Note)
	suspend fun updateNoteState(noteIds:List<Long>, state: String)
	suspend fun addNoteImages(noteImage: List<NoteImage>)
	suspend fun pinNotes(noteIds: List<Long>, isPinned:Boolean)
	fun getNotes()
	fun getArchivedNotes()
	fun getTrashNotes()
	fun getNoteDetail(id: Long): Flow<Note>
	fun getNoteImages(noteId: Long): Flow<List<NoteImage>>
	fun getNoteImage(id: Long): Flow<NoteImage>
	fun searchNotes(query: String)
	suspend fun deleteNote(note: Note)
	suspend fun deleteNotes(notes: List<Note>)
	suspend fun deleteNoteImage(noteImage: NoteImage)
}
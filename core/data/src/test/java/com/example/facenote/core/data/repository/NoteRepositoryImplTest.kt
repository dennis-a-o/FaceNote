package com.example.facenote.core.data.repository

import com.example.facenote.core.database.dao.NoteDao
import com.example.facenote.core.database.dao.NoteImageDao
import com.example.facenote.core.database.model.NoteEntity
import com.example.facenote.core.database.model.asExternalModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`

class NoteRepositoryImplTest {

	private val noteMock: NoteDao = mock(NoteDao::class.java)
	private val noteImageMock: NoteImageDao = mock(NoteImageDao::class.java)

	@Test
	fun noteRepository_get_note_by_id(){
		runTest {
			val note = NoteEntity(
					id = 1,
					title = "hello 1",
					content = "The world 1"
				)

			`when`(noteMock.getNote(1)).thenReturn(flowOf(note))

			val repository = NoteRepositoryImpl(noteDao = noteMock, noteImageDao = noteImageMock)

			val  repositoryNote = repository.getNoteDetail(1).first()

			assertEquals(repositoryNote, note.asExternalModel())

		}
	}
	@Test fun noteRepository_when_you_add_new_note_id_should_be_returned(){
		runTest {
			val note = NoteEntity(
				id = 1,
				title = "",
				content = ""
			)

			`when`(noteMock.createNote(note)).thenReturn(longArrayOf(1))

			val repository = NoteRepositoryImpl(noteDao = noteMock, noteImageDao = noteImageMock)

			val id = repository.addNote(note.asExternalModel())

			assertArrayEquals(longArrayOf(1), id)
		}
	}
}
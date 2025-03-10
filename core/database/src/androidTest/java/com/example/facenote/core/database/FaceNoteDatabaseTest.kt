package com.example.facenote.core.database

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.facenote.core.database.dao.NoteDao
import com.example.facenote.core.database.dao.NoteImageDao
import com.example.facenote.core.database.model.NoteEntity
import com.example.facenote.core.database.model.NoteImageEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class FaceNoteDatabaseTest {
	private lateinit var noteDao: NoteDao
	private lateinit var noteImageDao: NoteImageDao
	private lateinit var db: FaceNoteDatabase

	@Before
	fun setUp() {
		val context = ApplicationProvider.getApplicationContext<Context>()
		db = Room.inMemoryDatabaseBuilder(
			context,
			FaceNoteDatabase::class.java
		).build()
		noteDao = db.noteDao()
		noteImageDao = db.noteImageDao()
	}

	@After
	fun tearDown() {
		db.close()
	}

	@Test
	fun noteDao_fetch_items_by_descending_order() {
		runBlocking{
			val noteEnties = listOf(
				testNote(0, "hello1", "hello1"),
				testNote(0, "hello2", "hello2"),
				testNote(0, "hello3", "hello3"),
				testNote(0, "hello4", "hello4")
			)

			noteDao.createNotes(noteEnties)

			val savedNotesEntity = noteDao.getNotes(20, 0).first()

			assert(savedNotesEntity.first().createdAt == savedNotesEntity.last().createdAt)
		}
	}

	@Test
	fun noteDao_the_size_of_insert_should_equals_size_of_fetched_items() {
		runBlocking{
			val noteEnties = listOf(
				testNote(0, "hello1", "hello1"),
				testNote(0, "hello2", "hello2"),
				testNote(0, "hello3", "hello3"),
				testNote(0, "hello4", "hello4")
			)

			noteDao.createNotes(noteEnties)

			val savedNotesEntity = noteDao.getNotes(20, 0).first()

			assertEquals(noteEnties.size, savedNotesEntity.size)
		}
	}

	@Test
	fun noteImageDao_fetch_image_by_note_id(){
		runBlocking {
			val noteEnties = listOf(
				testNote(0, "hello1", "hello1"),
				testNote(0, "hello2", "hello2"),
				testNote(0, "hello3", "hello3"),
			)

			noteDao.createNotes(noteEnties)

			val noteImageEntities  = listOf(
				testNoteImage(0,2,"file1.jpg"),
				testNoteImage(0,1,"file2.jpg"),
				testNoteImage(0,3,"file3.jpg"),
				testNoteImage(0,2,"file4.jpg"),
			)

			noteImageDao.createNoteImages(noteImageEntities)

			val savedImageEntity = noteImageDao.getNoteImages(2).first()

			assertTrue(2 in savedImageEntity.map { it.noteId })
		}
	}
}

fun testNote(id:Long, title: String, content : String) = NoteEntity(
	id = id,
	title = title,
	content = content,
	color = 0,
	background = "",
	createdAt = System.currentTimeMillis(),
	updatedAt = 0,
)

fun testNoteImage(id:Long, noteId: Long, filePath : String) = NoteImageEntity(
	id = id,
	noteId = noteId,
	filePath = filePath
)
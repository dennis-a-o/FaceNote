package com.example.facenote.feature.note_gallery

import androidx.lifecycle.SavedStateHandle
import com.example.facenote.core.data.repository.NoteRepository
import com.example.facenote.core.domain.DeleteNoteImageUseCase
import com.example.facenote.core.model.NoteImage
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Before
import org.junit.Test

class NoteGalleryViewModelTest {
	@OptIn(ExperimentalCoroutinesApi::class)
	private val testDispatcher = StandardTestDispatcher()
	private val testScope = TestScope(testDispatcher)

	private lateinit var noteRepository: NoteRepository
	private lateinit var deleteNoteImageUseCase: DeleteNoteImageUseCase

	@Before
	fun setUp() {
		Dispatchers.setMain(testDispatcher)

		noteRepository = mockk()
		deleteNoteImageUseCase = mockk()
	}

	@After
	fun tearDown() {
		Dispatchers.resetMain()
	}

	@Test
	fun noteGalleryState_whenInitialized_showFetchedNoteImage() = testScope.runTest {
		val expectedNoteImages = listOf(
			NoteImage(1,1,"path1"),
			NoteImage(2,2,"path2")
		)

		every {
			noteRepository.getNoteImages(0)
		} returns flow { emit(expectedNoteImages) }

		val viewModel = NoteGalleryViewModel(
			noteRepository,
			mockk(),
			SavedStateHandle(mapOf())
		)

		delay(200)

		assertEquals(viewModel.noteGalleryState.value.size, expectedNoteImages.size)
	}

	@Test
	fun noteGallerySState_whenNoteImageDeleted_thenShowUpdatedImageList() = testScope.runTest  {
		val noteImages = listOf(
			NoteImage(1,1,"path1"),
			NoteImage(2,2,"path2")
		)

		every {
			noteRepository.getNoteImages(0)
		} returns flow { emit(noteImages) }


		coEvery {
			deleteNoteImageUseCase(noteImages[0])
		} returns Unit

		val viewModel = NoteGalleryViewModel(
			noteRepository,
			deleteNoteImageUseCase,
			SavedStateHandle(mapOf())
		)

		delay(100)

		viewModel.onDelete(noteImages[0])

		delay(100)

		assertNotEquals(viewModel.noteGalleryState.value.size, noteImages.size)
	}

}
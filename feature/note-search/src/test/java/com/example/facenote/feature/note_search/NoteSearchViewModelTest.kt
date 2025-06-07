package com.example.facenote.feature.note_search

import androidx.lifecycle.SavedStateHandle
import com.example.facenote.core.ui.model.NoteUi
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class NoteSearchViewModelTest {
	@OptIn(ExperimentalCoroutinesApi::class)
	private val testDispatcher = StandardTestDispatcher()
	private val testScope = TestScope(testDispatcher)

	@Before
	fun setUp() {
		Dispatchers.setMain(testDispatcher)

	}

	@After
	fun tearDown() {
		Dispatchers.resetMain()
	}

	@Test
	fun noteSelectionUpdateWhenNoteIsSelected() = testScope.runTest {
		val viewModel = NoteSearchViewModel(
			mockk(),
			mockk(),
			mockk(),
			savedStateHandle = SavedStateHandle(mapOf("noteState" to "Normal"))
		)
		val selectedNote = NoteUi(id= 1L, content = "one")
		viewModel.onSelect(selectedNote)

		assertEquals(viewModel.selectState.value.selected.first(), selectedNote)
	}

	@Test
	fun noteSelectionUpdatesAfterClearingSelection() = testScope.runTest {
		val viewModel = NoteSearchViewModel(
			mockk(),
			mockk(),
			mockk(),
			savedStateHandle = SavedStateHandle(mapOf("noteState" to "Normal"))
		)

		val selectedNote = NoteUi(id= 1L, content = "one")
		viewModel.onSelect(selectedNote)
		viewModel.onSelectClear()

		assertEquals(viewModel.selectState.value.selected.size, 0)
	}
}
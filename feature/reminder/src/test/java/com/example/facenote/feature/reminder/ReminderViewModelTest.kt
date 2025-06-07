package com.example.facenote.feature.reminder

import androidx.lifecycle.SavedStateHandle
import com.example.facenote.core.data.repository.NoteRepository
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.threeten.bp.LocalDate

class ReminderViewModelTest {
	@OptIn(ExperimentalCoroutinesApi::class)
	private val testDispatcher = StandardTestDispatcher()
	private val testScope = TestScope(testDispatcher)

	lateinit var noteRepositoryMock: NoteRepository

	@OptIn(ExperimentalCoroutinesApi::class)
	@Before
	fun setUp() {
		Dispatchers.setMain(testDispatcher)

		noteRepositoryMock = mockk()

		every {
			noteRepositoryMock.getNoteDetail(0)
		} returns flow { null }
	}

	@OptIn(ExperimentalCoroutinesApi::class)
	@After
	fun tearDown() {
		Dispatchers.resetMain()
	}

	@Test
	fun whenDateChangeThenReminderStateUpdates() = testScope.runTest{
		val viewModel = ReminderViewModel(
			noteRepositoryMock,
			mockk(),
			SavedStateHandle(mapOf())
		)

		val expectedDate = LocalDate.of(2025,7,1)

		viewModel.onEvent(ReminderFormEvent.DateChanged(expectedDate))

		assertEquals(viewModel.reminderState.value.selectedDate, expectedDate)
	}


}
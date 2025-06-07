package com.example.facenote.feature.note_editor

import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.SavedStateHandle
import com.example.facenote.core.ui.model.CheckListItem
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

class NoteEditorVIewModelTest {
	@OptIn(ExperimentalCoroutinesApi::class)
	private val testDispatcher = StandardTestDispatcher()
	private val testScope = TestScope(testDispatcher)

	@OptIn(ExperimentalCoroutinesApi::class)
	@Before
	fun setUp() {
		Dispatchers.setMain(testDispatcher)
	}

	@OptIn(ExperimentalCoroutinesApi::class)
	@After
	fun tearDown() {
		Dispatchers.resetMain()
	}

	@Test
	fun noteEditorStateInitiallyInDefault() = testScope.runTest {

		val viewModel  = NoteEditorVIewModel(
			mockk(),
			mockk(),
			mockk(),
			mockk(),
			mockk(),
			mockk(),
			mockk(),
			mockk(),
			SavedStateHandle(mapOf())
		)

		val editorState = viewModel.noteState.value
		assertEquals(editorState.id, 0)
		assertEquals(editorState.title, "")
		assertEquals(editorState.textFieldValue.text, "")
		assertEquals(editorState.checkListContent, emptyList<CheckListItem>())
	}

	@Test
	fun titleChangeUpdateSelectingTitle() = testScope.runTest{
		val viewModel  = NoteEditorVIewModel(
			mockk(),
			mockk(),
			mockk(),
			mockk(),
			mockk(),
			mockk(),
			mockk(),
			mockk(),
			SavedStateHandle(mapOf())
		)

		val testTitle = "Hello title"

		viewModel.onTitleChange(testTitle)
		delay(200)
		assertEquals(testTitle, viewModel.noteState.value.title)
	}

	@Test
	fun contentChangeUpdateSelectingContent()= testScope.runTest{
		val viewModel  = NoteEditorVIewModel(
			mockk(),
			mockk(),
			mockk(),
			mockk(),
			mockk(),
			mockk(),
			mockk(),
			mockk(),
			SavedStateHandle(mapOf())
		)

		val testContent = "Hello content"

		viewModel.onTextContentChange(TextFieldValue(testContent))

		delay(200)

		assertEquals(testContent, viewModel.noteState.value.textFieldValue.text)
	}
}
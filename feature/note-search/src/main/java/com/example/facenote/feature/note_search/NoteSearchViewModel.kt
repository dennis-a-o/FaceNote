package com.example.facenote.feature.note_search

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.map
import com.example.facenote.core.domain.PinNoteUseCase
import com.example.facenote.core.domain.SearchNoteUseCase
import com.example.facenote.core.domain.SetNoteStateUseCase
import com.example.facenote.core.model.NoteState
import com.example.facenote.core.ui.model.NoteUi
import com.example.facenote.core.ui.model.SelectState
import com.example.facenote.core.ui.model.toNoteUi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NoteSearchViewModel @Inject constructor(
	private val searchNoteUseCase: SearchNoteUseCase,
	private val pinNoteUseCase: PinNoteUseCase,
	private val setNoteStateUseCase: SetNoteStateUseCase,
	savedStateHandle: SavedStateHandle
): ViewModel() {

	private val _noteState: NoteState = when(savedStateHandle["noteSte"] ?: ""){
		"Trash" -> NoteState.TRASH
		"Archive" -> NoteState.ARCHIVE
		else -> NoteState.NORMAL
	}
	private val _query = MutableStateFlow("")
	private val _selectState = MutableStateFlow(SelectState())

	val noteSearchPager = Pager(PagingConfig(pageSize = 20, initialLoadSize = 20)){
		searchNoteUseCase(_query.value,_noteState)
	}.flow.map { it.map { note -> note.toNoteUi() } }

	val query = _query.asStateFlow()

	val selectState = _selectState.asStateFlow()

	fun onQueryChange(query: String){
		_query.value = query
	}

	fun onSelect(noteUi: NoteUi){
		if (noteUi in _selectState.value.selected){
			_selectState.update {
				it.copy(selected = it.selected - noteUi)
			}
			_selectState.update {
				it.copy(pin = it.selected.any{ note -> !note.isPinned })
			}
			if (_selectState.value.selected.isEmpty()){
				_selectState.update { it.copy(isSelecting = false) }
			}
		}else {
			if (_selectState.value.selected.isEmpty()){
				_selectState.update { it.copy(isSelecting = true) }
			}
			_selectState.update {
				it.copy(selected = it.selected + noteUi)
			}

			_selectState.update {
				it.copy(pin = if (_selectState.value.selected.isEmpty()) {
					!noteUi.isPinned
				}else{
					it.selected.any{ note -> !note.isPinned }
				})
			}
		}
	}

	fun  onSelectClear(){
		_selectState.update { SelectState() }
	}

	fun pin(){
		viewModelScope.launch {
			pinNoteUseCase(
				_selectState.value.selected.map{ it.id },
				_selectState.value.pin
			)
			onSelectClear()
		}
	}

	fun archive(){
		viewModelScope.launch {
			setNoteStateUseCase(
				_selectState.value.selected.map { it.id },
				NoteState.ARCHIVE
			)
			onSelectClear()
		}
	}

	fun trash(){
		viewModelScope.launch {
			setNoteStateUseCase(
				_selectState.value.selected.map { it.id },
				NoteState.TRASH
			)
			onSelectClear()
		}
	}
}
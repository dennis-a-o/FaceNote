package com.example.facenote.feature.trash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.map
import com.example.facenote.core.domain.DeleteNoteUseCase
import com.example.facenote.core.domain.EmptyTrashUseCase
import com.example.facenote.core.domain.GetTrashNotesUseCase
import com.example.facenote.core.domain.SetNoteStateUseCase
import com.example.facenote.core.model.NoteState
import com.example.facenote.core.ui.model.NoteUi
import com.example.facenote.core.ui.model.SelectState
import com.example.facenote.core.ui.model.toNote
import com.example.facenote.core.ui.model.toNoteUi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TrashViewModel @Inject constructor(
	private val getTrashNotesUseCase: GetTrashNotesUseCase,
	private val setNoteStateUseCase: SetNoteStateUseCase,
	private val deleteNoteUseCase: DeleteNoteUseCase,
	private val emptyTrashUseCase: EmptyTrashUseCase
): ViewModel() {
	private val _selectState = MutableStateFlow(SelectState())
	private val _isActionDone = MutableStateFlow(false)

	val trashNotePager = Pager(PagingConfig(pageSize = 20, initialLoadSize = 20)){
		getTrashNotesUseCase()
	}.flow.map { it.map { note -> note.toNoteUi() } }

	val selectState = _selectState.asStateFlow()
	val isActionDone = _isActionDone.asStateFlow()

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

	fun resetActionDone(){
		_isActionDone.value = false
	}

	fun  onSelectClear(){
		_selectState.update { SelectState() }
	}

	fun restore(){
		viewModelScope.launch {
			setNoteStateUseCase(
				_selectState.value.selected.map { it.id },
				NoteState.NORMAL
			)
			onSelectClear()
			_isActionDone.value = true
		}
	}

	fun emptyTrash(){
		viewModelScope.launch {
			emptyTrashUseCase()
			_isActionDone.value = true

		}
	}

	fun delete(){
		viewModelScope.launch {
			deleteNoteUseCase(
				_selectState.value.selected.map {  it.toNote() }
			)
			onSelectClear()
			_isActionDone.value = true
		}
	}
}
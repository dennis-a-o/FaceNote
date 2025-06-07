package com.example.facenote.feature.note_editor

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.input.TextFieldValue
import androidx.core.content.FileProvider
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.facenote.core.data.repository.NoteRepository
import com.example.facenote.core.domain.DeleteNoteUseCase
import com.example.facenote.core.domain.GetNoteByIdUseCase
import com.example.facenote.core.domain.PinNoteUseCase
import com.example.facenote.core.domain.SaveNoteImageUseCase
import com.example.facenote.core.domain.SaveNoteUseCase
import com.example.facenote.core.domain.SetNoteStateUseCase
import com.example.facenote.core.domain.UpdateNoteUseCase
import com.example.facenote.core.model.Note
import com.example.facenote.core.model.NoteImage
import com.example.facenote.core.model.NoteState
import com.example.facenote.core.ui.model.CheckListItem
import com.example.facenote.core.ui.util.NoteContentUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class NoteEditorVIewModel @Inject constructor(
	private val noteRepository: NoteRepository,
	private val getNoteByIdUseCase: GetNoteByIdUseCase,
	private val saveNoteUseCase: SaveNoteUseCase,
	private val updateNoteUseCase: UpdateNoteUseCase,
	private val saveNoteImageUseCase: SaveNoteImageUseCase,
	private val setNoteStateUseCase: SetNoteStateUseCase,
	private val deleteNoteUseCase: DeleteNoteUseCase,
	private val pinNoteUseCase: PinNoteUseCase,
	savedStateHandle: SavedStateHandle
):ViewModel() {
	private var noteId: Long = savedStateHandle["noteId"] ?: 0
	private var isCheckList: Boolean = savedStateHandle["isCheckList"] ?: false
	private val _noteState = MutableStateFlow(NoteEditorState(isChecklist = isCheckList))

	val noteState = _noteState.asStateFlow()

	init {
		if (noteId > 0) loadNote(noteId)
	}

	fun onTitleChange(title: String){
		_noteState.update {  it.copy(title = title) }
	}

	fun onCheckListChange(checkListContent: List<CheckListItem>){
		_noteState.update { it.copy(checkListContent = checkListContent) }
	}

	fun onTextContentChange(textFieldValue: TextFieldValue){
		_noteState.update { it.copy(textFieldValue = textFieldValue) }
	}

	fun onSetBackgroundColor(color: Color){
		_noteState.update { it.copy(color = color) }
	}

	fun onSetBackgroundImage(image: String){
		_noteState.update { it.copy(background = image) }
	}

	fun saveBitmapImage(bitmap: Bitmap){
		viewModelScope.launch {
			try {
				if (noteId < 0){
					_noteState.update {
						it.copy(imageBitmaps = it.imageBitmaps.plus(bitmap))
					}
				}else{
					saveNoteImageUseCase.invoke(bitmap,noteId).getOrThrow().let { image ->
						_noteState.update {
							it.copy(images = it.images + image)
						}
					}
				}
			}catch(e: Exception){
				_noteState.update {
					it.copy(
						isSaving = false,
						error = "Failed to save camera image"
					)
				}
			}
		}
	}

	fun saveUriImage(uri: Uri){
		viewModelScope.launch {
			try {
				if (noteId < 0){
					_noteState.update {
						it.copy(imageUris = it.imageUris.plus(uri))
					}
				}else{
					saveNoteImageUseCase.invoke(uri,noteId).getOrThrow().let {image ->
						_noteState.update {
							it.copy(images = it.images + image)
						}
					}
				}
			}catch(e: Exception){
				_noteState.update {
					it.copy(
						isSaving = false,
						error = "Failed to save image"
					)
				}
			}
		}
	}

	fun saveNote(){
		viewModelScope.launch {
			try {
				val note = _noteState.value.toNote(noteId)
				if (noteId > 0){
					updateNoteUseCase(note)
				}else{
					if (!isNoteEmpty()) {
						val result = saveNoteUseCase(
							note = note,
							uris = _noteState.value.imageUris,
							bitmaps = _noteState.value.imageBitmaps
						)

						if (result.isSuccess) {
							noteId = result.getOrDefault(-1)
							_noteState.update { NoteEditorState() }
							if (noteId > 0) {
								loadNote(noteId)
							}
						} else {
							throw Exception(result.exceptionOrNull())
						}
					}
				}
			}catch (e: Exception){
				_noteState.update {
					it.copy(
						isSaving = false,
						error = "Failed to save note"
					)
				}
			}
		}
	}

	fun onArchive(){
		viewModelScope.launch {
			if(noteId > 0) {
				if(_noteState.value.state != NoteState.ARCHIVE) {
					setNoteStateUseCase.invoke(noteId, NoteState.ARCHIVE)
					_noteState.update { it.copy(state = NoteState.ARCHIVE) }
				}else {
					setNoteStateUseCase.invoke(noteId, NoteState.NORMAL)
					_noteState.update { it.copy(state = NoteState.NORMAL) }
				}
			}
		}
	}

	fun onTrash(){
		viewModelScope.launch {
			if (noteId > 0) {
				setNoteStateUseCase.invoke(noteId, NoteState.TRASH)
				_noteState.update { it.copy(state = NoteState.TRASH) }
			}
		}
	}

	fun onDelete(){
		viewModelScope.launch {
			if (noteId > 0) {
				deleteNoteUseCase(_noteState.value.toNote(noteId))
			}
		}
	}

	fun onRestore(){
		viewModelScope.launch {
			if (noteId > 0){
				setNoteStateUseCase.invoke(noteId, NoteState.NORMAL)
				_noteState.update { it.copy(state = NoteState.NORMAL) }
			}
		}
	}

	fun onShare(context: Context){
		viewModelScope.launch {
			try {
				var noteText = ""
				val noteImages = arrayListOf<Uri>()

				if (_noteState.value.isChecklist) {
					//extract checklist text
					_noteState.value.checkListContent.forEach {
						noteText += "[] ${it.content} \n"
					}
				} else noteText = _noteState.value.textFieldValue.text

				if (_noteState.value.images.isNotEmpty()) {
					_noteState.value.images.forEach{
						val file = File(context.dataDir, it.filePath)
						val uri = FileProvider.getUriForFile(context,  "${context.packageName}.provider", file)
						noteImages.add(uri)
					}
				}

				val intent = Intent(Intent.ACTION_SEND_MULTIPLE).apply {
					type = if(noteImages.isEmpty()) "text/plain" else "image/*"
					//add text
					putExtra(Intent.EXTRA_TEXT, noteText)
					//add images
					putParcelableArrayListExtra(Intent.EXTRA_STREAM, noteImages)
				}
				context.startActivity(Intent.createChooser(intent, "Share note"))
			}catch (e: Exception){
				_noteState.update {
					it.copy(error = "Failed to share note")
				}
			}
		}
	}

	fun onPin(){
		viewModelScope.launch {
			if (noteId > 0) {
				pinNoteUseCase(noteId, !(_noteState.value.isPinned))
				_noteState.update { it.copy(isPinned = !(it.isPinned)) }
			}
		}
	}

	fun onReminderDone(){
		viewModelScope.launch {
			noteRepository.setNoteReminderDone(noteId)
			_noteState.update { it.copy(isReminded = true) }
		}
	}

	private fun loadNote(id: Long){
		_noteState.update { it.copy(isLoading = true) }
		viewModelScope.launch {
			try {
				val note = getNoteByIdUseCase(id).first()
				note?.let { loadedNote ->
					_noteState.update {
						it.copy(
							id = loadedNote.id,
							title = loadedNote.title,
							textFieldValue =if (!loadedNote.isChecklist)
								TextFieldValue(NoteContentUtil.jsonToAnnotatedString(loadedNote.content))
							else TextFieldValue(),
							checkListContent = if (loadedNote.isChecklist)
								NoteContentUtil.jsonToCheckList(loadedNote.content)
							else emptyList(),
							color = Color(loadedNote.color),
							background = loadedNote.background,
							createdAt = loadedNote.createdAt,
							updatedAt = loadedNote.updatedAt,
							remindAt = loadedNote.remindAt,
							isReminded = loadedNote.isReminded,
							trashedAt = loadedNote.trashedAt,
							isPinned = loadedNote.isPinned,
							isLocked = loadedNote.isLocked,
							isChecklist = loadedNote.isChecklist,
							state = when(loadedNote.state){
								"Trash" -> NoteState.TRASH
								"Archive" -> NoteState.ARCHIVE
								else -> NoteState.NORMAL
							},
							isLoading = false
						)
					}
				}
				val images = noteRepository.getNoteImages(id).first()
				_noteState.update { it.copy(images = images) }
			}catch (e:Exception){
				_noteState.update {
					it.copy(
						error = "Failed to load note",
						isLoading = false
					)
				}
			}
		}
	}

	private fun isNoteEmpty():Boolean{
		val isContentEmpty = if(_noteState.value.isChecklist) {
			_noteState.value.checkListContent.all { it.content.isEmpty() }
		}else {
			_noteState.value.textFieldValue.text.isEmpty()
		}

		return (_noteState.value.title.isEmpty() && isContentEmpty)
	}
}

data class NoteEditorState (
	val id: Long = 0,
	val title: String = "",
	val textFieldValue: TextFieldValue = TextFieldValue(),
	val checkListContent: List<CheckListItem> = emptyList(),
	val imageUris: List<Uri> = emptyList(),
	val imageBitmaps: List<Bitmap> = emptyList(),
	val images:List<NoteImage> = emptyList(),
	val color: Color = Color.Unspecified,
	val background: String = "",
	val createdAt: Long = 0,
	val updatedAt: Long = 0,
	val remindAt: Long? = null,
	val isReminded: Boolean = false,
	val trashedAt: Long? = null,
	val isPinned: Boolean = false,
	val isLocked: Boolean = false,
	val isChecklist: Boolean = true,
	val state: NoteState = NoteState.NORMAL,
	val isLoading: Boolean = false,
	val isSaving: Boolean = false,
	val error: String? = null,
	val success: String? = null
)

internal fun NoteEditorState.toNote(id: Long) = Note(
	id = id ,
	title = title,
	content = if (isChecklist){
		NoteContentUtil.checkListToJson(checkListContent)
	} else {
		NoteContentUtil.annotatedStringToJson(textFieldValue.annotatedString)
	},
	color = color.toArgb(),
	background = background,
	createdAt = if(id <= 0) System.currentTimeMillis() else createdAt,
	updatedAt = System.currentTimeMillis(),
	remindAt = remindAt,
	isReminded = isReminded,
	trashedAt = trashedAt,
	isPinned = isPinned,
	isLocked = isLocked,
	isChecklist = isChecklist,
	state = state.getName()
)
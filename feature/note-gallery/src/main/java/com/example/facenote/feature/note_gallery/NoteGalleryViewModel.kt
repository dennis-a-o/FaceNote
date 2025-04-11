package com.example.facenote.feature.note_gallery

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.facenote.core.data.repository.NoteRepository
import com.example.facenote.core.domain.DeleteNoteImageUseCase
import com.example.facenote.core.model.NoteImage
import com.example.facenote.core.model.NoteState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class NoteGalleryViewModel @Inject constructor(
	private val repository: NoteRepository,
	private  val deleteNoteImageUseCase: DeleteNoteImageUseCase,
	savedStateHandle: SavedStateHandle
): ViewModel() {
	private val noteId: Long = savedStateHandle["noteId"] ?: 0
	private val _imageIndex: Int =  savedStateHandle["selectedImageIndex"] ?: 0
	private val _noteState = when(savedStateHandle["noteState"]?:""){
		"Trash" -> NoteState.TRASH
		"Archive" -> NoteState.ARCHIVE
		else -> NoteState.NORMAL
	}

	private val  _noteGalleryState = MutableStateFlow(emptyList<NoteImage>())
	private val _selectedImageIndex = MutableStateFlow(_imageIndex)

	init {
		getImages()
	}

	val noteGalleryState = _noteGalleryState.asStateFlow()
	val noteState = _noteState
	var selectedImageIndex = _selectedImageIndex.asStateFlow()

	fun onIndexChange(index: Int){
		_selectedImageIndex.value = index
	}

	fun onDelete(noteImage: NoteImage){
		viewModelScope.launch {
			try {
				deleteNoteImageUseCase(noteImage)
				_noteGalleryState.value = _noteGalleryState.value.filter { it != noteImage }
				_selectedImageIndex.value = 0
			}catch (e: Exception){
				e.printStackTrace()
			}
		}
	}

	fun onSend(noteImage: NoteImage,context: Context){
		viewModelScope.launch {
			try {
				val file = File(context.dataDir, noteImage.filePath)
				val uri =
					FileProvider.getUriForFile(context, "${context.packageName}.provider", file)

				val intent = Intent(Intent.ACTION_SEND).apply {
					type = "image/*"
					putExtra(Intent.EXTRA_STREAM, uri)
				}
				context.startActivity(Intent.createChooser(intent, "Share image"))
			}catch (e:Exception){
				e.printStackTrace()
			}
		}
	}

	private fun getImages(){
		viewModelScope.launch {
			val images = repository.getNoteImages(noteId).first()
			_noteGalleryState.value = images
		}
	}
}




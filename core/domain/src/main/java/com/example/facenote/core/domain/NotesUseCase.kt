package com.example.facenote.core.domain

import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.paging.PagingSource
import com.example.facenote.core.data.repository.NoteRepository
import com.example.facenote.core.model.Note
import com.example.facenote.core.model.NoteImage
import com.example.facenote.core.model.NoteState
import com.example.facenote.core.storage.ImageStorage
import kotlinx.coroutines.flow.first
import java.util.UUID
import javax.inject.Inject


class GetNotesUseCase @Inject constructor(
	private val noteRepository: NoteRepository,
){
	operator fun invoke(): PagingSource<Int, Note> = noteRepository.getNotes(NoteState.NORMAL.getName())
}

class GetNoteByIdUseCase @Inject constructor(
	private val noteRepository: NoteRepository
){
	operator fun invoke(id: Long) = noteRepository.getNoteDetail(id)
}

class SearchNoteUseCase @Inject constructor(
	private val noteRepository: NoteRepository
){
	operator fun invoke(query: String,state: NoteState): PagingSource<Int, Note> {
		return noteRepository.searchNotes(query, state)
	}
}

class GetArchiveNotesUseCase @Inject constructor(
	private val noteRepository: NoteRepository,
){
	operator fun invoke(): PagingSource<Int, Note> = noteRepository.getNotes(NoteState.ARCHIVE.getName())
}

class GetTrashNotesUseCase @Inject constructor(
	private val noteRepository: NoteRepository,
){
	operator fun invoke(): PagingSource<Int, Note> = noteRepository.getNotes(NoteState.TRASH.getName())
}

class PinNoteUseCase @Inject constructor(
	private val noteRepository: NoteRepository,
){
	suspend operator fun invoke(id: Long, isPinned: Boolean){
		noteRepository.pinNotes(listOf(id),isPinned)
	}
	suspend operator fun invoke(ids: List<Long>, isPinned: Boolean){
		noteRepository.pinNotes(ids, isPinned)
	}
}


class SaveNoteUseCase @Inject constructor(
	private val noteRepository: NoteRepository,
	private val imageStorage: ImageStorage
){
	suspend operator fun invoke(
		note: Note,
		uris: List<Uri>,
		bitmaps: List<Bitmap> = emptyList()
	): Result<Long>{
		return try {
			val imagesPath = mutableListOf<String>()

			val uriImages = uris.map { uri ->
				val extension = uri.lastPathSegment?.substringAfterLast('.')
				val filename = "note_image_${System.currentTimeMillis()}_${UUID.randomUUID()}.$extension"
				imageStorage.saveImage(uri, filename).getOrDefault("")
			}

			val bitmapImages = bitmaps.map { bitmap ->
				val filename = "note_image_${System.currentTimeMillis()}_${UUID.randomUUID()}.png"
				imageStorage.saveBitmap(bitmap, filename).getOrDefault("")
			}

			imagesPath.apply {
				addAll(uriImages)
				addAll(bitmapImages)
			}

			val noteId = noteRepository.addNote(note).first()

			val noteImages = imagesPath.map { filename ->
				NoteImage(
					id = 0,
					noteId = noteId,
					filePath = filename
				)
			}

			noteRepository.addNoteImages(noteImages)

			Result.success(noteId)
		}catch (e: Exception){
			Result.failure(e)
		}
	}
}

class SetNoteStateUseCase @Inject constructor(
	private val noteRepository: NoteRepository
){
	suspend operator fun invoke(id:Long, state:NoteState){
		noteRepository.updateNoteState(listOf(id), state)
	}

	suspend operator fun invoke(noteIds:List<Long>, state: NoteState){
		noteRepository.updateNoteState(noteIds, state)
	}
}


class UpdateNoteUseCase @Inject constructor(
	private val noteRepository: NoteRepository
){
	suspend operator fun invoke(note: Note){
		noteRepository.updateNote(note)
	}
}

class SaveNoteImageUseCase @Inject constructor(
	private val noteRepository: NoteRepository,
	private val imageStorage: ImageStorage
){
	suspend operator fun invoke(uri: Uri, noteId: Long): Result<NoteImage>{
		return try {
			var filename = "note_image_${System.currentTimeMillis()}_${UUID.randomUUID()}.png"
			filename = imageStorage.saveImage(uri, filename).getOrThrow()

			val noteImage = NoteImage(
				id = 0,
				noteId = noteId,
				filePath = filename
			)

			val imageId = noteRepository.saveNoteImage(noteImage)

			Result.success(noteImage.copy(id = imageId))
		}catch (e: Exception){
			Result.failure(e)
		}
	}

	suspend fun invoke(bitmap: Bitmap, noteId: Long) :Result<NoteImage>{
		return try {
			var filename = "note_image_${System.currentTimeMillis()}_${UUID.randomUUID()}.png"
			filename = imageStorage.saveBitmap(bitmap, filename).getOrThrow()

			val noteImage = NoteImage(
				id = 0,
				noteId = noteId,
				filePath = filename
			)

			val imageId = noteRepository.saveNoteImage(noteImage)

			Result.success(noteImage.copy(id = imageId))
		}catch (e: Exception){
			Result.failure(e)
		}
	}
}

class EmptyTrashUseCase @Inject constructor(
	private val noteRepository: NoteRepository,
	private val imageStorage: ImageStorage
){
	suspend operator fun invoke(){
		val trashNoteIdList = noteRepository.getTrashNoteIdList().first()
		trashNoteIdList.forEach { noteId ->
			val noteImages = noteRepository.getNoteImages(noteId).first()

			noteImages.forEach { imageStorage.deleteImage(it.filePath) }

			noteRepository.deleteNoteImages(noteId)
		}
		noteRepository.deleteAllTrash()
	}
}

class DeleteNoteUseCase @Inject constructor(
	private val noteRepository: NoteRepository,
	private val imageStorage: ImageStorage
){
	suspend  operator fun invoke(note: Note){
		val noteImages = noteRepository.getNoteImages(note.id).first()
		noteImages.forEach { imageStorage.deleteImage(it.filePath) }

		noteRepository.deleteNoteImages(note.id)
		noteRepository.deleteNote(note)
	}

	suspend operator fun invoke(notes: List<Note>){
		notes.forEach { note ->
			val noteImages = noteRepository.getNoteImages(note.id).first()

			noteImages.forEach { imageStorage.deleteImage(it.filePath) }

			noteRepository.deleteNoteImages(note.id)
		}
		noteRepository.deleteNotes(notes)
	}
}

class DeleteNoteImageUseCase @Inject constructor(
	private val noteRepository: NoteRepository,
	private val imageStorage: ImageStorage
){
	suspend operator fun invoke(noteImage: NoteImage){
		imageStorage.deleteImage(noteImage.filePath)
		noteRepository.deleteNoteImage(noteImage)
	}
}



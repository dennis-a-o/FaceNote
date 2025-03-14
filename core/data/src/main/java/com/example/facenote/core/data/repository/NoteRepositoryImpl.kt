package com.example.facenote.core.data.repository

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.facenote.core.database.dao.NoteDao
import com.example.facenote.core.database.dao.NoteImageDao
import com.example.facenote.core.database.model.NoteEntity
import com.example.facenote.core.database.model.NoteImageEntity
import com.example.facenote.core.database.model.asExtenalModel
import com.example.facenote.core.database.model.asExternalModel
import com.example.facenote.core.model.Note
import com.example.facenote.core.model.NoteImage
import com.example.facenote.core.storage.ImageStorage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

const val ARCHIVE = "Archive"
const val TRASH = "Trash"

class NoteRepositoryImpl @Inject constructor(
	private val noteDao: NoteDao,
	private val noteImageDao: NoteImageDao,
	//private val imageStorage: ImageStorage
):NoteRepository {
	override suspend fun addNote(note: Note): LongArray {
		return noteDao.createNote(note.toEntity())
	}

	override suspend fun updateNote(note: Note) {
		noteDao.updateNote(note.toEntity())
	}

	override suspend fun updateNoteState(noteIds: List<Long>, state: String) {
		noteDao.updateNoteState(noteIds, state)
	}

	override suspend fun addNoteImages(noteImage: List<NoteImage>) {
		noteImageDao.createNoteImages(noteImage.map { it.toEntity() })
	}

	override suspend fun pinNotes(noteIds: List<Long>, isPinned: Boolean) {
		noteDao.pinNotes(noteIds, isPinned)
	}

	override fun getNotes(): PagingSource<Int, Note> {
		return object :PagingSource<Int, Note>(){
			override fun getRefreshKey(state: PagingState<Int, Note>): Int? {
				return state.anchorPosition?.let { anchorPosition ->
					state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
						?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
				}
			}

			override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Note> {
				return try {
					val page = params.key ?: 1
					val pageSize = params.loadSize

					val data = noteDao.getNotes(
						limit = pageSize,
						offset = ((page - 1) * pageSize)
					)

					LoadResult.Page(
						data = data.map { it.asExternalModel() },
						prevKey = if(page == 1) null else page - 1,
						nextKey = if (data.isEmpty()) null else page + 1
					)
				} catch (e:  Exception){
					LoadResult.Error(e)
				}
			}
		}
	}

	override fun getArchivedNotes(): PagingSource<Int, Note> {
		return object :PagingSource<Int, Note>(){
			override fun getRefreshKey(state: PagingState<Int, Note>): Int? {
				return state.anchorPosition?.let { anchorPosition ->
					state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
						?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
				}
			}

			override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Note> {
				return try {
					val page = params.key ?: 1
					val pageSize = params.loadSize

					val data = noteDao.getNotes(
						state = ARCHIVE,
						limit = pageSize,
						offset = ((page - 1) * pageSize)
					)

					LoadResult.Page(
						data = data.map { it.asExternalModel() },
						prevKey = if(page == 1) null else page - 1,
						nextKey = if (data.isEmpty()) null else page + 1
					)
				} catch (e:  Exception){
					LoadResult.Error(e)
				}
			}
		}
	}

	override fun getTrashNotes(): PagingSource<Int, Note> {
		return object :PagingSource<Int, Note>(){
			override fun getRefreshKey(state: PagingState<Int, Note>): Int? {
				return state.anchorPosition?.let { anchorPosition ->
					state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
						?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
				}
			}

			override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Note> {
				return try {
					val page = params.key ?: 1
					val pageSize = params.loadSize

					val data = noteDao.getNotes(
						state = TRASH,
						limit = pageSize,
						offset = ((page - 1) * pageSize)
					)

					LoadResult.Page(
						data = data.map { it.asExternalModel() },
						prevKey = if(page == 1) null else page - 1,
						nextKey = if (data.isEmpty()) null else page + 1
					)
				} catch (e:  Exception){
					LoadResult.Error(e)
				}
			}
		}
	}

	override fun getNoteDetail(id: Long): Flow<Note> {
		return noteDao.getNote(id).map { it.asExternalModel() }
	}

	override fun getNoteImages(noteId: Long): Flow<List<NoteImage>> {
		return noteImageDao.getNoteImages(noteId).map { it.map { it1 -> it1.asExtenalModel() } }
	}

	override fun getNoteImage(id: Long): Flow<NoteImage> {
		return noteImageDao.getNoteImage(id).map { it.asExtenalModel() }
	}

	override fun searchNotes(query: String): PagingSource<Int, Note> {
		return object :PagingSource<Int, Note>(){
			override fun getRefreshKey(state: PagingState<Int, Note>): Int? {
				return state.anchorPosition?.let { anchorPosition ->
					state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
						?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
				}
			}

			override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Note> {
				return try {
					val page = params.key ?: 1
					val pageSize = params.loadSize

					val data = noteDao.searchNotes(
						query = query,
						state = TRASH,
						limit = pageSize,
						offset = ((page - 1) * pageSize)
					)

					LoadResult.Page(
						data = data.map { it.asExternalModel() },
						prevKey = if(page == 1) null else page - 1,
						nextKey = if (data.isEmpty()) null else page + 1
					)
				} catch (e:  Exception){
					LoadResult.Error(e)
				}
			}
		}
	}

	override suspend fun deleteNote(note: Note) {
		noteDao.deleteNote(note.toEntity())
	}

	override suspend fun deleteNotes(notes: List<Note>) {
		noteDao.deleteNotes(notes.map { it.toEntity() })
	}

	override suspend fun deleteNoteImage(noteImage: NoteImage) {
		noteImageDao.deleteNoteImage(noteImage.toEntity())
	}

	override suspend fun deleteNoteImages(noteId: Long) {
		noteImageDao.deleteNoteImageByNoteId(noteId)
	}

	private fun Note.toEntity() = NoteEntity(
		id = id,
		title = title,
		content = content,
		color = color,
		background = background,
		createdAt = createdAt,
		updatedAt  = updatedAt,
		trashedAt = trashedAt,
		isPinned = isPinned,
		isLocked = isLocked,
		isChecklist = isChecklist,
		state = state
	)

	private fun NoteImage.toEntity() = NoteImageEntity(
		id = id,
		noteId = noteId,
		filePath = filePath
	)
}
package com.example.facenote.core.drive
import android.content.Context
import com.example.facenote.core.datastore.FaceNotePreferencesDataSource
import com.example.facenote.core.model.BackupResult
import com.example.facenote.core.model.BackupResult.Operation
import com.example.facenote.core.model.BackupResult.Progress
import com.example.facenote.core.model.DriveFile
import com.google.api.client.googleapis.json.GoogleJsonResponseException
import com.google.api.client.googleapis.media.MediaHttpDownloader
import com.google.api.client.googleapis.media.MediaHttpDownloader.DownloadState.MEDIA_COMPLETE
import com.google.api.client.googleapis.media.MediaHttpDownloader.DownloadState.MEDIA_IN_PROGRESS
import com.google.api.client.googleapis.media.MediaHttpDownloaderProgressListener
import com.google.api.client.googleapis.media.MediaHttpUploader
import com.google.api.client.googleapis.media.MediaHttpUploaderProgressListener
import com.google.api.client.http.FileContent
import com.google.api.services.drive.Drive
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.net.SocketTimeoutException
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class DriveServiceHelper @Inject constructor(
	@ApplicationContext private val context: Context,
	private val drive: Drive?,
	private val preferencesDataStore: FaceNotePreferencesDataSource
) {
	val APP_FOLDER = "FaceNote"
	val FOLDER_MIME = "application/vnd.google-apps.folder"
	private val RESTORE_DIR = "facenote_restore_temp"

	private suspend fun createFolder(): String? {
		val folderMetadata = com.google.api.services.drive.model.File().apply {
			name = APP_FOLDER
			mimeType = FOLDER_MIME
		}

		return try {
			val folder = drive!!.files().create(folderMetadata)
				.setFields("id")
				.execute()

			folder.id?.let {
				preferencesDataStore.setDriveFolderId(it)
			}

			folder.id
		} catch (e: Exception) {
			e.printStackTrace()
			null
		}
	}

	private fun searchFile(
		fileName: String? = null,
		mimeType: String? = null,
		folderId: String? = null
	): List<com.google.api.services.drive.model.File>? {
		return try {
			var pageToken: String? = null

			val request = drive!!.files().list().apply {
				fields = "nextPageToken, files(id, name)"
				pageToken = pageToken
			}

			var query: String? = null

			fileName?.let {
				query = "name=\"$fileName\""
			}

			mimeType?.let {
				query = if (query == null) {
					"mimeType='$it'"
				} else {
					"$query and mimeType = '$it'"
				}
			}

			folderId?.let {
				request.spaces = it
			}
			request.q = query

			request.execute().files
		}catch (e: Exception){
			null
		}
	}

	private suspend fun getFolderId(): String? {
		var folderId: String? = preferencesDataStore.getDriveFolderId().first()
		if (folderId == null) {
			folderId = searchFile(APP_FOLDER, mimeType = FOLDER_MIME)?.firstOrNull()?.id
			if (folderId == null) {
				folderId = createFolder()
			}
		}

		return folderId
	}

	 fun uploadFile(file: File, fileMimeType: String): Flow<BackupResult<Unit>>{
		return callbackFlow {
			try {
				val folderId = getFolderId()

				if (!file.exists()) throw FileNotFoundException("File not found: ${file.path}")

				val fileMetaData = com.google.api.services.drive.model.File().apply {
					name = file.name
					parents = listOf(folderId)
					mimeType = fileMimeType
				}

				val mediaContent = FileContent(fileMimeType, file)
				val request = drive!!.files().create(fileMetaData, mediaContent)

				request.mediaHttpUploader.setProgressListener(
					object : MediaHttpUploaderProgressListener {
						override fun progressChanged(uploader: MediaHttpUploader) {
							when (uploader.getUploadState()) {
								MediaHttpUploader.UploadState.INITIATION_STARTED -> {
									trySend(Operation("Upload initializing started"))
								}

								MediaHttpUploader.UploadState.INITIATION_COMPLETE -> {
									trySend(Operation("Upload initializing complete"))
									trySend(Operation("Uploading"))
								}

								MediaHttpUploader.UploadState.MEDIA_IN_PROGRESS -> {
									trySend(Progress(uploader.progress))
								}

								MediaHttpUploader.UploadState.MEDIA_COMPLETE -> {
									trySend(Progress(1.0))
									trySend(Operation("Upload Complete"))
								}
								MediaHttpUploader.UploadState.NOT_STARTED -> {}
							}
						}
					}
				).setChunkSize(MediaHttpUploader.MINIMUM_CHUNK_SIZE)

				request.execute()

				send(Progress(1.0))//Done
				send(BackupResult.Success(Unit))
				close()
			} catch (e: GoogleJsonResponseException) {
				send(BackupResult.Error(
					when (e.statusCode) {
						401 -> "Authentication failed. Please sign in again"
						403 -> "Permission denied. Check your Google drive permission"
						404 -> "Resource not found"
						else -> "Google Drive error"
					}
				))
				close(e)
			} catch (e: SocketTimeoutException) {
				send(BackupResult.Error("Network timeout. Check your internet connection."))
				close(e)
			} catch (e: IOException) {
				send(BackupResult.Error("Network error"))
				close(e)
			} catch (e: Exception) {
				send(BackupResult.Error("Failed to upload file"))
				close(e)
			}
			awaitClose()
		}.flowOn(Dispatchers.IO)
	}

	fun downloadFile(id: String): Flow<BackupResult<File>>{
		return callbackFlow {
			try {
				val resDir = File(context.cacheDir, RESTORE_DIR)
				resDir.mkdirs()

				val restoreFile = File(resDir,"backup_restore.zip")
				val fileOutputStream = FileOutputStream(restoreFile)

				val request = drive!!.files().get(id)
				request.mediaHttpDownloader.setProgressListener(
					object : MediaHttpDownloaderProgressListener {
						override fun progressChanged(downloader: MediaHttpDownloader) {
							when (downloader.getDownloadState()) {
								MEDIA_IN_PROGRESS -> {
									println("Download percentage: " + downloader.getProgress())
									trySend(Progress(downloader.progress))
								}

								MEDIA_COMPLETE -> trySend(Operation("Download complete"))
								MediaHttpDownloader.DownloadState.NOT_STARTED -> {}
							}
						}
					}
				).setChunkSize(MediaHttpDownloader.MAXIMUM_CHUNK_SIZE)
				request.executeMediaAndDownloadTo(fileOutputStream)

				send(Progress(1.0))
				send(BackupResult.Success(restoreFile))
				close()
			} catch (e: GoogleJsonResponseException) {
				send(BackupResult.Error(
					when (e.statusCode) {
						401 -> "Authentication failed. Please sign in again"
						403 -> "Permission denied. Check your Google drive permission"
						404 -> "Resource not found"
						else -> "Google Drive error"
					}
				))
				close(e)
			} catch (e: SocketTimeoutException) {
				send(BackupResult.Error("Network timeout. Check your internet connection."))
				close(e)
			} catch (e: IOException) {
				send(BackupResult.Error("Network error"))
				close(e)
			} catch (e: Exception) {
				send(BackupResult.Error("Failed to download file"))
				close(e)
			}
			awaitClose()
		}.flowOn(Dispatchers.IO)
	}

	fun getFiles(): Flow<Result<List<DriveFile>>>{
		return flow {
			try {
				var pageToken: String? = null
				val result = drive!!.files().list().apply {
					fields = "nextPageToken, files(id, name, mimeType, size, createdTime)"
					pageToken = this.pageToken
					spaces = spaces
				}.execute()
				emit(Result.success(
					result.files.filter { it.mimeType == "application/zip" }.map {
							DriveFile(
								id = it.id,
								name = it.name,
								size = it.getSize(),
								createdTime = it.createdTime.value
							)
					}
				))

			} catch (e: GoogleJsonResponseException) {
				emit(Result.failure(
					when (e.statusCode) {
						401 -> Exception("Authentication failed. Please sign in again")
						403 -> Exception("Permission denied. Check your Google drive permission")
						404 -> Exception("Resource not found")
						else -> Exception("Google Drive error")
					}
				))
			} catch (e: SocketTimeoutException) {
				emit(Result.failure(Exception("Network timeout. Check your internet connection")))
			} catch (e: IOException) {
				emit(Result.failure(Exception("Network error")))
			} catch (e: Exception) {
				emit(Result.failure(Exception("Failed to fetch file")))
			}
		}.flowOn(Dispatchers.IO)
	}

	suspend fun deleteFile(id: String): Result<String>{
		return try {
			withContext(Dispatchers.IO){
				drive!!.files().delete(id).execute()

				Result.success("File deleted success fully ")
			}
		}catch (e: Exception){
			Result.failure(Exception("Failed to delete file"))
		}
	}

}
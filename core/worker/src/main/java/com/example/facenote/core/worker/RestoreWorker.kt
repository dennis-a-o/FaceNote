package com.example.facenote.core.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.example.facenote.core.data.repository.BackupRepository
import com.example.facenote.core.model.BackupResult
import com.example.facenote.core.notifications.Notifier
import java.io.File

class RestoreWorker	(
	context: Context,
	params: WorkerParameters,
	private val notifier: Notifier,
	private val backupRepository: BackupRepository
): CoroutineWorker(context, params) {
	override suspend fun doWork(): Result {
		return try {
			val fileId = inputData.getString("fileId").toString()
			var backupFile: File? = null

			setProgress(workDataOf("operation" to "Preparing restore..."))
			notifier.postBackupNotifications(
				"Restore backup",
				"Preparing restore...",
				0.0,
				true
			)

			backupRepository.downloadBackup(fileId).collect {
				when(it){
					is BackupResult.Error -> throw Exception(it.message)
					is BackupResult.Operation -> setProgress(workDataOf("operation" to it.operation))
					is BackupResult.Progress -> {
						setProgress(workDataOf("progress" to it.progress))
						notifier.postBackupNotifications(
							"Restore backup",
							"Downloading...",
							it.progress,
							true
						)
					}
					is BackupResult.Success<File> -> {
						backupFile = it.result as File?
						setProgress(workDataOf("operation" to "Download complete"))
						notifier.postBackupNotifications(
							"Restore backup",
							"Download completed",
							1.0,
							true
						)
					}
					else -> {}
				}
			}

			setProgress(workDataOf("operation" to "Restoring"))
			notifier.postBackupNotifications(
				"Restore backup",
				"Restoring...",
				1.0,
				true
			)

			val unzipResult = backupFile?.let { backupRepository.unzipBackup(it) }
			unzipResult?.get("database.db")?.let { backupRepository.importDatabase(it) }
			unzipResult?.get("images.zip")?.let { backupRepository.importImages(it) }

			notifier.postBackupNotifications(
				"Restore backup",
				"Restore completed",
				1.0,
				true
			)

			Result.success()
		}catch (e: Exception){
			notifier.postBackupNotifications(
				"Restore backup",
				"An error occurred",
				1.0,
				true
			)
			Result.failure(workDataOf("error" to e.message))
		}
	}

}
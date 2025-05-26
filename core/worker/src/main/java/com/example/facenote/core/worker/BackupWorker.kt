package com.example.facenote.core.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.example.facenote.core.data.repository.BackupRepository
import com.example.facenote.core.model.BackupResult
import com.example.facenote.core.notifications.Notifier


class BackupWorker(
	context: Context,
	params: WorkerParameters,
	private val notifier: Notifier,
	private val backupRepository: BackupRepository
): CoroutineWorker(context, params) {
	override suspend fun doWork(): Result {
		return try {
			setProgress(workDataOf("operation" to "Preparing backup..."))
			notifier.postBackupNotifications(
				"Backup",
				"Preparing backup...",
				0.0,
				false
			)

			setProgress(workDataOf("operation" to "Exporting database"))
			notifier.postBackupNotifications(
				"Backup",
				"Exporting database...",
				0.0,
				false
			)
			val dbFile = backupRepository.exportDatabase()

			setProgress(workDataOf("operation" to "Exporting images"))
			notifier.postBackupNotifications(
				"Backup",
				"Exporting images...",
				0.0,
				false
			)
			val imagesFile = backupRepository.exportImages()

			setProgress(workDataOf("operation" to "Zipping files together"))
			notifier.postBackupNotifications(
				"Backup",
				"Zipping files together...",
				0.0,
				false
			)
			val backupFile = backupRepository.zipBackup(dbFile, imagesFile)

			backupRepository.uploadBackup(backupFile).collect {
				 when(it){
					 is BackupResult.Error -> throw Exception(it.message)
					 is BackupResult.Operation ->{
						 setProgress(workDataOf("operation" to it.operation))
						 notifier.postBackupNotifications(
							 "Backup",
							 it.operation,
							 0.0,
							 false
						 )
					 }
					 is BackupResult.Progress -> {
						 setProgress(workDataOf("progress" to it.progress))
						 notifier.postBackupNotifications(
							 "Backup",
							 "Uploading...",
							 it.progress,
							 false
						 )
					 }
					 else -> {}
				 }
			}
			notifier.postBackupNotifications(
				"Backup",
				"Backup completed",
				1.0,
				false
			)

			backupRepository.setLastBackup(System.currentTimeMillis())

			Result.success()
		}catch (e: Exception){
			notifier.postBackupNotifications(
				"Backup",
				"An error occurred",
				0.0,
				false
			)
			Result.failure(workDataOf("error" to e.message))
		}
	}
}
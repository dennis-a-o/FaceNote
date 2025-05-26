package com.example.facenote.core.worker

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.example.facenote.core.data.repository.BackupRepository
import com.example.facenote.core.notifications.Notifier


class FacenoteWorkerFactory (
	private val notifier: Notifier,
	private  val backupRepository: BackupRepository
): WorkerFactory() {
	override fun createWorker(
		appContext: Context,
		workerClassName: String,
		workerParameters: WorkerParameters
	): ListenableWorker? {
		return when(workerClassName){
			BackupWorker::class.java.name -> BackupWorker(appContext,workerParameters, notifier, backupRepository)
			RestoreWorker::class.java.name -> RestoreWorker(appContext,workerParameters, notifier, backupRepository)
			ReminderWorker::class.java.name -> ReminderWorker(appContext, workerParameters,notifier)
			else -> null
		}
	}
}
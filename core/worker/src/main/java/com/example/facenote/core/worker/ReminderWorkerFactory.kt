package com.example.facenote.core.worker

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.example.facenote.core.notifications.Notifier
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReminderWorkerFactory @Inject constructor(
	private val notifier: Notifier
): WorkerFactory() {
	override fun createWorker(
		appContext: Context,
		workerClassName: String,
		workerParameters: WorkerParameters
	): ListenableWorker? {
		return when(workerClassName){
			ReminderWorker::class.java.name -> ReminderWorker(appContext,workerParameters, notifier)
			else -> null
		}
	}
}
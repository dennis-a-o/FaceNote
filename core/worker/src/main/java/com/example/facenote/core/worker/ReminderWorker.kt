package com.example.facenote.core.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.facenote.core.notifications.Notifier

class ReminderWorker(
	context: Context,
	params: WorkerParameters,
	private val notifier: Notifier
) : CoroutineWorker(context, params) {

	override suspend fun doWork(): Result {
		val noteId = inputData.getLong("NOTE_ID", -1L)
		val noteTitle = inputData.getString("NOTE_TITLE") ?: "Reminder"
		val noteContent = inputData.getString("NOTE_CONTENT") ?: ""

		if (noteId != -1L){
			notifier.postReminderNotifications(noteId,noteTitle,noteContent)
		}
		return Result.success()
	}
}
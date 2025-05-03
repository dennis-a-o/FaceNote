package com.example.facenote.core.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationManagerCompat
import com.example.facenote.core.data.repository.NoteRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class NotificationActionReceiver: BroadcastReceiver() {
	private val scope = MainScope()

	@Inject
	lateinit var noteRepository: NoteRepository

	override fun onReceive(
		context: Context,
		intent: Intent
	) {
		val noteId = intent.getLongExtra("NOTE_ID",0)

		when(intent.action){
			ACTION_DONE -> {
				scope.launch {
					noteRepository.setNoteReminderDone(noteId)
				}
				NotificationManagerCompat.from(context).cancel(NOTIFICATION_ID)
			}
			ACTION_CANCEL -> {
				NotificationManagerCompat.from(context).cancel(NOTIFICATION_ID)
			}
		}
	}
}
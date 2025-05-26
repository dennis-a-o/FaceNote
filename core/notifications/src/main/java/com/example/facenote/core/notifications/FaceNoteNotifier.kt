package com.example.facenote.core.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.net.toUri
import com.example.facenote.core.ui.R
import com.example.facenote.core.ui.theme.Purple40
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

private const val TARGET_ACTIVITY_NAME = "com.example.facenote.MainActivity"
private const val DEEP_LINK_SCHEME_AND_HOST = "app://com.example.facenote"
private const val NOTE_EDITOR_ROUTE = "note_editor_route"
private const val NOTE_BACKUP_ROUTE = "backup"
const val REMINDER_CHANNEL_ID = "note_reminder"
const val BACKUP_CHANNEL_ID = "backup"
const val NOTIFICATION_ID = 1
const val ACTION_DONE = "action_done"
const val ACTION_CANCEL = "action_cancel"
const val REQUEST_CODE = 1

class FaceNoteNotifier @Inject constructor(
	@ApplicationContext private val context: Context
): Notifier {

	private lateinit var notificationManager: NotificationManager

	init {
		context.createNotificationChannel()
		notificationManager = context.getSystemService(NotificationManager::class.java)
	}
	override fun postReminderNotifications(
		noteId: Long,
		noteTitle: String,
		noteContent: String
	) {
		val contentIntent = PendingIntent.getActivity(
			context,
			REQUEST_CODE,
			Intent().apply {
				action = Intent.ACTION_VIEW
				data = noteDeepLinkUri(noteId)
				component = ComponentName(
					context.packageName,
					TARGET_ACTIVITY_NAME,
				)
			},
			PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
		)

		val donePendingIntent = PendingIntent.getBroadcast(
			context,
			REQUEST_CODE,
			Intent(context, NotificationActionReceiver::class.java).apply {
				action = ACTION_DONE
				putExtra("NOTE_ID", noteId)
			},
			PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
		)

		val cancelPendingIntent = PendingIntent.getBroadcast(
			context,
			REQUEST_CODE,
			Intent(context,NotificationActionReceiver::class.java).apply {
				action = ACTION_CANCEL
			},
			PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
		)

		val builder = NotificationCompat.Builder(context, REMINDER_CHANNEL_ID)
			.setSmallIcon(R.drawable.ic_note_alt_outlined)
			.setContentTitle(noteTitle)
			.setContentText(noteContent)
			.setPriority(NotificationCompat.PRIORITY_HIGH)
			.setCategory(NotificationCompat.CATEGORY_REMINDER)
			.setAutoCancel(true)
			.setContentIntent(contentIntent)
			.addAction(0,"Cancel",cancelPendingIntent)
			.addAction(0,"Done",donePendingIntent)
			.apply {
				setColor(if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) Color.toArgb(Purple40.value.toLong()) else Color.BLUE)
			}
			.setColorized(true)
			.build()

		notificationManager.notify(NOTIFICATION_ID, builder)
	}

	override fun postBackupNotifications(
		title: String,
		operation: String,
		progress: Double,
		showBackupFiles: Boolean
	) {

		val contentIntent = PendingIntent.getActivity(
			context,
			REQUEST_CODE,
			Intent().apply {
				action = Intent.ACTION_VIEW
				data = backupDeepLinkUrl(true, showBackupFiles)
				component = ComponentName(
					context.packageName,
					TARGET_ACTIVITY_NAME,
				)
			},
			PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
		)

		val builder = NotificationCompat.Builder(context, BACKUP_CHANNEL_ID)
			.setSmallIcon(R.drawable.ic_note_alt_outlined)
			.setContentTitle(title)
			.setContentText(operation)
			.setAutoCancel(true)
			.setOnlyAlertOnce(true)
			.setPriority(NotificationCompat.PRIORITY_DEFAULT)
			.setCategory(NotificationCompat.CATEGORY_PROGRESS)
			.setContentIntent(contentIntent)
			.apply {
				setColor(if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) Color.toArgb(Purple40.value.toLong()) else Color.BLUE)
			}
			.setColorized(true)
			.setProgress(100, (progress * 100).toInt(),false)
			.build()

		notificationManager.notify(NOTIFICATION_ID, builder)
	}
}


private fun Context.createNotificationChannel(){
	if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
		val reminderChannel = NotificationChannel(
			REMINDER_CHANNEL_ID,
			"Notes reminder",
			NotificationManager.IMPORTANCE_DEFAULT
		).apply {
			description = "Notifications for note reminders"
			enableLights(true)
			enableVibration(true)
		}

		val backupChannel = NotificationChannel(
			BACKUP_CHANNEL_ID,
			"Notes backup",
			NotificationManager.IMPORTANCE_UNSPECIFIED
		).apply {
			description = "Notifications for note backup"
			enableLights(false)
			enableVibration(false)
		}

		val notificationManager = this.getSystemService(NotificationManager::class.java)
		notificationManager.createNotificationChannel(reminderChannel)
		notificationManager.createNotificationChannel(backupChannel)
	}
}

private fun noteDeepLinkUri(id:Long,isCheckList: Boolean = false) = "$DEEP_LINK_SCHEME_AND_HOST/$NOTE_EDITOR_ROUTE/$id/$isCheckList".toUri()
private fun backupDeepLinkUrl(inProgress: Boolean,showBackupFiles: Boolean) = "$DEEP_LINK_SCHEME_AND_HOST/$NOTE_BACKUP_ROUTE/$inProgress/$showBackupFiles".toUri()
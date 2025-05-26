package com.example.facenote.core.notifications

interface Notifier {
	fun postReminderNotifications(noteId:Long, noteTitle: String, noteContent: String)
	fun postBackupNotifications(title: String,operation: String, progress: Double,showBackupFiles: Boolean)
}
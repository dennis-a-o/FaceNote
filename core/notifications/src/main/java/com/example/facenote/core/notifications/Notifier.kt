package com.example.facenote.core.notifications

interface Notifier {
	fun postReminderNotifications(noteId:Long, noteTitle: String, noteContent: String)
}
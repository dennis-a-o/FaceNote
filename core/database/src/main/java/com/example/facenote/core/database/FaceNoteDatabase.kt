package com.example.facenote.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.facenote.core.database.dao.NoteDao
import com.example.facenote.core.database.dao.NoteImageDao
import com.example.facenote.core.database.model.NoteEntity
import com.example.facenote.core.database.model.NoteImageEntity

@Database(
	entities = [
		NoteEntity::class,
		NoteImageEntity::class
	],
	version = 1,
	autoMigrations = [],
	exportSchema = false
)
abstract class FaceNoteDatabase: RoomDatabase() {
	abstract fun noteDao(): NoteDao
	abstract fun noteImageDao(): NoteImageDao
}
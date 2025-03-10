package com.example.facenote.core.database.di

import com.example.facenote.core.database.FaceNoteDatabase
import com.example.facenote.core.database.dao.NoteDao
import com.example.facenote.core.database.dao.NoteImageDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal object DaoModule {
	@Provides
	fun providesNoteDao(
		database: FaceNoteDatabase
	): NoteDao = database.noteDao()

	@Provides
	fun providesNoteImageDao(
		database: FaceNoteDatabase
	): NoteImageDao = database.noteImageDao()
}
package com.example.facenote.core.database.di

import android.content.Context
import androidx.room.Room
import com.example.facenote.core.database.FaceNoteDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object DatabaseModule {
	@Provides
	@Singleton
	fun providesFaceNoteDatabase(
		@ApplicationContext context: Context
	): FaceNoteDatabase = Room.databaseBuilder(
		context,
		FaceNoteDatabase::class.java,
		"facenote-database"
	).build()
}
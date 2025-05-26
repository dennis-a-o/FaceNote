package com.example.facenote.core.data.di

import com.example.facenote.core.data.repository.BackupRepository
import com.example.facenote.core.data.repository.BackupRepositoryImpl
import com.example.facenote.core.data.repository.NoteRepository
import com.example.facenote.core.data.repository.NoteRepositoryImpl
import com.example.facenote.core.data.repository.SettingRepository
import com.example.facenote.core.data.repository.SettingRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {
	@Binds
	abstract fun bindSettingRepository(
		settingRepositoryImpl: SettingRepositoryImpl
	):SettingRepository

	@Binds
	abstract  fun  bindNoteRepository(
		noteRepositoryImpl: NoteRepositoryImpl
	): NoteRepository

	@Binds
	abstract fun bindBackupRepository(
		backupRepositoryImpl: BackupRepositoryImpl
	): BackupRepository
}
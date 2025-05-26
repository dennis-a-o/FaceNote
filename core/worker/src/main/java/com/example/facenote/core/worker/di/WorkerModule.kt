package com.example.facenote.core.worker.di

import android.content.Context
import androidx.work.WorkManager
import androidx.work.WorkerFactory
import com.example.facenote.core.data.repository.BackupRepository
import com.example.facenote.core.notifications.Notifier
import com.example.facenote.core.worker.FacenoteWorkerFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object WorkerModule {

	@Provides
	fun provideBackupWorkManager(
		@ApplicationContext context: Context,
	): WorkManager {
		return WorkManager.getInstance(context)
	}

	@Provides
	fun providesWorkerFactory(
		notifier: Notifier,
		backupRepository: BackupRepository
	): WorkerFactory{
		return FacenoteWorkerFactory(notifier, backupRepository)
	}
}
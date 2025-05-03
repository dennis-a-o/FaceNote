package com.example.facenote.core.worker.di

import android.content.Context
import androidx.work.Configuration
import androidx.work.WorkManager
import com.example.facenote.core.worker.ReminderWorkerFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Inject
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object WorkerModule {

	@Provides
	@Singleton
	fun provideWorkManager(
		@ApplicationContext context: Context,
		workerFactory: ReminderWorkerFactory
	): WorkManager {
		WorkManager.initialize(
			context,
			Configuration.Builder()
				.setWorkerFactory(workerFactory)
				.build()
		)
		return WorkManager.getInstance(context)
	}
}
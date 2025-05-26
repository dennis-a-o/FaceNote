package com.example.facenote

import android.app.Application
import androidx.work.Configuration
import androidx.work.WorkManager
import androidx.work.WorkerFactory
import com.jakewharton.threetenabp.AndroidThreeTen
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class FaceNoteApplication: Application(), Configuration.Provider{
	@Inject
	lateinit var workerFactory: WorkerFactory

	override val workManagerConfiguration: Configuration
		get() = Configuration.Builder()
			.setWorkerFactory(workerFactory)
			.build()

	override fun onCreate() {
		super.onCreate()
		WorkManager.initialize(this, workManagerConfiguration)
		AndroidThreeTen.init(this)
	}
}
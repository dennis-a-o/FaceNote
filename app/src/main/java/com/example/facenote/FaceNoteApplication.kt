package com.example.facenote

import android.app.Application
import com.jakewharton.threetenabp.AndroidThreeTen
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class FaceNoteApplication: Application() {
	override fun onCreate() {
		super.onCreate()
		AndroidThreeTen.init(this)
	}
}
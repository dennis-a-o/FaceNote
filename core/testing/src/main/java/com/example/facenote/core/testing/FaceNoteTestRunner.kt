package com.example.facenote.core.testing

import android.app.Application
import android.content.Context
import androidx.test.runner.AndroidJUnitRunner
import dagger.hilt.android.testing.HiltTestApplication


class FaceNoteTestRunner : AndroidJUnitRunner() {
	override fun newApplication(
		cl: ClassLoader?,
		className: String?,
		context: Context?
	): Application? {
		return super.newApplication(cl, HiltTestApplication::class.java.name, context)
	}
}
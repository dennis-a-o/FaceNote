package com.example.facenote.core.notifications.di

import android.content.Context
import com.example.facenote.core.notifications.Notifier
import com.example.facenote.core.notifications.FaceNoteNotifier
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object  NotificationsModule{

	@Provides
	@Singleton
	fun  providesNotifier(
		@ApplicationContext context: Context
	): Notifier{
		return FaceNoteNotifier(context)
	}
}
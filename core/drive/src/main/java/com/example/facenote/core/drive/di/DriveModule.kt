package com.example.facenote.core.drive.di

import android.content.Context
import com.google.api.client.googleapis.extensions.android.accounts.GoogleAccountManager
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.http.HttpRequest
import com.google.api.client.http.HttpRequestInitializer
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.drive.Drive
import com.google.api.services.drive.DriveScopes
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DriveModule {

	@Singleton
	@Provides
	fun providesDrive(
		@ApplicationContext context: Context
	): Drive? {

		val credential = GoogleAccountCredential
			.usingOAuth2(context,listOf(DriveScopes.DRIVE_FILE))
		credential.selectedAccount = credential.allAccounts[0]

		return Drive.Builder(
			GoogleNetHttpTransport.newTrustedTransport(),
			GsonFactory(),
			credential
		).setHttpRequestInitializer(
			object : HttpRequestInitializer{
				override fun initialize(request: HttpRequest) {
					credential.initialize(request)
					request.setConnectTimeout(60_000)//60 seconds
					request.setReadTimeout(60_000)//60 seconds
				}
			}
		).build()
	}
}

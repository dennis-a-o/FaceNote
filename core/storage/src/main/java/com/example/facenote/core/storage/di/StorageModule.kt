package com.example.facenote.core.storage.di

import com.example.facenote.core.storage.FileSystemImageStorage
import com.example.facenote.core.storage.ImageStorage
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class StorageModule {
	@Binds
	abstract fun bindImageStorage(
		fileSystemImageStorage: FileSystemImageStorage
	): ImageStorage
}
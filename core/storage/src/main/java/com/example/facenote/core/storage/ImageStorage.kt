package com.example.facenote.core.storage

import android.net.Uri
import java.io.File

interface ImageStorage {
	suspend fun saveImage(imageUri: Uri,filename:String): Result<String>
	suspend fun getImage(filename: String): Result<File>
	suspend fun deleteImage(filename: String): Result<Unit>
}
package com.example.facenote.core.storage

import android.content.Context
import android.net.Uri
import android.util.Log
import java.io.File
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class FileSystemImageStorage @Inject constructor(
	@ApplicationContext private val context: Context
):ImageStorage {
	private val imgDir = context.getDir("facenote", Context.MODE_PRIVATE)

	override suspend fun saveImage(imageUri: Uri, filename: String): Result<String> = withContext(Dispatchers.IO){
		runCatching {
			val file = File(imgDir,filename)
			context.contentResolver.openInputStream(imageUri)?.use { input ->
				file.outputStream().use { output ->
					input.copyTo(output)
				}
			}
			filename
		}
	}

	override suspend fun getImage(filename: String): Result<File> = withContext(Dispatchers.IO){
		kotlin.runCatching {
			val file = File(imgDir,filename)
			file
		}
	}

	override suspend fun deleteImage(filename: String): Result<Unit> = withContext(Dispatchers.IO) {
		runCatching {
			val file = File(imgDir, filename)
			if (!(file.delete())) {
				Log.e("FileSystemImageStorage","Could not delete file ${file.name}")
			}
		}
	}
}
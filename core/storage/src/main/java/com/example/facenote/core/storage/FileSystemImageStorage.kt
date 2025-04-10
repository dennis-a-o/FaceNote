package com.example.facenote.core.storage

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import java.io.File
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.FileOutputStream
import javax.inject.Inject

class FileSystemImageStorage @Inject constructor(
	@ApplicationContext private val context: Context
):ImageStorage {
	private val dir = context.dataDir
	private val imageDir ="files/facenote/image/"

	override suspend fun saveImage(imageUri: Uri, filename: String): Result<String> = withContext(Dispatchers.IO){
		runCatching {
			val file = File(dir,imageDir+filename)

			if (!file.exists()) file.createNewFile()

			context.contentResolver.openInputStream(imageUri)?.use { input ->
				file.outputStream().use { output ->
					input.copyTo(output)
				}
			}
			"${imageDir}/$filename"
		}
	}

	override suspend fun saveBitmap(bitmap: Bitmap, filename: String): Result<String> = withContext(Dispatchers.IO) {
		runCatching {
			val file = File(dir,imageDir+filename)

			if (!file.exists()) file.createNewFile()

			FileOutputStream(file).use { out ->
				bitmap.compress(Bitmap.CompressFormat.PNG,90,out)
			}

				"$imageDir/$filename"
		}
	}

	override suspend fun getImage(filename: String): Result<File> = withContext(Dispatchers.IO){
		kotlin.runCatching {
			val file = File(dir,imageDir+filename)
			file
		}
	}

	override suspend fun deleteImage(filename: String): Result<Unit> = withContext(Dispatchers.IO) {
		runCatching {
			val file = File(dir, imageDir+filename)
			if (!(file.delete())) {
				Log.e("FileSystemImageStorage","Could not delete file ${file.name}")
			}
		}
	}
}
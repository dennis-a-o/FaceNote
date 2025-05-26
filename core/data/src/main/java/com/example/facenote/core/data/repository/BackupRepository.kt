package com.example.facenote.core.data.repository

import com.example.facenote.core.model.BackupResult
import com.example.facenote.core.model.DriveFile
import kotlinx.coroutines.flow.Flow
import java.io.File

interface BackupRepository {
	suspend fun exportDatabase(): File
	suspend fun exportImages(): File
	suspend fun zipBackup(dbFile: File, imagesFile: File): File
	suspend fun uploadBackup(backupFile: File): Flow<BackupResult<Unit>>
	fun downloadBackup(backupId:String): Flow<BackupResult<File>>
	fun getFiles(): Flow<Result<List<DriveFile>>>
	suspend fun deleteFile(id: String): Result<String>
	suspend fun unzipBackup(file: File): Map<String, File>
	suspend fun importDatabase(file: File)
	suspend fun importImages(file: File)
	suspend fun setLastBackup(lastBackup:Long)
	fun getLastBackup():Flow<Long?>

}
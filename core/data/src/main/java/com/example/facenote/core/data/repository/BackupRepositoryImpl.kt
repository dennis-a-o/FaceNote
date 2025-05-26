package com.example.facenote.core.data.repository

import android.content.Context
import com.example.facenote.core.datastore.FaceNotePreferencesDataSource
import com.example.facenote.core.drive.DriveServiceHelper
import com.example.facenote.core.model.BackupResult
import com.example.facenote.core.model.DriveFile
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import java.io.BufferedOutputStream
import java.io.File
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream
import javax.inject.Inject

class BackupRepositoryImpl @Inject constructor(
	@ApplicationContext private val context: Context,
	private val driveServiceHelper: DriveServiceHelper,
	private val datastorePreference: FaceNotePreferencesDataSource
): BackupRepository {
	private val IMAGE_DIR ="files/facenote/image/"
	private val BACKUP_DIR = "facenote_backup_temp"
	private val RESTORE_DIR = "facenote_restore_temp"

	override suspend fun exportDatabase(): File {
		val dbPath = context.getDatabasePath("facenote-database")
		val tempDir = File(context.cacheDir, BACKUP_DIR)
		tempDir.mkdirs()

		val dbBackupFile = File(tempDir,"db_backup.db")
		dbPath.copyTo(dbBackupFile, overwrite = true)

		return dbBackupFile
	}

	override suspend fun exportImages(): File {
		val imagesDir = File(context.dataDir,IMAGE_DIR)
		val tempDir = File(context.cacheDir, BACKUP_DIR)
		tempDir.mkdirs()

		val zipFile = File(tempDir, "images_backup.zip")

		ZipOutputStream(BufferedOutputStream(zipFile.outputStream())).use { out ->
			imagesDir.walk().filter { it.isFile }.forEach { file ->
				out.putNextEntry(ZipEntry(file.name))
				file.inputStream().use { it.copyTo(out) }
				out.closeEntry()
			}
		}

		return zipFile
	}

	override suspend fun zipBackup(dbFile: File, imagesFile: File): File {
		val tempDir = File(context.cacheDir, BACKUP_DIR)
		tempDir.mkdirs()

		val backupFile = File(tempDir, "backup_${System.currentTimeMillis()}.zip")

		ZipOutputStream(BufferedOutputStream(backupFile.outputStream())).use { out ->
			out.putNextEntry(ZipEntry("database.db"))
			dbFile.inputStream().use { it.copyTo(out) }
			out.closeEntry()

			out.putNextEntry(ZipEntry("images.zip"))
			imagesFile.inputStream().use { it.copyTo(out) }
			out.closeEntry()
		}

		return backupFile
	}

	override suspend fun uploadBackup(backupFile: File): Flow<BackupResult<Unit>> {
		return driveServiceHelper.uploadFile(backupFile,"application/zip")
	}

	override  fun downloadBackup(backupId: String): Flow<BackupResult<File>> {
		return driveServiceHelper.downloadFile(backupId)
	}

	override  fun getFiles(): Flow<Result<List<DriveFile>>> {
		return driveServiceHelper.getFiles()
	}

	override suspend fun deleteFile(id: String): Result<String> {
		return driveServiceHelper.deleteFile(id)
	}

	override suspend fun unzipBackup(file: File): Map<String, File> {
		val resDir = File(context.cacheDir, RESTORE_DIR)
		resDir.mkdirs()

		val zipFiles = mutableMapOf<String, File>()

		ZipInputStream(file.inputStream()).use { zin ->
			var  entry = zin.nextEntry

			while (entry != null){
				val filename = entry.name
				val file = File(resDir, filename)


				file.outputStream().use { fOut ->
					zin.copyTo(fOut)
				}
				zin.closeEntry()

				zipFiles.put(filename, file)
				entry = zin.nextEntry
			}
		}

		return zipFiles
	}

	override suspend fun importDatabase(file: File) {

		val dbPath = context.getDatabasePath("facenote-database")

		file.copyTo(dbPath, overwrite = true)

		context.getDatabasePath("facenote-database-shm").delete()
		context.getDatabasePath("facenote-database-wal").delete()
	}

	override suspend fun importImages(imageFile: File) {
		val resDir = File(context.cacheDir, RESTORE_DIR)
		resDir.mkdirs()

		ZipInputStream(imageFile.inputStream()).use { zin ->
			var entry = zin.nextEntry
			while (entry != null) {
				var filename = entry.name
				var resFile = File(resDir, filename)
				//extract image to temp dir
				resFile.outputStream().use { fOut ->
					zin.copyTo(fOut)
				}
				zin.closeEntry()
				//copy extracted image from temp dir  to image dir
				val imagesDir = File(context.dataDir,IMAGE_DIR+filename)
				resFile.copyTo(imagesDir,overwrite = true)

				entry = zin.nextEntry
			}
		}
	}

	override suspend fun setLastBackup(lastBackup: Long) {
		datastorePreference.setLastBackup(lastBackup)
	}

	override fun getLastBackup(): Flow<Long?> {
		return  datastorePreference.getLastBackup()
	}
}
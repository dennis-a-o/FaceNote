package com.example.facenote.feature.backup

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.example.facenote.core.data.repository.BackupRepository
import com.example.facenote.core.model.DriveFile
import com.example.facenote.core.worker.BackupWorker
import com.example.facenote.core.worker.RestoreWorker
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.Scope
import com.google.api.services.drive.DriveScopes
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class BackupViewModel @Inject constructor(
	@ApplicationContext val context: Context,
	private val backupRepository: BackupRepository,
	private val workManager: WorkManager,
	savedStateHandle: SavedStateHandle
): ViewModel() {
	private val inProgress = savedStateHandle["inProgress"] ?: false
	private val showBackupFiles =  savedStateHandle["showBackupFiles"] ?: false

	private val _backupState = MutableStateFlow(BackupState(inProgress = inProgress, showBackupFiles = showBackupFiles))
	val backupState = _backupState.asStateFlow()

	private val gso = GoogleSignInOptions
		.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
		.requestEmail()
		.requestProfile()
		.requestScopes(Scope(DriveScopes.DRIVE_FILE), Scope(DriveScopes.DRIVE_APPDATA))
		.build()

	val googleSignInClient = GoogleSignIn.getClient(context, gso)

	init {
		getFiles()
		lastBackup()
		initializeAccount()
		getWorkerRunning()
	}

	private fun initializeAccount(){
		viewModelScope.launch {
			val account = GoogleSignIn.getLastSignedInAccount(context)
			_backupState.update {
				it.copy(
					account = account,
					isSignedIn = account != null,
				)
			}
		}
	}

	fun resetActionState(){
		viewModelScope.launch{
			_backupState.update {
				it.copy(
					success = null,
					error = null
				)
			}
		}
	}

	fun cancel(){
		viewModelScope.launch{
			workManager.cancelUniqueWork("restore-worker")
			workManager.cancelUniqueWork("backup-worker")
		}
	}

	fun showBackupFiles(show: Boolean){
		viewModelScope.launch{
			_backupState.update {
				it.copy(showBackupFiles = show)
			}
		}
	}

	fun logout(){
		googleSignInClient.signOut()
		initializeAccount()
	}

	fun getFiles(){
		viewModelScope.launch {
			_backupState.update {
				it.copy(
					filesLoading = true,
					filesError = null
				)
			}
			val files = backupRepository.getFiles().first()

			when {
				files.isSuccess -> {
					_backupState.update {
						it.copy(
							files = files.getOrNull(),
							filesLoading = false
						)
					}
				}
				files.isFailure ->{
					_backupState.update {
						it.copy(
							filesLoading = false,
							filesError = files.exceptionOrNull()?.message.toString()
						)
					}
				}
			}
		}
	}

	fun delete(id: String){
		viewModelScope.launch {
			val result = backupRepository.deleteFile(id)
			when {
				result.isSuccess -> {
					_backupState.update {
						it.copy(
							success = result.getOrDefault("Success"),
							files = it.files?.filter { it.id != id }
						)
					}
				}
				result.isFailure ->{
					_backupState.update {
						it.copy(error = result.exceptionOrNull()?.message.toString())
					}
				}
			}
		}
	}

	fun restore(fileId :String){
		viewModelScope.launch {
			_backupState.update {
				it.copy(inProgress = true)
			}
			workManager.cancelAllWork()
			try {
				val restoreWorkRequest = OneTimeWorkRequestBuilder<RestoreWorker>()
					.setInputData(workDataOf("fileId" to fileId))
					.build()

				workManager.enqueueUniqueWork("restore-worker", ExistingWorkPolicy.REPLACE, restoreWorkRequest)
				workManager.getWorkInfosForUniqueWorkFlow("restore-worker")
					.collect { listWorkInfo ->
						listWorkInfo.firstOrNull()?.let { info ->
							when(info.state){
								WorkInfo.State.RUNNING -> {
									val operation = info.progress.getString("operation")
									val progress = info.progress.getDouble("progress", -1.0)
									_backupState.update {
										it.copy(
											progress = if(progress >= 0.0) progress else it.progress,
											operation = operation ?: it.operation
										)
									}
								}
								WorkInfo.State.SUCCEEDED -> {
									_backupState.update {
										it.copy(
											operation ="Restore completed",
											success = "Restore completed",
											inProgress = false
										)
									}
								}
								WorkInfo.State.FAILED, WorkInfo.State.CANCELLED -> {
									val  backupState = info.outputData.getString("error") ?: "Restore failed: Unknown error"
									_backupState.update {
										it.copy(
											error = backupState,
											operation = backupState,
											inProgress = false
										)
									}
								}
								else -> {}
							}
						}
					}
			}catch (e: Exception){
				_backupState.update {
					it.copy(
						error = e.message.toString(),
						inProgress = false
					)
				}
			}
		}
	}

	fun backup(){
		viewModelScope.launch {
			_backupState.update {
				it.copy(inProgress = true)
			}
			workManager.cancelAllWork()
			try {
				val backupWorkRequest = OneTimeWorkRequestBuilder<BackupWorker>()
					.build()

				workManager.enqueueUniqueWork("backup-worker",ExistingWorkPolicy.REPLACE,backupWorkRequest)

				workManager.getWorkInfosForUniqueWorkFlow("backup-worker")
					.collect { workInfoList ->
						workInfoList.firstOrNull()?.let { workInfo ->
							when (workInfo?.state) {
								WorkInfo.State.RUNNING -> {
									val operation = workInfo.progress.getString("operation")
									val progress = workInfo.progress.getDouble("progress", 0.0)
									_backupState.update {
										it.copy(
											progress = if(progress >= 0.0) progress else it.progress,
											operation = operation ?: it.operation
										)
									}
								}

								WorkInfo.State.SUCCEEDED -> {
									_backupState.update {
										it.copy(
											operation = "Backup completed",
											success = "Backup completed",
											inProgress = false
										)
									}
									lastBackup()
								}

								WorkInfo.State.FAILED, WorkInfo.State.CANCELLED -> {
									val backupState = workInfo.outputData.getString("error") ?: "Backup failed: Unknown error"
									_backupState.update {
										it.copy(
											operation = backupState,
											error = backupState,
											inProgress = false
										)
									}
								}

								else -> {}
							}
						}
					}
			} catch (e: Exception) {
				_backupState.update {
					it.copy(
						error = e.message.toString(),
						inProgress = false
					)
				}
			}
		}
	}

	fun updateAccountInfo(account: GoogleSignInAccount?){
		viewModelScope.launch {
			_backupState.update {
				it.copy(
					account = account,
					isSignedIn = account != null
				)
			}
		}
	}

	private fun lastBackup(){
		viewModelScope.launch {
			val lastBackup = backupRepository.getLastBackup().first()
			_backupState.update {
				it.copy(lastBackup = lastBackup )
			}
		}
	}

	private fun getWorkerRunning(){
		viewModelScope.launch {
			try {
				if (!showBackupFiles) {
					workManager.getWorkInfosForUniqueWorkFlow("backup-worker")

						.collect { workInfoList ->
							workInfoList.firstOrNull()?.let { workInfo ->
								when (workInfo?.state) {
									WorkInfo.State.RUNNING -> {
										val operation = workInfo.progress.getString("operation")
										val progress = workInfo.progress.getDouble("progress", 0.0)
										_backupState.update {
											it.copy(
												progress = if (progress >= 0.0) progress else it.progress,
												operation = operation ?: it.operation
											)
										}
									}

									WorkInfo.State.SUCCEEDED -> {
										_backupState.update {
											it.copy(
												operation = "Backup completed",
												success = "Backup completed",
												inProgress = false
											)
										}
										lastBackup()
									}

									WorkInfo.State.FAILED, WorkInfo.State.CANCELLED -> {
										val backupState = workInfo.outputData.getString("error")
											?: "Backup failed: Unknown error"
										_backupState.update {
											it.copy(
												operation = backupState,
												error = backupState,
												inProgress = false
											)
										}
									}

									else -> {}
								}
							}
						}
				}else {
					workManager.getWorkInfosForUniqueWorkFlow("restore-worker")
						.collect { listWorkInfo ->
							listWorkInfo.firstOrNull()?.let { info ->
								when (info.state) {
									WorkInfo.State.RUNNING -> {
										val operation = info.progress.getString("operation")
										val progress = info.progress.getDouble("progress", -1.0)
										_backupState.update {
											it.copy(
												progress = if (progress >= 0.0) progress else it.progress,
												operation = operation ?: it.operation
											)
										}
									}

									WorkInfo.State.SUCCEEDED -> {
										_backupState.update {
											it.copy(
												operation = "Restore completed",
												success = "Restore completed",
												inProgress = false
											)
										}
									}

									WorkInfo.State.FAILED, WorkInfo.State.CANCELLED -> {
										val backupState = info.outputData.getString("error")
											?: "Restore failed: Unknown error"
										_backupState.update {
											it.copy(
												error = backupState,
												operation = backupState,
												inProgress = false
											)
										}
									}

									else -> {}
								}
							}
						}
				}
			}catch (e: Exception){
				_backupState.update {
					it.copy(
						error = e.message.toString(),
						inProgress = false
					)
				}
			}
		}
	}
}


data class BackupState(
	val isSignedIn: Boolean = false,
	val account: GoogleSignInAccount? = null,
	val operation: String = "",
	val progress: Double = 0.0,
	val files: List<DriveFile>? = null,
	val filesError: String? = null,
	val filesLoading: Boolean = false,
	val showBackupFiles: Boolean = false,
	val inProgress: Boolean = false,
	val error: String? = null,
	val success: String? = null,
	val lastBackup: Long? = null
)
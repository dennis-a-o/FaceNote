package com.example.facenote.core.model

sealed class BackupResult<out T: Any>{
	data object Idle: BackupResult<Nothing>()
	data class Progress(val progress: Double): BackupResult<Nothing>()
	data class Operation(val operation: String): BackupResult<Nothing>()
	data class Error(val message: String, val cause: Throwable? = null): BackupResult<Nothing>()
	data class Success<out T: Any>(val result: T): BackupResult<T>()
}
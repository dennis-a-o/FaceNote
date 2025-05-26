package com.example.facenote.feature.reminder

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.example.facenote.core.data.repository.NoteRepository
import com.example.facenote.core.model.Note
import com.example.facenote.core.model.RepeatInterval
import com.example.facenote.core.worker.ReminderWorker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.threeten.bp.Instant
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime
import org.threeten.bp.LocalTime
import org.threeten.bp.ZoneId
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class ReminderViewModel @Inject constructor(
	private val noteRepository: NoteRepository,
	private val workManager: WorkManager,
	savedStateHandle: SavedStateHandle
):ViewModel() {
	private val remindAt = savedStateHandle.getStateFlow("remindAt", 0L)
	private val noteId = savedStateHandle.getStateFlow("noteId", 0L)
	private val _note = MutableStateFlow<Note?>(null)

	private val _reminderState = MutableStateFlow(
		ReminderState(
			isSaved = remindAt.value > 0L,
			selectedDate = if (remindAt.value != 0L) {
				Instant.ofEpochMilli(remindAt.value).atZone(ZoneId.systemDefault()).toLocalDate()
			}else LocalDate.now(),
			selectedTime = if(remindAt.value != 0L) {
				Instant.ofEpochMilli(remindAt.value).atZone(ZoneId.systemDefault()).toLocalTime()
			}else LocalTime.now()
		)
	)

	init {
		getNote(noteId.value)
	}

	val reminderState = _reminderState.asStateFlow()

	fun onEvent(event: ReminderFormEvent){
		when(event){
			ReminderFormEvent.Clear -> clearReminder(noteId.value)
			is ReminderFormEvent.DateChanged -> {
				_reminderState.update {
					if (event.localDate >= LocalDate.now()) {
						it.copy(selectedDate = event.localDate, selectedDateError = null)
					}else{
						it.copy(selectedDateError = "The date has passed")
					}
				}
			}
			is ReminderFormEvent.RepeatIntervalChanged -> {
				_reminderState.update {
					it.copy(repeatInterval = event.repeatInterval)
				}
			}
			ReminderFormEvent.Save -> saveReminder(noteId.value)
			is ReminderFormEvent.TimeChanged -> {
				_reminderState.update {
					if (it.selectedDate > LocalDate.now()){
						it.copy(selectedTime = event.localTime, selectedTimeError = null)
					}else {
						if (event.localTime >= LocalTime.now()) {
							it.copy(selectedTime = event.localTime, selectedTimeError = null)
						} else {
							it.copy(selectedTimeError = "The time has passed")
						}
					}
				}
			}
		}
	}

	private fun saveReminder(noteId: Long){
		viewModelScope.launch {
			val reminderTime = LocalDateTime
				.of(_reminderState.value.selectedDate, _reminderState.value.selectedTime)
				.atZone(ZoneId.systemDefault())
				.toInstant()
				.toEpochMilli()

			noteRepository.setNoteReminder(noteId,reminderTime)

			scheduleReminder(
				noteId = noteId,
				noteTitle = _note.value?.title ?: "",
				noteContent = "",
				reminderTime = reminderTime,
				repeatInterval = _reminderState.value.repeatInterval
			)

			_reminderState.update {
				it.copy(isSaved = true)
			}
		}
	}

	private fun clearReminder(noteId: Long){
		viewModelScope.launch {
			workManager.cancelUniqueWork("note_reminder_$noteId")
			noteRepository.clearNoteReminder(noteId)
			_reminderState.update { it.copy(isSaved = false) }
		}
	}

	private fun scheduleReminder(
		noteId: Long,
		noteTitle: String,
		noteContent: String,
		reminderTime: Long,
		repeatInterval: RepeatInterval
	){
		val currentTime = System.currentTimeMillis()
		val delay = reminderTime - currentTime

		if (delay > 0) {
			if (repeatInterval != RepeatInterval.NONE) {
				val reminderRequest = PeriodicWorkRequestBuilder<ReminderWorker>(
					repeatInterval.toMillis(),
					TimeUnit.MILLISECONDS
				).setInitialDelay(delay, TimeUnit.MILLISECONDS)
					.setInputData(
						workDataOf(
							"NOTE_ID" to noteId,
							"NOTE_TITLE" to noteTitle,
							"NOTE_CONTENT" to noteContent,
							"REPEAT_INTERVAL" to repeatInterval.name
						)
					).build()

				workManager.enqueueUniquePeriodicWork(
					"note_reminder_$noteId",
					ExistingPeriodicWorkPolicy.REPLACE,
					reminderRequest
				)
			}else{
				val reminderRequest = OneTimeWorkRequestBuilder<ReminderWorker>()
					.setInitialDelay(delay, TimeUnit.MILLISECONDS)
					.setInputData(
						workDataOf(
							"NOTE_ID" to noteId,
							"NOTE_TITLE" to noteTitle,
							"NOTE_CONTENT" to noteContent,
							"REPEAT_INTERVAL" to repeatInterval.name
						)
					)
					.build()

				workManager.enqueueUniqueWork(
					"note_reminder_$noteId",
					ExistingWorkPolicy.REPLACE,
					reminderRequest
				)
			}
		}
	}

	private fun getNote(noteId: Long){
		viewModelScope.launch {
			val note= noteRepository.getNoteDetail(noteId).first()
			_note.value  = note
			note?.let{
				_reminderState.update {
					it.copy(isSaved = note.remindAt != null)
				}
			}
		}
	}
}

data class ReminderState(
	val isSaved: Boolean = false,
	val selectedDate: LocalDate = LocalDate.now(),
	val selectedDateError: String? = null,
	val selectedTime: LocalTime = LocalTime.now(),
	val selectedTimeError: String? = null,
	val repeatInterval: RepeatInterval = RepeatInterval.NONE
)
package com.example.facenote.feature.reminder

import com.example.facenote.core.model.RepeatInterval
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalTime


sealed class ReminderFormEvent {
	data class DateChanged(val localDate: LocalDate): ReminderFormEvent()
	data class TimeChanged(val localTime: LocalTime): ReminderFormEvent()
	data class RepeatIntervalChanged(val repeatInterval: RepeatInterval): ReminderFormEvent()
	data object Clear: ReminderFormEvent()
	data object Save: ReminderFormEvent()
}
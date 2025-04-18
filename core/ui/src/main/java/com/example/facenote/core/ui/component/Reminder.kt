package com.example.facenote.core.ui.component

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.facenote.core.model.RepeatInterval
import com.example.facenote.core.ui.R
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Reminder(
	selectedDate: LocalDate?,
	selectedTime: LocalTime?,
	repeatInterval: RepeatInterval,
	onDateSelected: (LocalDate?) -> Unit,
	onTimeSelected: (LocalTime?) -> Unit,
	onIntervalSelected: (RepeatInterval) -> Unit,
	onDismiss: () -> Unit
) {
	var intervalMenuExpanded by remember { mutableStateOf(false) }
	var showDatePicker by remember { mutableStateOf(false) }
	var showTimePicker by remember { mutableStateOf(false) }

	if(showDatePicker){
		ReminderDatePicker(onDismiss = { showDatePicker = false }, onDateSelected = onDateSelected)
	}

	if(showTimePicker){
		ReminderTimePicker(onDismiss = { showTimePicker = false },onTimeSelected = onTimeSelected)
	}

	Dialog(onDismissRequest = onDismiss) {
		Column (
			modifier = Modifier
				.clip(RoundedCornerShape(16.dp))
				.background(color = MaterialTheme.colorScheme.background)
				.padding(vertical = 16.dp)
				.fillMaxWidth(0.9f)
		){
			Text(
				text = "Add reminder",
				modifier = Modifier.padding(horizontal = 16.dp),
				style = MaterialTheme.typography.titleLarge
			)
			Spacer(Modifier.height(16.dp))
			Row (
				modifier = Modifier
					.clickable {
						showDatePicker = true
					}
					.padding(16.dp)
					.fillMaxWidth(),
				horizontalArrangement = Arrangement.SpaceBetween,
				verticalAlignment = Alignment.CenterVertically
			){
				Row {
					Icon(
						painter = painterResource(id = R.drawable.ic_calender_today_outlined), 
						contentDescription = "" 
					)
					Spacer(Modifier.width(8.dp))
					Text(text = "Date")
				}
				Row {
					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
						Text(
							text = selectedDate?.format(DateTimeFormatter.ofPattern("MM dd, yyyy")) ?:"Select Date",
							color = MaterialTheme.colorScheme.primary,
						)
					}else{
						Text(
							text = selectedDate?.toString() ?:"Select Date",
							color = MaterialTheme.colorScheme.primary,
						)
					}
					Icon(
						painter = painterResource(id = R.drawable.ic_arrow_drop_down ) ,
						contentDescription = "" ,
						tint = MaterialTheme.colorScheme.primary
					)
				}
			}
			Row (
				modifier = Modifier
					.clickable {
						showTimePicker = true
					}
					.padding(16.dp)
					.fillMaxWidth(),
				horizontalArrangement = Arrangement.SpaceBetween,
				verticalAlignment = Alignment.CenterVertically
			){
				Row {
					Icon(
						painter = painterResource(id = R.drawable.ic_access_time_outlined),
						contentDescription = ""
					)
					Spacer(Modifier.width(8.dp))
					Text(text = "Time")
				}
				Row {
					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
						Text(
							text = selectedTime?.format(DateTimeFormatter.ofPattern("hh:mm a")) ?:"Select Time",
							color = MaterialTheme.colorScheme.primary,
						)
					}else{
						Text(
							text = selectedTime?.toString() ?:"Select Time",
							color = MaterialTheme.colorScheme.primary,
						)
					}
					Icon(
						painter = painterResource(id = R.drawable.ic_arrow_drop_down) ,
						contentDescription = "" ,
						tint = MaterialTheme.colorScheme.primary
					)
				}
			}
			Row (
				modifier = Modifier
					.clickable {

					}
					.padding(horizontal = 16.dp)
					.fillMaxWidth(),
				horizontalArrangement = Arrangement.SpaceBetween,
				verticalAlignment = Alignment.CenterVertically
			){
				Row {
					Icon(
						painter = painterResource(id = R.drawable.ic_repeat),
						contentDescription = ""
					)
					Spacer(Modifier.width(8.dp))
					Text(
						text = "Repeat"
					)
				}
				Row {
					ExposedDropdownMenuBox(
						expanded = intervalMenuExpanded,
						onExpandedChange = {
							intervalMenuExpanded = it
						}
					) {
						TextField(
							value = repeatInterval.name,
							onValueChange = {},
							modifier = Modifier
								.menuAnchor(MenuAnchorType.PrimaryNotEditable),
							textStyle = MaterialTheme.typography.bodyMedium.copy(
								color = MaterialTheme.colorScheme.primary,
								textAlign = TextAlign.End
							),
							readOnly = true,
							trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = intervalMenuExpanded) },
							colors = TextFieldDefaults.colors(
								unfocusedContainerColor =  Color.Transparent,
								focusedContainerColor =  Color.Transparent,
								focusedIndicatorColor = Color.Transparent,
								unfocusedIndicatorColor = Color.Transparent
							)
						)
						ExposedDropdownMenu(
							expanded = intervalMenuExpanded,
							onDismissRequest = {
								intervalMenuExpanded = false
							},
						) {
							RepeatInterval.entries.forEach { interval ->
								DropdownMenuItem(
									text = {
										Text(
											text = when(interval){
												RepeatInterval.NONE -> "None"
												RepeatInterval.DAILY -> "Daily"
												RepeatInterval.WEEKLY -> "Weekly"
												RepeatInterval.MONTHLY -> "Monthly"
												RepeatInterval.YEARLY -> "Yearly"
											}
										)
									},
									onClick = { onIntervalSelected(interval); intervalMenuExpanded = false }
								)
							}
						}
					}
				}
			}
		}
	}
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ReminderDatePicker(
	onDismiss: () -> Unit,
	onDateSelected: (LocalDate?) -> Unit
){
	val datePickerState = rememberDatePickerState()
	val confirmEnabled = remember { derivedStateOf { datePickerState.selectedDateMillis != null } }

	AlertDialog(
		onDismissRequest = onDismiss,
		confirmButton = {
			TextButton(
				onClick = {
					onDateSelected(datePickerState.selectedDateMillis?.let {
						if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
							Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate()
						} else {
							null
						}
					})
					onDismiss()
				},
				enabled = confirmEnabled.value
			) {
				Text(text = "Ok")
			}
		},
		dismissButton = {
			TextButton(onClick = { onDismiss() }) { Text("Cancel") }
		},
		title = {
			Text(
				text = "Select date",
				style = MaterialTheme.typography.bodySmall
			)
		},
		text = {
			DatePicker(state = datePickerState)
		}
	)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ReminderTimePicker(
	onDismiss: () -> Unit,
	onTimeSelected: (LocalTime?) -> Unit
){
	val timePickerState = rememberTimePickerState()

	AlertDialog(
		onDismissRequest = onDismiss,
		confirmButton = {
			TextButton(
				onClick = {
					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
						onTimeSelected(LocalTime.of(timePickerState.hour,timePickerState.minute))
					}
					onDismiss()
				},
			) {
				Text(text = "Ok")
			}
		},
		dismissButton = {
			TextButton(onClick = onDismiss) { Text("Cancel") }
		},
		title = {
			Text(
				text = "Select time",
				style = MaterialTheme.typography.bodySmall
			)
		},
		text = {
			TimePicker(state = timePickerState)
		}
	)
}
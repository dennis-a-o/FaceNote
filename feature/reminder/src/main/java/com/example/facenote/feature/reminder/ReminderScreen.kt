package com.example.facenote.feature.reminder

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.facenote.core.model.RepeatInterval
import com.example.facenote.core.ui.R
import org.threeten.bp.Instant
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalTime
import org.threeten.bp.ZoneId
import org.threeten.bp.format.DateTimeFormatter

@Composable
fun ReminderScreen(
	viewModel: ReminderViewModel,
	onNavigateBack: () -> Unit
) {
	val reminderState by viewModel.reminderState.collectAsState()

	Scaffold (
		topBar = {
			ReminderTopBar(
				onClickBack = onNavigateBack,
				onClickDelete = { viewModel.onEvent(ReminderFormEvent.Clear) },
				reminderState = reminderState
			)
		},
		bottomBar = {
			ReminderBottomBar(
				onCancel = { onNavigateBack() },
				onEvent = { viewModel.onEvent(it)}
			)
		}
	){ paddingValues ->
		Box(modifier = Modifier
			.fillMaxSize()
			.padding(paddingValues)
		){
			ReminderForm(
				onEvent = { viewModel.onEvent(it) },
				reminderState = reminderState
			)
		}
	}
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ReminderForm(
	onEvent: (ReminderFormEvent) -> Unit,
	reminderState: ReminderState,
){
	var intervalMenuExpanded by rememberSaveable { mutableStateOf(false) }
	var showDatePicker by rememberSaveable { mutableStateOf(false) }
	var showTimePicker by rememberSaveable { mutableStateOf(false) }

	if(showDatePicker){
		ReminderDatePicker(
			onDismiss = {
				showDatePicker = false
			},
			onDateSelected = {
				onEvent(ReminderFormEvent.DateChanged(it))
			}
		)
	}

	if(showTimePicker){
		ReminderTimePicker(
			localTime = reminderState.selectedTime,
			onDismiss = {
				showTimePicker = false
			},
			onTimeSelected = {
				onEvent(ReminderFormEvent.TimeChanged(it))
			}
		)
	}

	Column (
		modifier = Modifier
			.padding(8.dp)
			.fillMaxWidth()
			.shadow(elevation = 1.dp, shape = RoundedCornerShape(16.dp))
			.background(MaterialTheme.colorScheme.surfaceContainerLowest, RoundedCornerShape(16.dp))
	){
		Row (
			modifier = Modifier
				.clickable {
					showDatePicker = true
				}
				.padding(16.dp)
				.fillMaxWidth()
				.testTag("datePicker"),
			horizontalArrangement = Arrangement.SpaceBetween,
			verticalAlignment = Alignment.CenterVertically
		){
			Row {
				Icon(
					painter = painterResource(id = R.drawable.ic_calender_today_outlined),
					tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
					contentDescription = ""
				)
				Spacer(Modifier.width(8.dp))
				Text(stringResource(R.string.date))
			}
			Row (verticalAlignment = Alignment.CenterVertically){
				Column {
					Text(
						text = reminderState.selectedDate.format(DateTimeFormatter.ofPattern("MMM dd, yyyy")),
						style = MaterialTheme.typography.bodyMedium.copy(
							color = MaterialTheme.colorScheme.primary,
							fontWeight = FontWeight.Bold
						)
					)
					if (reminderState.selectedDateError != null){
						Text(
							text = reminderState.selectedDateError,
							style = MaterialTheme.typography.bodySmall.copy(
								color = MaterialTheme.colorScheme.error,
								fontWeight = FontWeight.Bold
							)
						)
					}
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
				.fillMaxWidth()
				.testTag("timePicker"),
			horizontalArrangement = Arrangement.SpaceBetween,
			verticalAlignment = Alignment.CenterVertically
		){
			Row {
				Icon(
					painter = painterResource(id = R.drawable.ic_access_time_outlined),
					tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
					contentDescription = ""
				)
				Spacer(Modifier.width(8.dp))
				Text(stringResource(R.string.reminder_time))
			}
			Row (verticalAlignment = Alignment.CenterVertically){
				Column {

					Text(
						text = reminderState.selectedTime.format(DateTimeFormatter.ofPattern("hh:mm a")),
						style = MaterialTheme.typography.bodyMedium.copy(
							color = MaterialTheme.colorScheme.primary,
							fontWeight = FontWeight.Bold
						)
					)
					if (reminderState.selectedTimeError != null){
						Text(
							text = reminderState.selectedTimeError,
							style = MaterialTheme.typography.bodySmall.copy(
								color = MaterialTheme.colorScheme.error,
								fontWeight = FontWeight.Bold
							)
						)
					}
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

				}
				.padding(horizontal = 16.dp)
				.fillMaxWidth(),
			horizontalArrangement = Arrangement.SpaceBetween,
			verticalAlignment = Alignment.CenterVertically
		){
			Row {
				Icon(
					painter = painterResource(id = R.drawable.ic_repeat),
					tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
					contentDescription = ""
				)
				Spacer(Modifier.width(8.dp))
				Text(stringResource(R.string.repeat))
			}
			Row {
				ExposedDropdownMenuBox(
					expanded = intervalMenuExpanded,
					onExpandedChange = {
						intervalMenuExpanded = it
					}
				) {
					TextField(
						value = reminderState.repeatInterval.name,
						onValueChange = {},
						modifier = Modifier
							.testTag("repeatInterval")
							.menuAnchor(MenuAnchorType.PrimaryNotEditable),
						textStyle = MaterialTheme.typography.bodyMedium.copy(
							color = MaterialTheme.colorScheme.primary,
							textAlign = TextAlign.End,
							fontWeight = FontWeight.Bold
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
										},
									)
								},
								onClick = {
									onEvent(ReminderFormEvent.RepeatIntervalChanged(interval))
									intervalMenuExpanded = false
								}
							)
						}
					}
				}
			}
		}
	}
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ReminderTopBar(
	onClickBack: () -> Unit,
	onClickDelete: () -> Unit,
	reminderState: ReminderState
){
	var showClearDialog by rememberSaveable { mutableStateOf(false) }

	if (showClearDialog){
		AlertDialog(
			onDismissRequest = { showClearDialog = false},
			confirmButton = {
				TextButton(onClick = { onClickDelete(); showClearDialog = false}) {
					Text(stringResource(R.string.delete))
				}
			},
			dismissButton = {
				TextButton(onClick = { showClearDialog = false }) {
					Text(stringResource(R.string.cancel))
				}
			},
			text = {
				Text(stringResource(R.string.clear_this_reminder))
			}
		)
	}
	TopAppBar(
		title = { Text(stringResource(R.string.reminder)) },
		navigationIcon = {
			IconButton(onClick = onClickBack) {
				Icon(
					painter = painterResource(R.drawable.ic_arrow_back),
					contentDescription = null
				)
			}
		},
		actions = {
			if(reminderState.isSaved) {
				IconButton(onClick = { showClearDialog = true }) {
					Icon(
						painter = painterResource(R.drawable.ic_delete_outline),
						contentDescription = stringResource(R.string.delete)
					)
				}
			}
		}
	)
}

@Composable
private fun ReminderBottomBar(
	onCancel: () -> Unit,
	onEvent: (ReminderFormEvent) -> Unit
){
	BottomAppBar (
		containerColor = MaterialTheme.colorScheme.background
	){
		Spacer(Modifier.width(8.dp))
		OutlinedButton(
			onClick = onCancel,
			modifier = Modifier.weight(1f),
			shape = RoundedCornerShape(16.dp)
		) {
			Text(stringResource(R.string.cancel))
		}
		Spacer(Modifier.width(8.dp))
		Button(
			onClick = { onEvent(ReminderFormEvent.Save) },
			modifier = Modifier.weight(1f),
			shape = RoundedCornerShape(16.dp)
		) {
			Text(stringResource(R.string.save))
		}
		Spacer(Modifier.width(8.dp))
	}
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ReminderDatePicker(
	onDismiss: () -> Unit,
	onDateSelected: (LocalDate) -> Unit
){
	val datePickerState = rememberDatePickerState()
	val confirmEnabled = remember { derivedStateOf { datePickerState.selectedDateMillis != null } }

	DatePickerDialog(
		onDismissRequest = onDismiss,
		confirmButton = {
			TextButton(
				onClick = {
					onDateSelected(
						Instant
							.ofEpochMilli(datePickerState.selectedDateMillis?:0)
							.atZone(ZoneId.systemDefault())
							.toLocalDate()
					)
					onDismiss()
				},
				enabled = confirmEnabled.value
			) {
				Text(stringResource(R.string.ok))
			}
		},
		modifier = Modifier.testTag("reminderDatePickerDialog"),
		dismissButton = {
			TextButton(onClick = { onDismiss() }) { Text(stringResource(R.string.cancel)) }
		}
	){
		DatePicker(state = datePickerState)
	}
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ReminderTimePicker(
	localTime: LocalTime,
	onDismiss: () -> Unit,
	onTimeSelected: (LocalTime) -> Unit
){

	val timePickerState = rememberTimePickerState(
		initialHour = localTime.hour,
		initialMinute = localTime.minute
	)

	AlertDialog(
		onDismissRequest = onDismiss,
		confirmButton = {
			TextButton(
				onClick = {
					onTimeSelected(LocalTime.of(timePickerState.hour,timePickerState.minute))
					onDismiss()
				},
			) {
				Text(stringResource(R.string.ok))
			}
		},
		modifier = Modifier.testTag("reminderTimePickerDialog"),
		dismissButton = {
			TextButton(onClick = onDismiss) { Text(stringResource(R.string.cancel)) }
		},
		title = {
			Text(
				text = stringResource(R.string.select_time),
				style = MaterialTheme.typography.bodySmall
			)
		},
		text = {
			TimePicker(state = timePickerState)
		}
	)
}
package com.example.facenote.feature.backup

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.facenote.core.model.DriveFile
import com.example.facenote.core.ui.R
import com.example.facenote.core.ui.component.NoteError
import com.example.facenote.core.ui.component.NoteProgressIndicator
import com.example.facenote.core.ui.util.DateTimeUtil
import com.example.facenote.core.ui.util.FileUtil
import com.google.android.gms.auth.api.signin.GoogleSignIn
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BackupScreen(
	viewModel: BackupViewModel = hiltViewModel(),
	onNavigateBack: () -> Unit
) {
	val backupState by viewModel.backupState.collectAsState()

	val context = LocalContext.current
	val snackbarHostState = remember { SnackbarHostState() }
	val scope = rememberCoroutineScope()

	val launcherActivity =
		rememberLauncherForActivityResult(contract = ActivityResultContracts.StartActivityForResult()) { result ->
			GoogleSignIn.getSignedInAccountFromIntent(result.data)
				.addOnSuccessListener{ account ->
					viewModel.updateAccountInfo(account)
				}
				.addOnFailureListener { error ->
					error.message?.let {
						scope.launch {
							snackbarHostState.showSnackbar(message = it)
						}
					}
				}
		}

	LaunchedEffect(backupState.error) {
		backupState.error?.let {
			snackbarHostState.showSnackbar(message = it)
			viewModel.resetActionState()
		}
	}

	LaunchedEffect(backupState.success) {
		backupState.success?.let {
			Toast.makeText(context,  it, Toast.LENGTH_LONG ).show()
			viewModel.resetActionState()
		}
	}

	Scaffold(
		topBar = {
			TopAppBar(
				title = { Text(text = stringResource(R.string.note_backup)) },
				navigationIcon = {
					IconButton(onClick = {
						if (backupState.showBackupFiles){
							viewModel.showBackupFiles(false)
						}else {
							onNavigateBack()
						}
					}) {
						Icon(painter = painterResource(R.drawable.ic_arrow_back), contentDescription = null)
					}
				}
			)
		},
		snackbarHost = { SnackbarHost(snackbarHostState) }
	) { paddingValues ->
		Box(
			modifier = Modifier
				.padding(paddingValues)
				.fillMaxSize(),
		){
			if (backupState.showBackupFiles) {
				RestoreView(
					backupState = backupState,
					onClickRetry = { viewModel.getFiles() },
					onClickCancel = { viewModel.cancel() },
					onClickRestore = { viewModel.restore(it) },
					onClickDelete = { viewModel.delete(it) }
				)
			}else{
				BackupView(
					backupState = backupState,
					onClickBackup = { viewModel.backup() },
					onShowRestoreView = { viewModel.showBackupFiles(true)},
					onClickSignOut = { viewModel.logout() },
					onClickSignIn = {
						launcherActivity.launch(viewModel.getSignInIntent())
					},
					onClickCancel = { viewModel.cancel() }
				)
			}
		}
	}
}

@Composable
internal fun BackupView(
	backupState: BackupState,
	onClickBackup: () -> Unit,
	onShowRestoreView: () -> Unit,
	onClickSignOut:() -> Unit,
	onClickSignIn: () -> Unit,
	onClickCancel: () -> Unit
){
	Column (
		modifier = Modifier
			.fillMaxSize()
			.padding(16.dp)
			.verticalScroll(rememberScrollState())

	){
		Text(
			text = stringResource(R.string.backup_description),
			style = MaterialTheme.typography.bodyMedium.copy(
				color = MaterialTheme.colorScheme.outline
			)
		)
		if (backupState.isSignedIn) {
			Spacer(Modifier.height(16.dp))
			Column(
				modifier = Modifier.fillMaxWidth(),
				horizontalAlignment = Alignment.CenterHorizontally
			) {
				Column(
					modifier = Modifier
						.clip(RoundedCornerShape(16.dp))
						.background(MaterialTheme.colorScheme.surfaceContainerLow)
						.padding(16.dp),
					horizontalAlignment = Alignment.CenterHorizontally
				) {
					Text(
						text = backupState.account?.displayName.toString(),
						style = MaterialTheme.typography.bodyMedium,
						fontWeight = FontWeight.W500
					)
					Text(
						text = backupState.account?.email.toString(),
						style = MaterialTheme.typography.bodyMedium
					)
				}
				Spacer(Modifier.height(8.dp))
				Button(
					onClick = onClickSignOut
				) {
					Text(stringResource(R.string.google_signout))
				}
			}
		}
		Spacer(Modifier.height(16.dp))
		Text(
			text = stringResource(R.string.backup_data_summary),
			style = MaterialTheme.typography.titleMedium
		)
		Spacer(Modifier.height(8.dp))
		Text(
			text = stringResource(R.string.backup_summary_note),
			style = MaterialTheme.typography.bodySmall.copy(
				color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
			)
		)
		Spacer(Modifier.height(16.dp))
		backupState.lastBackup?.let {
			Text(
				text = "${stringResource(R.string.last_backup)} : ${DateTimeUtil.millisToTextFormat(it)}",
				style = MaterialTheme.typography.bodyMedium
			)
		}
		if (backupState.inProgress) {
			Spacer(Modifier.height(16.dp))
			Column {
				Text(
					text = backupState.operation,
					style = MaterialTheme.typography.bodyMedium.copy(
						color = MaterialTheme.colorScheme.primary
					)
				)
				Spacer(Modifier.height(8.dp))
				Row(
					modifier = Modifier,
					verticalAlignment = Alignment.CenterVertically
				) {
					LinearProgressIndicator(
						progress = { backupState.progress.toFloat() },
						modifier = Modifier.weight(1f)
					)
					IconButton(
						onClick = onClickCancel,
						modifier = Modifier
					) {
						Icon(painterResource(R.drawable.ic_close), null)
					}
				}
			}
		}
		Spacer(Modifier.height(16.dp))
		if (backupState.isSignedIn) {
			if (!backupState.inProgress) {
				Row(
					modifier = Modifier.fillMaxWidth(),
					horizontalArrangement = Arrangement.SpaceBetween
				) {
					Button(
						onClick = onClickBackup
					) {
						Text(stringResource(R.string.backup))
					}
					OutlinedButton(
						onClick = onShowRestoreView
					) {
						Text(stringResource(R.string.restore))
					}
				}
			}
		}else{
			Button(
				onClick = onClickSignIn
			) {
				Text(stringResource(R.string.google_signin))
			}
		}
	}
}

@Composable
internal fun RestoreView(
	backupState: BackupState,
	onClickRetry: () -> Unit,
	onClickDelete: (String) -> Unit,
	onClickRestore: (String) -> Unit,
	onClickCancel: () -> Unit
){
	Column (
		modifier = Modifier
			.fillMaxSize()
			.fillMaxHeight()
			.verticalScroll(rememberScrollState())
			.padding(horizontal = 16.dp),
		verticalArrangement = Arrangement.Center
	){
		backupState.files?.let {
			Text(
				text = stringResource(R.string.backup_files),
				style = MaterialTheme.typography.titleMedium
			)
		}
		if (backupState.inProgress) {
			Spacer(Modifier.height(16.dp))
			Column {
				Text(
					text = backupState.operation,
					style = MaterialTheme.typography.bodyMedium.copy(
						color = MaterialTheme.colorScheme.primary
					)
				)
				Spacer(Modifier.height(8.dp))
				Row(
					modifier = Modifier,
					verticalAlignment = Alignment.CenterVertically
				) {
					LinearProgressIndicator(
						progress = { backupState.progress.toFloat() },
						modifier = Modifier.weight(1f)
					)
					IconButton(
						onClick = onClickCancel,
						modifier = Modifier
					) {
						Icon(painterResource(R.drawable.ic_close), null)
					}
				}
			}
		}
		Spacer(Modifier.height(16.dp))
		if (backupState.filesLoading){
			NoteProgressIndicator()
		}else {
			backupState.files?.forEach {
				BackupItem(
					driveFile = it,
					onClickRestore,
					onClickDelete
				)
				Spacer(Modifier.height(16.dp))
			}
			if (backupState.files == null && backupState.filesError != null){
				NoteError(
					title = stringResource(R.string.error),
					message = backupState.filesError,
					onAction =  onClickRetry
				)
			}else{
				if(backupState.files == null) {
					Column(
						modifier = Modifier.fillMaxWidth(),
						verticalArrangement = Arrangement.Center,
						horizontalAlignment = Alignment.CenterHorizontally
					) {
						Icon(
							painter = painterResource(R.drawable.ic_backup_outlined),
							contentDescription = null,
							modifier = Modifier
								.size(100.dp),
							tint = MaterialTheme.colorScheme.primary
						)
						Spacer(Modifier.height(8.dp))
						Text(stringResource(R.string.no_backup))
					}
				}
			}
		}
	}
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BackupItem(
	driveFile: DriveFile,
	onClickRestore: (String) -> Unit,
	onClickDelete: (String) -> Unit
){
	var expanded by remember { mutableStateOf(false) }
	var confirmDelete by remember {  mutableStateOf(false) }

	if (confirmDelete){
		AlertDialog(
			onDismissRequest = { confirmDelete = false },
			confirmButton = {
				TextButton(onClick = { onClickDelete(driveFile.id); confirmDelete = false }) {
					Text(stringResource(R.string.delete))
				}
			},
			dismissButton = {
				TextButton(onClick = { confirmDelete = false }) {
					Text(stringResource(R.string.cancel))
				}
			},
			title = {
				Text(stringResource(R.string.delete_this_backup))
			},
			text = {
				Text(stringResource(R.string.this_permanent_cannot_undone))
			}
		)
	}

	Row (
		modifier = Modifier
			.fillMaxWidth()
			.clip(RoundedCornerShape(16.dp))
			.border(0.1.dp, Color.Black.copy(alpha = 0.2f), RoundedCornerShape(16.dp))
			.padding(vertical = 16.dp, horizontal = 8.dp),
	){
		Icon(
			painter = painterResource(R.drawable.ic_folder_zip_outlined),
			contentDescription = null,
			modifier = Modifier.size(54.dp),
			tint = MaterialTheme.colorScheme.primary
		)
		Spacer(Modifier.width(8.dp))
		Column (
			modifier = Modifier.weight(1f)
		){
			Text(
				text = driveFile.name,
				style = MaterialTheme.typography.bodyLarge
			)
			Text(
				text = FileUtil.bytesToStringFormat(driveFile.size),
				style = MaterialTheme.typography.bodyMedium
			)
			Text(
				text = DateTimeUtil.millisToTextFormat(driveFile.createdTime),
				style = MaterialTheme.typography.bodyMedium.copy(
					color = MaterialTheme.colorScheme.outline
				)
			)
		}
		Spacer(Modifier.width(8.dp))
		Column {
			DropdownMenu(
				expanded = expanded,
				onDismissRequest = { expanded = false },
			) {
				DropdownMenuItem(
					text = { Text(stringResource(R.string.restore)) },
					onClick = { onClickRestore(driveFile.id);  expanded = false },
					leadingIcon = { Icon(painterResource(R.drawable.ic_restore), null) }
				)
				DropdownMenuItem(
					text = { Text(stringResource(R.string.delete)) },
					onClick = { confirmDelete = true;  expanded = false },
					leadingIcon = { Icon(painterResource(R.drawable.ic_delete_outline), null) }
				)
			}
		}
		IconButton(
			onClick = { expanded = true },
			modifier = Modifier.size(20.dp)
		) {
			Icon(painterResource(R.drawable.ic_more_vert),null)
		}
	}
}

@Preview(showSystemUi = true)
@Composable
private fun PreviewBackupItem(){
	BackupItem(
		DriveFile("ddze6447","backupfile_66677777.zip", 6, 5677899L),
		{},
		{}
	)
}


@Preview(showSystemUi = true)
@Composable
private fun PreviewBackupView(){
	BackupView(
		BackupState(),
		{},{},{},{},{}
	)
}

@Preview(showSystemUi = true)
@Composable
private fun PreviewRestoreView(){
	RestoreView(
		BackupState(),
		{},{},{},{}
	)
}


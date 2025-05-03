package com.example.facenote.feature.trash

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.facenote.core.ui.R
import com.example.facenote.core.ui.component.NoteError
import com.example.facenote.core.ui.component.NoteItem
import com.example.facenote.core.ui.component.NoteProgressIndicator
import com.example.facenote.core.ui.model.SelectState

@Composable
fun TrashScreen(
	viewModel: TrashViewModel = hiltViewModel(),
	onNavigateBack: () -> Unit,
	onNavigateToNoteEditor: (Long, Boolean) -> Unit,
) {
	val lazyPagingTrashNotes = viewModel.trashNotePager.collectAsLazyPagingItems()
	val selectState by viewModel.selectState.collectAsState()
	val isActionDone by viewModel.isActionDone.collectAsState()

	val isGrid by remember{ mutableStateOf(true) }

	LaunchedEffect(key1 = isActionDone) {
		if (isActionDone){
			lazyPagingTrashNotes.refresh()
			viewModel.resetActionDone()
		}
	}

	Scaffold (
		topBar = {
			if(selectState.isSelecting){
				TrashSelectTobBar(
					selectState = selectState,
					onCancel = { viewModel.onSelectClear() },
					onClickRestore = { viewModel.restore() },
					onClickDelete = { viewModel.delete() }
				)
			}else{
				TrashTobBar(
					itemCount = lazyPagingTrashNotes.itemCount,
					onClickBack = { onNavigateBack() },
					onClickEmptyTrash = { viewModel.emptyTrash() }
				)
			}
		}
	){ paddingValues ->
		Box(modifier = Modifier
			.padding(paddingValues)
			.fillMaxSize()){
			when(lazyPagingTrashNotes.loadState.refresh){
				is LoadState.Error -> NoteError()
				LoadState.Loading -> NoteProgressIndicator()
				else -> {
					if (lazyPagingTrashNotes.itemCount != 0) {
						LazyVerticalStaggeredGrid(
							columns = StaggeredGridCells.Fixed(count = if (isGrid) 2 else 1),
							contentPadding = PaddingValues(8.dp),
							horizontalArrangement = Arrangement.spacedBy(8.dp),
							verticalItemSpacing = 8.dp
						) {
							items(lazyPagingTrashNotes.itemCount) {
								lazyPagingTrashNotes[it]?.let { item ->
									NoteItem(
										note = item,
										onClick = { noteUi ->
											if (selectState.isSelecting) {
												viewModel.onSelect(noteUi)
											} else {
												onNavigateToNoteEditor(
													noteUi.id,
													noteUi.isChecklist
												)
											}
										},
										onLongClick = { noteUi ->
											viewModel.onSelect(noteUi)
										},
										isSelected = item in selectState.selected
									)
								}
							}
						}
					}else{
						NoTrash()
					}
				}
			}
		}
	}
}

@Composable
private fun NoTrash(){
	Column(
		modifier = Modifier.fillMaxSize(),
		verticalArrangement = Arrangement.Center,
		horizontalAlignment = Alignment.CenterHorizontally
	){
		Icon(
			painter = painterResource(R.drawable.ic_delete_forever_outlined),
			contentDescription = "",
			modifier = Modifier
				.size(100.dp),
			tint = MaterialTheme.colorScheme.primary
		)
		Spacer(Modifier.height(8.dp))
		Text(text = "Empty trash")
	}
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrashSelectTobBar(
	selectState: SelectState,
	onCancel: () -> Unit,
	onClickRestore: () -> Unit,
	onClickDelete:() -> Unit
){
	var showDeleteDialog by remember { mutableStateOf(false) }


	if (showDeleteDialog){
		AlertDialog(
			onDismissRequest = { showDeleteDialog= false },
			confirmButton = {
				TextButton(onClick = { onClickDelete(); showDeleteDialog = false }) {
					Text(text = "Delete")
				}
			},
			dismissButton = {
				TextButton(onClick = { onCancel(); showDeleteDialog = false }) {
					Text(text = "Cancel")
				}
			},
			text = {
				Text(text = "Delete permanent selected notes?")
			}
		)
	}
	TopAppBar(
		title = {},
		navigationIcon = {
			Row (verticalAlignment = Alignment.CenterVertically){
				IconButton(onClick = onCancel) {
					Icon(
						painter = painterResource(R.drawable.ic_close),
						contentDescription = null
					)
				}
				Spacer(Modifier.width(8.dp))
				Text(text = "${selectState.selected.size}")
			}
		},
		actions = {
			IconButton(onClick = onClickRestore) {
				Icon(
					painter = painterResource(R.drawable.ic_restore),
					contentDescription = null
				)
			}
			IconButton(onClick = { showDeleteDialog = true }) {
				Icon(
					painter = painterResource(R.drawable.ic_delete_outline),
					contentDescription = null
				)
			}
		}
	)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TrashTobBar(
	itemCount: Int,
	onClickBack: () -> Unit,
	onClickEmptyTrash: () -> Unit,
){
	var expanded by remember { mutableStateOf(false) }
	var showTrashDialog by remember { mutableStateOf(false) }

	if (showTrashDialog){
		AlertDialog(
			onDismissRequest = { showTrashDialog = false },
			confirmButton = {
				TextButton(onClick = { onClickEmptyTrash(); showTrashDialog = false }) {
					Text(text = "Empty")
				}
			},
			dismissButton = {
				TextButton(onClick = { showTrashDialog = false }) {
					Text(text = "Cancel")
				}
			},
			title = {
				Text(text = "Empty trash?")
			},
			text = {
				Text(text = "All notes in trash will be permanently deleted.")
			}
		)
	}

	TopAppBar(
		title = { Text(text = "Trash") },
		navigationIcon = {
			IconButton(onClick = onClickBack) {
				Icon(painter = painterResource(R.drawable.ic_arrow_back), contentDescription = null)
			}
		},
		actions = {
			if (itemCount > 0) {
				IconButton(onClick = { expanded = true }) {
					Icon(
						painter = painterResource(R.drawable.ic_more_vert),
						contentDescription = ""
					)
				}
				DropdownMenu(
					expanded = expanded,
					onDismissRequest = { expanded = false }
				) {
					DropdownMenuItem(
						text = { Text(text = "Empty trash") },
						onClick = { showTrashDialog = true; expanded = false }
					)
				}
			}
		}
	)
}
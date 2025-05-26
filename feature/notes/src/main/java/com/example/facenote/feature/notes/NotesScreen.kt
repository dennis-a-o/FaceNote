package com.example.facenote.feature.notes

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.facenote.core.ui.R
import com.example.facenote.core.ui.component.NoteError
import com.example.facenote.core.ui.component.NoteItem
import com.example.facenote.core.ui.component.NoteProgressIndicator
import com.example.facenote.core.ui.component.NoteSelectTopBar
import kotlinx.coroutines.launch

@Composable
fun NotesScreen (
	onNavigateToNoteEditor: (Long,Boolean) -> Unit,
	onNavigateToNoteSearch:(String?)  -> Unit,
	onNavigateToArchive: () -> Unit,
	onNavigateToTrash: () -> Unit,
	onNavigateToSetting: () -> Unit,
	onNavigateToBackUp: () -> Unit,
	viewModel: NotesViewModel = hiltViewModel()
){
	val  lazyPagingNotes = viewModel.notesPaging.collectAsLazyPagingItems()
	val selectState by viewModel.selectState.collectAsState()

	var isGrid by remember{ mutableStateOf(true) }

	val drawerState = rememberDrawerState(DrawerValue.Closed)
	val scope = rememberCoroutineScope()

	BackHandler(selectState.isSelecting) {
		if (selectState.isSelecting) viewModel.onSelectClear()
	}

	ModalNavigationDrawer(
		drawerContent = {
			DrawerContent(
				onNavigateToArchive =  onNavigateToArchive,
				onNavigateToTrash = onNavigateToTrash,
				onNavigateToSetting = onNavigateToSetting,
				onNavigateToBackUp = onNavigateToBackUp,
				closeDrawer = { scope.launch {  drawerState.close() } }
			)
		},
		drawerState = drawerState,
	) {
		Scaffold (
			topBar = {
				if(selectState.isSelecting){
					NoteSelectTopBar(
						selectState = selectState,
						onCancel = { viewModel.onSelectClear() },
						onClickPin = {
							viewModel.pin()
							lazyPagingNotes.refresh()
						},
						onClickArchive = {
							viewModel.archive()
							lazyPagingNotes.refresh()
						},
						onClickDelete = {
							viewModel.trash()
							lazyPagingNotes.refresh()
						}
					)
				}else {
					NotesTopBar(
						onClickShowDrawer = { scope.launch { drawerState.open() } },
						onToggleGridList = { isGrid = !isGrid },
						onClickSearch = { onNavigateToNoteSearch(null) },
						isGrid = isGrid
					)
				}
			},
			floatingActionButton = {
				NotesFloatingActionButton(
					onClickAdd = { isChecklist ->
						onNavigateToNoteEditor(0, isChecklist)
					}
				)
			}
		){ paddingValues ->
			Box(
				modifier = Modifier
					.padding(paddingValues)
					.fillMaxSize(),
			){
				when(lazyPagingNotes.loadState.refresh){
					is LoadState.Error -> {
						NoteError()
					}
					LoadState.Loading -> {
						NoteProgressIndicator()
					}
					else -> {
						LazyVerticalStaggeredGrid(
							columns = StaggeredGridCells.Fixed(count = if (isGrid) 2 else 1),
							contentPadding = PaddingValues(8.dp),
							horizontalArrangement = Arrangement.spacedBy(8.dp),
							verticalItemSpacing = 8.dp
						) {
							items(lazyPagingNotes.itemCount){
								lazyPagingNotes[it]?.let { item ->
									NoteItem(
										note = item,
										onClick = { noteUi ->
											if (selectState.isSelecting) {
												viewModel.onSelect(noteUi)
											}else {
												onNavigateToNoteEditor(noteUi.id, noteUi.isChecklist)
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
					}
				}
			}
		}
	}
}

@Composable
private fun DrawerContent(
	onNavigateToArchive: () -> Unit,
	onNavigateToTrash: () -> Unit,
	onNavigateToSetting: () -> Unit,
	onNavigateToBackUp: () -> Unit,
	closeDrawer: () -> Unit
){
	ModalDrawerSheet (
		modifier = Modifier.fillMaxWidth(0.8f),
	){
		DrawerLogo(
			modifier = Modifier
				.padding(horizontal = 28.dp, vertical = 24.dp)
		)
		NavigationDrawerItem(
			label = { Text(stringResource(R.string.notes)) },
			icon = {
				Icon(
					painter = painterResource(R.drawable.ic_notes),
					contentDescription = ""
				)
			},
			selected = true,
			onClick = { closeDrawer() },
			modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
		)
		NavigationDrawerItem(
			label = { Text(stringResource(R.string.archive)) },
			icon = {
				Icon(
					painter = painterResource(R.drawable.ic_archive_outline),
					contentDescription = ""
				)
			},
			selected = false,
			onClick = { onNavigateToArchive(); closeDrawer() },
			modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
		)
		NavigationDrawerItem(
			label = { Text(stringResource(R.string.trash)) },
			icon = {
				Icon(
					painter = painterResource(R.drawable.ic_delete_outline),
					contentDescription = ""
				)
			},
			selected = false,
			onClick = { onNavigateToTrash(); closeDrawer() },
			modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
		)
		NavigationDrawerItem(
			label = { Text(stringResource(R.string.setting)) },
			icon = {
				Icon(
					painter = painterResource(R.drawable.ic_setting_outlined),
					contentDescription = ""
				)
			},
			selected = false,
			onClick = { onNavigateToSetting(); closeDrawer() },
			modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
		)
		NavigationDrawerItem(
			label = { Text(stringResource(R.string.backup)) },
			icon = {
				Icon(
					painter = painterResource(R.drawable.ic_backup_outlined),
					contentDescription = ""
				)
			},
			selected = false,
			onClick = { onNavigateToBackUp(); closeDrawer() },
			modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
		)
	}
}

@Composable
private fun DrawerLogo(modifier: Modifier = Modifier){
	Row (
		modifier = modifier,
		verticalAlignment = Alignment.CenterVertically
	){
		Icon(
			painter = painterResource(R.drawable.ic_note_alt_outlined),
			contentDescription = "",
			modifier = Modifier.size(36.dp),
			tint = MaterialTheme.colorScheme.primary
		)
		Spacer(Modifier.width(8.dp))
		Text(
			text = stringResource(R.string.app_name),
			style = MaterialTheme.typography.titleLarge.copy(
				color = MaterialTheme.colorScheme.primary,
				fontWeight = FontWeight.Bold
			)
		)
	}
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NotesTopBar(
	onClickShowDrawer: () -> Unit,
	onToggleGridList: () -> Unit,
	onClickSearch: () -> Unit,
	isGrid: Boolean,
){
	TopAppBar(
		title = { },
		modifier = Modifier.shadow(1.dp),
		navigationIcon = {
			IconButton(onClick = onClickShowDrawer) {
				Icon(
					painter = painterResource(R.drawable.ic_menu),
					contentDescription = null
				)
			}
		},
		actions = {
			IconButton(onClick = onClickSearch) {
				Icon(
					painter = painterResource(R.drawable.ic_search),
					contentDescription = null
				)
			}
			if (isGrid) {
				IconButton(onClick = onToggleGridList) {
					Icon(
						painter = painterResource(R.drawable.ic_view_list_outlined),
						contentDescription = null
					)
				}
			}else {
				IconButton(onClick = onToggleGridList) {
					Icon(
						painter = painterResource(R.drawable.ic_grid_view_outlined),
						contentDescription = null
					)
				}
			}
		}
	)
}

@Composable
private fun NotesFloatingActionButton(
	onClickAdd: (Boolean) -> Unit
){
	var expanded by remember { mutableStateOf(false) }

	FloatingActionButton(
		onClick = { expanded = true },
		containerColor = MaterialTheme.colorScheme.primary,
		contentColor = MaterialTheme.colorScheme.surfaceContainerLowest
	){
		Icon(
			painter = painterResource(id = if(expanded) R.drawable.ic_close else R.drawable.ic_add),
			contentDescription = ""
		)
		DropdownMenu(
			expanded = expanded,
			onDismissRequest = { expanded = false },
			containerColor = Color.Transparent,
			shadowElevation = 0.dp
		) {
			TextButton(
				onClick = { onClickAdd(true); expanded = false },
				colors = ButtonDefaults.buttonColors(
					containerColor = MaterialTheme.colorScheme.surfaceBright,
					contentColor = MaterialTheme.colorScheme.onBackground
				)
			) {
				Text(stringResource(R.string.list))
				Spacer(Modifier.width(8.dp))
				Icon(painter = painterResource(R.drawable.ic_check_box), contentDescription = "")
			}
			TextButton(
				onClick = { onClickAdd(false); expanded = false },
				colors = ButtonDefaults.buttonColors(
					containerColor = MaterialTheme.colorScheme.surfaceBright,
					contentColor = MaterialTheme.colorScheme.onBackground
				)
			) {
				Text(stringResource(R.string.text))
				Spacer(Modifier.width(8.dp))
				Icon(painter = painterResource(R.drawable.ic_text_fields), contentDescription = "")
			}
		}
	}
}

@Preview(showSystemUi = true)
@Composable
private fun PreviewDrawerContent(){
	DrawerContent({},{},{},{},{})
}
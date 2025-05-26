package com.example.facenote.feature.archive

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.facenote.core.model.NoteState
import com.example.facenote.core.ui.component.NoteSelectTopBar
import com.example.facenote.core.ui.R
import com.example.facenote.core.ui.component.NoteError
import com.example.facenote.core.ui.component.NoteItem
import com.example.facenote.core.ui.component.NoteProgressIndicator


@Composable
fun ArchiveScreen(
	viewModel: ArchiveViewModel = hiltViewModel(),
	onNavigateBack: () -> Unit,
	onNavigateToNoteEditor: (Long, Boolean) -> Unit,
	onNavigateToNoteSearch: (String) -> Unit,
) {
	val  lazyPagingArchiveNotes = viewModel.archiveNotePager.collectAsLazyPagingItems()
	val selectState by viewModel.selectState.collectAsState()

	var isGrid by remember{ mutableStateOf(true) }

	Scaffold (
		topBar = {
			if(selectState.isSelecting){
				NoteSelectTopBar(
					selectState = selectState,
					onCancel = { viewModel.onSelectClear() },
					onClickPin = {
						viewModel.pin()
						lazyPagingArchiveNotes.refresh()
					},
					onClickArchive = {//we unarchive since we are in archive state
						viewModel.unArchive()
						lazyPagingArchiveNotes.refresh()
					},
					onClickDelete = {
						viewModel.trash()
						lazyPagingArchiveNotes.refresh()
					}
				)
			}else{
				ArchiveTobBar(
					isGrid = isGrid,
					onClickBack = { onNavigateBack() },
					onToggleGrid = { isGrid = !isGrid },
					onClickSearch = { onNavigateToNoteSearch(NoteState.ARCHIVE.getName()) }
				)
			}
		}
	){ paddingValues ->
		Box(modifier = Modifier
			.padding(paddingValues)
			.fillMaxSize()){
			when(lazyPagingArchiveNotes.loadState.refresh){
				is LoadState.Error -> NoteError()
				LoadState.Loading -> NoteProgressIndicator()
				else -> {
					if (lazyPagingArchiveNotes.itemCount != 0) {
						LazyVerticalStaggeredGrid(
							columns = StaggeredGridCells.Fixed(count = if (isGrid) 2 else 1),
							contentPadding = PaddingValues(8.dp),
							horizontalArrangement = Arrangement.spacedBy(8.dp),
							verticalItemSpacing = 8.dp
						) {
							items(lazyPagingArchiveNotes.itemCount) {
								lazyPagingArchiveNotes[it]?.let { item ->
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
						NoArchive()
					}
				}
			}
		}
	}
}

@Composable
private fun NoArchive(){
	Column(
		modifier = Modifier.fillMaxSize(),
		verticalArrangement = Arrangement.Center,
		horizontalAlignment = Alignment.CenterHorizontally
	){
		Icon(
			painter = painterResource(R.drawable.ic_archive_outline),
			contentDescription = null,
			modifier = Modifier
				.size(100.dp),
			tint = MaterialTheme.colorScheme.primary
		)
		Spacer(Modifier.height(8.dp))
		Text(text = stringResource(R.string.no_archive))
	}
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ArchiveTobBar(
	isGrid: Boolean,
	onClickBack: () -> Unit,
	onToggleGrid: () -> Unit,
	onClickSearch: () -> Unit
){
	TopAppBar(
		title = { Text(text = stringResource(R.string.archive)) },
		navigationIcon = {
			IconButton(onClick = onClickBack) {
				Icon(painter = painterResource(R.drawable.ic_arrow_back), contentDescription = null)
			}
		},
		actions = {
			IconButton(onClick = onClickSearch) {
				Icon(painter = painterResource(R.drawable.ic_search), contentDescription = null)
			}
			IconButton(onClick = onToggleGrid) {
				Icon(
					painter = if (isGrid){
						painterResource(R.drawable.ic_view_list_outlined)
					}else{
						painterResource(R.drawable.ic_grid_view_outlined)
					},
					contentDescription = null
				)
			}
		}
	)
}
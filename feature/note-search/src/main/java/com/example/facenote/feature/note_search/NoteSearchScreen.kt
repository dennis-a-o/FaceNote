package com.example.facenote.feature.note_search

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
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.facenote.core.ui.component.NoteError
import com.example.facenote.core.ui.component.NoteItem
import com.example.facenote.core.ui.component.NoteProgressIndicator
import com.example.facenote.core.ui.component.NoteSelectTopBar

@Composable
fun NoteSearchScreen(
	viewModel: NoteSearchViewModel,
	onNavigateBack: () -> Unit,
	onNavigateToNoteEditor: (Long, Boolean) -> Unit
) {
	val  lazyPagingNotes = viewModel.noteSearchPager.collectAsLazyPagingItems()
	val query by viewModel.query.collectAsState()
	val selectState by viewModel.selectState.collectAsState()

	var expanded by rememberSaveable { mutableStateOf(true) }
	val isGrid by remember{ mutableStateOf(true) }

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
				NoteSearchBar(
					query = query,
					onQueryChange = { viewModel.onQueryChange(it) },
					onSearch = {
						expanded = false
						lazyPagingNotes.refresh()
					},
					expanded = expanded,
					onExpandedChange = { expanded = it },
					onClickBack = onNavigateBack
				)
			}
		},

	){ paddingValues ->
		Box(
			modifier = Modifier
				.padding(paddingValues)
				.fillMaxSize(),
		){
			when(lazyPagingNotes.loadState.refresh){
				is LoadState.Error -> NoteError()
				LoadState.Loading -> NoteProgressIndicator()
				else -> {
					if (lazyPagingNotes.itemCount != 0) {
						LazyVerticalStaggeredGrid(
							columns = StaggeredGridCells.Fixed(count = if (isGrid) 2 else 1),
							contentPadding = PaddingValues(8.dp),
							horizontalArrangement = Arrangement.spacedBy(8.dp),
							verticalItemSpacing = 8.dp
						) {
							items(lazyPagingNotes.itemCount) {
								lazyPagingNotes[it]?.let { item ->
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
						NoSearchResult()
					}
				}
			}
		}
	}
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NoteSearchBar(
	query: String,
	onQueryChange: (String) -> Unit,
	onSearch: (String) -> Unit,
	expanded: Boolean,
	onExpandedChange: (Boolean) -> Unit,
	onClickBack: () -> Unit
){
	SearchBar(
		inputField = {
			SearchBarDefaults.InputField(
				query = query,
				onQueryChange = onQueryChange,
				onSearch = onSearch,
				expanded = expanded,
				onExpandedChange = onExpandedChange,
				placeholder = {
					Text(
						text = "Search",
						style = MaterialTheme.typography.bodyLarge.copy(
							color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
						)
					)
				},
				leadingIcon = {
					IconButton(onClick = onClickBack ) {
						Icon(
							painter = painterResource(com.example.facenote.core.ui.R.drawable.ic_arrow_back),
							contentDescription = ""
						)
					}
				},
				trailingIcon = {
					Row {
						if (query.isNotEmpty()) {
							IconButton(onClick = { onQueryChange("") }) {
								Icon(
									painter = painterResource(com.example.facenote.core.ui.R.drawable.ic_close),
									contentDescription = "clear search"
								)
							}
						}
						IconButton(onClick = {
							onSearch(query)
						}) {
							Icon(
								painterResource(com.example.facenote.core.ui.R.drawable.ic_search),
								contentDescription = "search"
							)
						}
					}
				}
			)
		},
		expanded = expanded,
		onExpandedChange = onExpandedChange,
		modifier = Modifier.shadow(elevation = 1.dp , shape = RectangleShape),
		colors = SearchBarDefaults.colors(
			containerColor = MaterialTheme.colorScheme.surface,
			dividerColor = MaterialTheme.colorScheme.surfaceContainer
		),

	) {
	}
}

@Composable
private fun NoSearchResult(){
	Column(
		modifier = Modifier.fillMaxSize(),
		verticalArrangement = Arrangement.Center,
		horizontalAlignment = Alignment.CenterHorizontally
	){
		Icon(
			painter = painterResource(com.example.facenote.core.ui.R.drawable.ic_search),
			contentDescription = "",
			modifier = Modifier
				.size(100.dp),
			tint = MaterialTheme.colorScheme.primary
		)
		Spacer(Modifier.height(8.dp))
		Text(text = "No match found")
	}
}
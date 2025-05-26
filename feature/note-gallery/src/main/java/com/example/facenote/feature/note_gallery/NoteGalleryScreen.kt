package com.example.facenote.feature.note_gallery

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.facenote.core.model.NoteImage
import com.example.facenote.core.model.NoteState
import com.example.facenote.core.ui.R
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteGalleryScreen(
	onNavigateBack:() -> Unit,
	viewModel: NoteGalleryViewModel
) {
	val noteImages by viewModel.noteGalleryState.collectAsState()
	val selectedImageIndex by viewModel.selectedImageIndex.collectAsState()
	val noteState = viewModel.noteState

	val context = LocalContext.current

	val pagerState =  rememberPagerState(
		initialPage = selectedImageIndex,
		initialPageOffsetFraction = 0f,
		pageCount = { noteImages.size }
	)

	var showDeleteConfirmDialog by remember{ mutableStateOf(false) }

	LaunchedEffect(pagerState.currentPage) {
		viewModel.onIndexChange(pagerState.currentPage)
	}

	Scaffold (
		topBar = {
			TopAppBar(
				title = {
					Text(text = "${selectedImageIndex + 1} of ${noteImages.size}")
				},
				modifier = Modifier.shadow(1.dp),
				navigationIcon = {
					IconButton(onClick = onNavigateBack) {
						Icon(
							painter = painterResource(R.drawable.ic_arrow_back),
							contentDescription = stringResource(R.string.back)
						)
					}
				},
				actions = {
					if (noteState != NoteState.TRASH) {
						IconButton(onClick = { viewModel.onSend(noteImages[pagerState.currentPage], context) }) {
							Icon(
								painter = painterResource(R.drawable.ic_send_outlined),
								contentDescription = stringResource(R.string.send)
							)
						}
						IconButton(onClick = { showDeleteConfirmDialog = true }) {
							Icon(
								painter = painterResource(R.drawable.ic_delete_outline),
								contentDescription = stringResource(R.string.delete)
							)
						}
					}
				}
			)
		}
	){ paddingValues ->
		Box (
			modifier = Modifier
				.padding(paddingValues)
		){
			NoteGalleryPager(images = noteImages, pagerState = pagerState )
		}
		if (showDeleteConfirmDialog){
			AlertDialog(
				onDismissRequest = { showDeleteConfirmDialog = false },
				confirmButton = {
					TextButton(
						onClick = {
							showDeleteConfirmDialog = false
							viewModel.onDelete(noteImages[pagerState.currentPage])
							if (noteImages.size == 1){
								onNavigateBack()
							}
						}
					) {
						Text(stringResource(R.string.delete))
					}
				},
				dismissButton = {
					TextButton(onClick = { showDeleteConfirmDialog = false }) {
						Text(stringResource(R.string.cancel))
					}
				},
				text = { Text(stringResource(R.string.delete_image)) }
			)
		}
	}
}

@Composable
private fun NoteGalleryPager(
	images: List<NoteImage>,
	pagerState: PagerState
){
	val context = LocalContext.current

	HorizontalPager(
		state = pagerState,
		modifier = Modifier.fillMaxSize(),
		pageSize = PageSize.Fill,
		pageSpacing = 8.dp
	) { index ->
		AsyncImage(
			model = File(context.dataDir, images[index].filePath),
			contentDescription = "",
			modifier = Modifier
				.fillMaxWidth(),
			contentScale = ContentScale.Fit
		)
	}
}
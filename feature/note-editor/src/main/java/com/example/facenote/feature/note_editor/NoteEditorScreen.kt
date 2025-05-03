package com.example.facenote.feature.note_editor

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedFilterChip
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import coil.compose.AsyncImage
import com.example.facenote.core.model.NoteImage
import com.example.facenote.core.model.NoteState
import com.example.facenote.core.ui.R
import com.example.facenote.core.ui.model.CheckListItem
import com.example.facenote.core.ui.util.AssetsUtil
import com.example.facenote.core.ui.util.DateTimeUtil
import com.example.facenote.feature.note_editor.sheet.AddBottomSheet
import com.example.facenote.feature.note_editor.sheet.BackgroundBottomSheet
import com.example.facenote.feature.note_editor.sheet.FormatBottomSheet
import com.example.facenote.feature.note_editor.sheet.TrashBottomSheet
import org.threeten.bp.Instant
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneId
import org.threeten.bp.format.DateTimeFormatter
import java.io.File

@Composable
fun NoteEditorScreen(
	onNavigateBack: () -> Unit,
	onNavigateToNoteGallery: (Long, Int, String) -> Unit,
	onNavigateToReminder: (Long,Long) -> Unit,
	viewModel: NoteEditorVIewModel
) {
	val noteState by viewModel.noteState.collectAsState()

	var showFormatBottomSheet by remember { mutableStateOf(false) }
	var showBackgroundBottomSheet by remember { mutableStateOf(false) }
	var showAddBottomSheet by remember { mutableStateOf(false) }
	var showTrashBottomSheet by remember { mutableStateOf(false) }
	val content = LocalContext.current

	val takePhoto = rememberLauncherForActivityResult(
		contract = ActivityResultContracts.TakePicturePreview()
	) { bitmap->
		bitmap?.let { viewModel.saveBitmapImage(it) }
	}
	val galleryLauncher = rememberLauncherForActivityResult(
		contract = ActivityResultContracts.GetContent()
	) { uri ->
		uri?.let { viewModel.saveUriImage(uri) }
	}



	Box{
		if (noteState.background.isNotEmpty()) {
			AssetsUtil.rememberBitmapFromAsset(noteState.background)?.asImageBitmap()
				?.let { bitmap ->
					Image(
						bitmap = bitmap,
						contentDescription = "",
						modifier = Modifier.fillMaxSize(),
						contentScale = ContentScale.FillBounds
					)
				}
		}
		Scaffold(
			modifier = Modifier
				.imePadding()
				.fillMaxSize(),
			topBar = {
				NoteEditorTopBar(
					noteState = noteState,
					onClickReminder = { onNavigateToReminder(noteState.id,noteState.remindAt ?: 0L) },
					onClickBack = { viewModel.saveNote(); onNavigateBack() },
					onClickTrash = { viewModel.onTrash() },
					onClickShare = {  viewModel.onShare(content) },
					onTogglePin = { viewModel.onPin() },
					onToggleArchive = { viewModel.onArchive() }
				)
			},
			bottomBar = {
				NoteEditorFooter(
					noteState = noteState,
					onClickAdd = {
						showAddBottomSheet = true
					},
					onClickFormating = {
						showFormatBottomSheet = true
					},
					onClickBackground = {
						showBackgroundBottomSheet = true
					},
					onClickSave = {
						viewModel.saveNote()
					},
					onClickShowTrash = {
						showTrashBottomSheet = true
					}
				)
			},
			containerColor = if(noteState.background.isNotEmpty()) {
				Color.Transparent
			}else {
				if (noteState.color.toArgb() != 0) noteState.color.copy(alpha = 0.5f)
				else MaterialTheme.colorScheme.background
			}
		) { paddingValues ->
			Box (
				modifier = Modifier
					.padding(paddingValues)
					.fillMaxSize()
			){
				NoteEditor(
					noteState = noteState,
					viewModel = viewModel,
					onClickImage = { noteImage, index ->
						onNavigateToNoteGallery(
							noteImage.noteId,
							index,
							noteState.state.getName())
					},
					onClickReminderDone = { viewModel.onReminderDone() },
					onClickReminder = { onNavigateToReminder(noteState.id,noteState.remindAt ?: 0L) }
				)
			}
			if (showFormatBottomSheet) {
				FormatBottomSheet(
					onDismiss = { showFormatBottomSheet = false },
					onSelectStyle = {
						applyStyle(
							style = it,
							currentValue = noteState.textFieldValue,
							onValueChange = { it1 ->
								viewModel.onTextContentChange(it1)
							}
						)
					}
				)
			}
			if (showBackgroundBottomSheet) {
				BackgroundBottomSheet(
					onDismiss = { showBackgroundBottomSheet = false },
					onSelectColor = { viewModel.onSetBackgroundColor(it)},
					onSelectImage = { viewModel.onSetBackgroundImage(it) },
					selectedColor = noteState.color,
					selectedImage = noteState.background
				)
			}
			if (showAddBottomSheet) {
				AddBottomSheet(
					onDismiss = { showAddBottomSheet = false },
					onClickAddImage = { galleryLauncher.launch("image/*") },
					onClickTakePhoto = { takePhoto.launch() }
				)
			}
			if (showTrashBottomSheet){
				TrashBottomSheet(
					onDismiss = { showTrashBottomSheet = false },
					onRestore = { viewModel.onRestore(); showTrashBottomSheet = false },
					onDeleteForever = { viewModel.onDelete(); showTrashBottomSheet = false  }
				)
			}
		}
	}
}

@Composable
private fun NoteEditor(
	noteState: NoteEditorState,
	viewModel: NoteEditorVIewModel,
	onClickImage: (NoteImage,Int) -> Unit,
	onClickReminderDone: () -> Unit,
	onClickReminder: () -> Unit
){
	val context = LocalContext.current

	LazyVerticalGrid(
		columns = GridCells.Fixed(6),
		modifier = Modifier.fillMaxSize(),
		horizontalArrangement = Arrangement.spacedBy(8.dp),
		verticalArrangement = Arrangement.spacedBy(8.dp),
		userScrollEnabled = true
	) {
		
		noteState.remindAt?.let { remindAt ->
			if (remindAt < System.currentTimeMillis() && !noteState.isReminded){
				item(span = { GridItemSpan(6) }){
					Row (
						modifier = Modifier
							.fillMaxWidth()
							.background(Color.Black)
							.padding(horizontal = 16.dp),
						verticalAlignment = Alignment.CenterVertically,
						horizontalArrangement = Arrangement.SpaceBetween
					){
						Text(
							text = "Sent ${DateTimeUtil.millisToTextFormat(remindAt)}",
							style = MaterialTheme.typography.bodyMedium.copy(
								color = MaterialTheme.colorScheme.surfaceContainerLowest
							)
						)
						IconButton(onClick = { onClickReminderDone() }) {
							Icon(
								painter = painterResource(R.drawable.ic_check),
								contentDescription = null,
								tint = MaterialTheme.colorScheme.surfaceContainerLowest
							)
						}
					}
				}
			}
		}
		// preview bitmaps for new unsaved note
		noteState.imageBitmaps.forEach { bitmap ->
			item (span = { GridItemSpan(2) }){
				Image(
					bitmap = bitmap.asImageBitmap(),
					contentDescription = "" ,
					modifier = Modifier.fillMaxWidth(),
					contentScale = ContentScale.Crop
				)
			}
		}
		// preview uris for new unsaved note
		noteState.imageUris.forEach { uri ->
			item (span = { GridItemSpan(2) }){
				AsyncImage(
					model =  uri,
					contentDescription = "",
					modifier = Modifier.fillMaxWidth(),
					contentScale = ContentScale.Crop
				)
			}
		}
		// preview images for saved note
		noteState.images.forEachIndexed { index, noteImage ->
			item (
				span = {
					if (noteState.images.size % 3 == 1 && index == 0) {
						GridItemSpan(maxLineSpan)
					}else if(noteState.images.size % 3 == 2 && (index == 0 || index == 1)){
						GridItemSpan(3)
					}else{
						GridItemSpan(2)
					}
				}
			){
				AsyncImage(
					model = File(context.dataDir, noteImage.filePath),
					contentDescription = "",
					modifier = Modifier
						.fillMaxWidth()
						.clickable {
							onClickImage(noteImage, index)
						},
					contentScale = ContentScale.FillWidth
				)
			}
		}
		item (span = { GridItemSpan(maxLineSpan)}){
			TextField(
				value = noteState.title,
				onValueChange = {
					viewModel.onTitleChange(it)
				},
				modifier = Modifier.fillMaxWidth(),
				textStyle = MaterialTheme.typography.titleLarge,
				placeholder = {
					Text(
						text = "Title",
						style = MaterialTheme.typography.titleLarge.copy(
							color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
						),
					)
				},
				maxLines = 4,
				colors = TextFieldDefaults.colors(
					unfocusedContainerColor = Color.Transparent,
					focusedContainerColor = Color.Transparent,
					focusedIndicatorColor = Color.Transparent,
					unfocusedIndicatorColor = Color.Transparent
				)
			)
		}
		item(span = { GridItemSpan(maxLineSpan)}){
			if (noteState.isChecklist) {
				CheckListTextEditor(
					listContent = noteState.checkListContent,
					onClickAdd = {
						val items = noteState.checkListContent.toMutableList().apply {
							add(it)
						}
						viewModel.onCheckListChange(items)
					},
					onClickRemove = {
						val items = noteState.checkListContent.toMutableList().apply {
							removeAt(it)
						}
						viewModel.onCheckListChange(items)
					},
					onCheck = {
						val items = noteState.checkListContent.toMutableList()
						val item = items[it]
						items[it] = items[it].copy(checked = !item.checked)
						viewModel.onCheckListChange(items)
					},
					onValueChange = { index, content ->
						val items = noteState.checkListContent.toMutableList()
						items[index] = items[index].copy(content = content)
						viewModel.onCheckListChange(items)
					},
					onMove = { index, newIndex ->
						val items = noteState.checkListContent.toMutableList()
						val temp = items[index]
						items[index] = items[newIndex]
						items[newIndex] = temp

						viewModel.onCheckListChange(items)
					}
				)
			} else {
				RichTextEditor(
					content = noteState.textFieldValue,
					onContentChange = {
						viewModel.onTextContentChange(it)
					}
				)
			}
		}
		noteState.remindAt?.let { remindAt ->
			item (span = { GridItemSpan(6) }){
				Column (
					modifier = Modifier.padding(horizontal = 16.dp)
				){
					ElevatedFilterChip(
						selected = true,
						onClick = { onClickReminder() },
						label = {
							Text(
								text = DateTimeUtil.millisToTextFormat(remindAt),
								style = MaterialTheme.typography.bodyMedium.copy(
									textDecoration =if(noteState.isReminded) TextDecoration.LineThrough else TextDecoration.None
								)
							)
						},
						leadingIcon = {
							Icon(
								painter = painterResource(R.drawable.ic_alarm),
								contentDescription = null,
							)
						}
					)
				}
			}
		}
	}
}

//composable for text field with styles
@Composable
fun RichTextEditor(
	content: TextFieldValue,
	onContentChange:(TextFieldValue)->Unit
){
	TextField(
		value = content,
		onValueChange = {
			updateEditorContent(
				newContent = it,
				currentContent = content,
				onContentChange = onContentChange
			)
		},
		modifier = Modifier.fillMaxSize(),
		placeholder = {
			Text(
				text = "Notes",
				style = MaterialTheme.typography.bodyMedium.copy(
					color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
				),
			)
		},
		colors = TextFieldDefaults.colors(
			unfocusedContainerColor =  Color.Transparent,
			focusedContainerColor =  Color.Transparent,
			focusedIndicatorColor = Color.Transparent,
			unfocusedIndicatorColor = Color.Transparent
		)
	)
}

//composable for  checked list
@Composable
private fun CheckListTextEditor(
	listContent:List<CheckListItem>,
	onClickAdd:(CheckListItem) -> Unit,
	onClickRemove: (Int) -> Unit,
	onCheck: (Int) -> Unit,
	onValueChange: (Int,String) -> Unit,
	onMove: (Int,Int)-> Unit
){
	var checkListItemLayoutInfo  by remember{ mutableStateOf(emptySet<LayoutCoordinates>()) }
	var draggingItemIndex by remember { mutableStateOf<Int?>(null) }
	var draggedOverItemIndex by remember { mutableStateOf<Int?>(null) }

	Column(modifier = Modifier.fillMaxSize()) {
		listContent.forEachIndexed{ index, item ->
			val interactionSource = remember { MutableInteractionSource() }
			val isFocused by interactionSource.collectIsFocusedAsState()
			val isDragging = draggingItemIndex == index
			var offsetY by remember { mutableFloatStateOf(0f) }

			Row(
				modifier = Modifier
					.padding(horizontal = 16.dp)
					.fillMaxWidth()
					.zIndex(if (isDragging) 1f else 0f)
					.graphicsLayer {
						if (isDragging) {
							scaleX = 1.05f
							scaleY = 1.05f
							alpha = 0.9f
							translationY = offsetY
						}
					}
					.onGloballyPositioned { layoutCords ->
						checkListItemLayoutInfo = checkListItemLayoutInfo + layoutCords
					},
				verticalAlignment = Alignment.Top
			) {
				Icon(
					painter = painterResource(id = R.drawable.ic_drag_indicator),
					contentDescription = "",
					modifier = Modifier
						.pointerInput(item){
							detectDragGesturesAfterLongPress(
								onDragStart = {
									draggingItemIndex = index
								},
								onDragEnd = {
									if (draggingItemIndex != null && draggedOverItemIndex != null  && draggedOverItemIndex != listContent.size) {
										onMove(draggingItemIndex!!, draggedOverItemIndex!!)
									}
									draggingItemIndex = null
									draggedOverItemIndex = null
									offsetY = 0f
								},
								onDragCancel = {
									draggingItemIndex = null
									draggedOverItemIndex = null
								},
								onDrag = { change, dragAmount ->
									change.consume()
									// Get the position of the drag
									offsetY += dragAmount.y

									val currentTotalOffset = offsetY + checkListItemLayoutInfo.toList()[index].positionInParent().y
									checkListItemLayoutInfo.forEachIndexed {index1, item1 ->
										// get dragged over item in list
										if (currentTotalOffset >= (item1.positionInParent().y - (item1.size.height / 2))){
											draggedOverItemIndex = index1
										}
									}
								}
							)
						}
				)
				Spacer(Modifier.width(8.dp))
				Checkbox(
					checked = item.checked,
					onCheckedChange = { onCheck(index) },
					modifier = Modifier.size(24.dp)
				)
				Spacer(Modifier.width(16.dp))
				BasicTextField(
					value = item.content,
					onValueChange = {
						onValueChange(index,it)
					},
					modifier = Modifier
						.fillMaxWidth(0.9f),
					interactionSource = interactionSource,
					textStyle = MaterialTheme.typography.bodyLarge,
				)
				if (isFocused) {
					IconButton(
						onClick = { onClickRemove(index) },
						modifier = Modifier.size(24.dp)
					) {
						Icon(
							painter = painterResource(id = R.drawable.ic_close),
							contentDescription = ""
						)
					}
				}
			}
			Spacer(Modifier.height(16.dp))
		}

		Row(
			modifier = Modifier
				.padding(start = 16.dp)
				.clickable {
					onClickAdd(CheckListItem())
				}
				.padding(horizontal = 16.dp)
		) {
			Icon(
				painter = painterResource(R.drawable.ic_add),
				contentDescription = ""
			)
			Spacer(Modifier.width(8.dp))
			Text(text = "Add item")
		}
	}
}

// Restore old text styles when editor content changes
private fun updateEditorContent(
	newContent: TextFieldValue,
	currentContent: TextFieldValue,
	onContentChange: (TextFieldValue) -> Unit
){
	val currentSpanStyles = currentContent.annotatedString.spanStyles.filter { it.start != it.end }.toMutableList()

	if(newContent.text.length != currentContent.text.length){
		val len = newContent.text.length - currentContent.text.length
		currentSpanStyles.forEachIndexed{ index, spanStyle->
			val  oldSelection = currentContent.selection

			if (
				newContent.selection.start.minus(len) in spanStyle.start..spanStyle.end  &&
				(newContent.text.elementAtOrElse(oldSelection.end - len) { ' ' } != '\n')
			){
				currentSpanStyles[index] = spanStyle.copy(end = spanStyle.end.plus(len))
			}
			//update other styles ranges
			if (newContent.selection.end < spanStyle.start){
				currentSpanStyles[index] = spanStyle.copy(start = spanStyle.start.plus(len),end = spanStyle.end.plus(len))
			}
		}
	}
	onContentChange(
		newContent.copy(
			annotatedString = AnnotatedString(
				text =  newContent.text,
				spanStyles = currentSpanStyles,
			)
		)
	)
}

private fun applyStyle(
	style: SpanStyle,
	currentValue: TextFieldValue,
	onValueChange:(TextFieldValue) -> Unit
){
	val selection = currentValue.selection
	val annotatedString = currentValue.annotatedString

	val newString = buildAnnotatedString {
		append(annotatedString)
		if (selection.collapsed){
			addStyle(style, selection.start,selection.start)
		}else if(selection.reversed){
			addStyle(style, selection.end, selection.start)
		}else{
			addStyle(style, selection.start, selection.end)
		}
	}

	onValueChange(
		currentValue.copy(
			annotatedString = newString,
			selection = selection
		)
	)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun  NoteEditorTopBar(
	noteState: NoteEditorState,
	onClickReminder:() -> Unit,
	onClickBack: () -> Unit,
	onTogglePin: () -> Unit,
	onToggleArchive: () -> Unit,
	onClickTrash: () -> Unit,
	onClickShare: () -> Unit
){
	var expanded by  remember { mutableStateOf(false) }

	TopAppBar(
		title = { },
		navigationIcon = {
			IconButton(onClick = onClickBack) {
				Icon(
					painter = painterResource(R.drawable.ic_arrow_back),
					contentDescription = ""
				)
			}
		},
		actions = {
			if (noteState.state != NoteState.TRASH) {
				IconButton(onClick = { onTogglePin() }) {
					if (noteState.isPinned) {
						Icon(
							painter = painterResource(R.drawable.ic_push_pin_filled),
							contentDescription = "",
							tint = MaterialTheme.colorScheme.primary
						)
					} else {
						Icon(
							painter = painterResource(R.drawable.ic_push_pin_outlined),
							contentDescription = ""
						)
					}
				}
				IconButton(onClick = onClickReminder) {
					Icon(
						painter = painterResource(R.drawable.ic_notification_add_outlined),
						contentDescription = ""
					)
				}
				IconButton(onClick = { expanded = true }) {
					Icon(
						painter = painterResource(R.drawable.ic_more_vert),
						contentDescription = ""
					)
				}
				DropdownMenu(
					expanded = expanded,
					onDismissRequest = { expanded = false },
					modifier = Modifier.fillMaxWidth(0.5f)
				) {
					DropdownMenuItem(
						text = {
							Row(verticalAlignment = Alignment.CenterVertically) {
								Icon(
									painter = painterResource(R.drawable.ic_delete_outline),
									contentDescription = "Delete"
								)
								Spacer(Modifier.width(16.dp))
								Text(text = "Delete")
							}
						},
						onClick = { onClickTrash(); expanded = false }
					)
					DropdownMenuItem(
						text = {
							Row(verticalAlignment = Alignment.CenterVertically) {
								if (noteState.state == NoteState.ARCHIVE) {
									Icon(
										painter = painterResource(R.drawable.ic_unarchive_outlined),
										contentDescription = "Unarchive"
									)
									Spacer(Modifier.width(16.dp))
									Text(text = "Unarchive")
								} else {
									Icon(
										painter = painterResource(R.drawable.ic_archive_outline),
										contentDescription = "Archive"
									)
									Spacer(Modifier.width(16.dp))
									Text(text = "Archive")
								}
							}
						},
						onClick ={ onToggleArchive(); expanded = false }
					)
					DropdownMenuItem(
						text = {
							Row(verticalAlignment = Alignment.CenterVertically) {
								Icon(
									painter = painterResource(R.drawable.ic_share_outline),
									contentDescription = "Share"
								)
								Spacer(Modifier.width(16.dp))
								Text(text = "Share")
							}
						},
						onClick = { onClickShare(); expanded = false }
					)
				}
			}
		},
		colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
	)
}

@Composable
private fun NoteEditorFooter(
	noteState: NoteEditorState,
	onClickAdd:() -> Unit,
	onClickBackground:() -> Unit,
	onClickFormating:() -> Unit,
	onClickSave: () -> Unit,
	onClickShowTrash: () -> Unit
){
	BottomAppBar(
		modifier = Modifier,
		containerColor = Color.Transparent,
		windowInsets = WindowInsets.navigationBars
	){
		Row (
			modifier = Modifier
				.fillMaxWidth(),
			horizontalArrangement = Arrangement.SpaceBetween
		){
			Row {
				IconButton(
					onClick = onClickAdd ,
					enabled = noteState.state != NoteState.TRASH
				) {
					Icon(
						painter = painterResource(id = R.drawable.ic_add_box_outlined) ,
						contentDescription = "Add",
						modifier = Modifier.size(20.dp)
					)
				}
				IconButton(
					onClick = onClickBackground,
					enabled = noteState.state != NoteState.TRASH
				) {
					Icon(
						painter = painterResource(id = R.drawable.ic_color_lens_outlined) ,
						contentDescription = "Trash",
						modifier = Modifier.size(20.dp)
					)
				}
				IconButton(
					onClick = onClickFormating,
					enabled = noteState.state != NoteState.TRASH,
					colors = IconButtonDefaults.iconButtonColors(
						contentColor =  MaterialTheme.colorScheme.onBackground
					)
				) {
					Text(
						text = "Aa",
						style = MaterialTheme.typography.titleMedium
					)
				}

			}
			Row {
				IconButton(
					onClick = onClickSave,
					colors = IconButtonDefaults.iconButtonColors(
						contentColor =  MaterialTheme.colorScheme.onBackground
					)
				) {
					Icon(
						painter = painterResource(R.drawable.ic_save_outlined),
						contentDescription = "Save",
						modifier = Modifier.size(20.dp)
					)
				}
				if (noteState.state == NoteState.TRASH) {
					IconButton(onClick = onClickShowTrash) {
						Icon(
							painter = painterResource(R.drawable.ic_more_vert),
							contentDescription = "Delete"
						)
					}
				}
			}
		}
	}
}

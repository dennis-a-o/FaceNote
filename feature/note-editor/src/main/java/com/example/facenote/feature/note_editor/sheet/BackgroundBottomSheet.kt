package com.example.facenote.feature.note_editor.sheet

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.facenote.core.ui.R
import com.example.facenote.core.ui.util.AssetsUtil
import java.io.IOException

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BackgroundBottomSheet(
	onDismiss: () -> Unit,
	onSelectColor:(Color) -> Unit,
	onSelectImage:(String) -> Unit,
	selectedColor: Color?,
	selectedImage: String,
){

	ModalBottomSheet(
		onDismissRequest = onDismiss,
	) {
		Column(
			modifier = Modifier
				.fillMaxWidth()
				.padding(16.dp)
		) {
			Text(
				text = "Color",
				style = MaterialTheme.typography.bodyMedium,
			)
			Spacer(Modifier.height(16.dp))
			ColorList (selectedColor,onSelectColor)
			Spacer(Modifier.height(16.dp))
			Text(
				text = "Background",
				style = MaterialTheme.typography.bodyMedium,
			)
			Spacer(Modifier.height(16.dp))
			ImageList(selectedImage, onSelectImage)
			Spacer(Modifier.height(16.dp))

		}
	}
}


@Composable
private fun ColorList(
	selectedColor: Color? = null,
	onSelect: (Color) -> Unit
) {
	val colors = arrayOf(
		Color(0xFFFFFFFF),
		Color(0xFFCDDC39),
		Color(0xFFE91E63),
		Color(0xFF9C27B0),
		Color(0xFFFFEB3B),
		Color(0xFF3F51B5),
		Color(0xFF2196F3),
		Color(0xFF009688),
		Color(0xFF8BC34A),
		Color(0xFFFF5722),
	)

	Row (
		modifier = Modifier
			.horizontalScroll(rememberScrollState()),
		verticalAlignment = Alignment.CenterVertically
	){
		IconButton(
			onClick ={
				onSelect(Color.Unspecified)
			},
			modifier = Modifier.size(40.dp),
		){
			Icon(
				painter = painterResource(id = R.drawable.ic_format_color_reset_outlined),
				contentDescription = ""
			)
		}
		colors.forEach{ color ->
			Spacer(Modifier.width(16.dp))
			IconButton(
				onClick = {
					onSelect(color)
				},
				modifier = Modifier
					.shadow(elevation = 1.dp, shape = CircleShape)
					.size(32.dp)
					.background(color = color),
			) {
				if (color == selectedColor){
					Icon(
						painter = painterResource(R.drawable.ic_check),
						contentDescription = "",
						tint = Color.White.copy(alpha = 0.8f)
					)
				}
			}
		}
	}
}

@Composable
private fun ImageList(
	selectedImage: String,
	onSelect: (String) -> Unit
) {
	val images = arrayOf("b1.png","b2.png")
	Row (
		modifier = Modifier
			.horizontalScroll(rememberScrollState()),
		verticalAlignment = Alignment.CenterVertically
	){
		IconButton(
			onClick ={
				onSelect("")
			},
			modifier = Modifier.size(40.dp),
		){
			Icon(
				painter = painterResource(id = R.drawable.ic_hide_image_outlined),
				contentDescription = ""
			)
		}
		images.forEach{ image ->
			Spacer(Modifier.width(16.dp))

			AssetsUtil.rememberBitmapFromAsset(image)?.asImageBitmap()?.let { bitmap ->
				Box(contentAlignment = Alignment.Center) {
					Image(
						bitmap = bitmap,
						contentDescription = "",
						modifier = Modifier
							.width(50.dp)
							.height(50.dp)
							.clip(CircleShape)
							.clickable {
								onSelect(image)
							},
						contentScale = ContentScale.Crop
					)
					if (selectedImage == image) {
						Icon(
							painter = painterResource(id = R.drawable.ic_check),
							contentDescription = "",
							tint = MaterialTheme.colorScheme.surfaceContainerLowest
						)
					}
				}

			}
		}
	}
}
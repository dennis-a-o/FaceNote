package com.example.facenote.core.ui.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import java.io.IOException

object AssetsUtil {
	private fun Context.getBitmapFromAssets(fileName:String): Bitmap? {
		return try {
			assets.open(fileName).use { inputStream ->
				BitmapFactory.decodeStream(inputStream)
			}
		}catch (e: IOException){
			e.printStackTrace()
			null
		}
	}

	@Composable
	fun rememberBitmapFromAsset(imageName: String): Bitmap? {
		val context = LocalContext.current
		return remember (imageName){
			context.getBitmapFromAssets(imageName)
		}
	}
}
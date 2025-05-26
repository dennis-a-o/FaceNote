package com.example.facenote.core.ui.util

object FileUtil {
	const val B = 1024
	const val KB = 1024 * 1024
	const val MB = 1024 * 1024 * 1024
	const val GB = 1024 * 1024 * 1024 * 1024

	fun bytesToStringFormat(size: Long): String{
		if (size < 0L)
			return ""
		else
			return when{
				size < B -> "$size Bytes"
				size < KB ->"%.2f KB".format(size / 1024.0 )
				size < MB ->"%.2f MB".format(size / (1024.0 * 1024.0))
				size < GB -> "%.2f GB".format(size / (1024.0 * 1024.0 * 1024.0))
				else -> "%.2f TB".format(size / (1024.0 * 1024.0 * 1024.0 * 1024.0) )
			}
	}
}
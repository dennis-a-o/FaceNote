package com.example.facenote.core.ui.util

import org.threeten.bp.Instant
import org.threeten.bp.ZoneId
import org.threeten.bp.format.DateTimeFormatter
import java.util.Calendar
import java.util.concurrent.TimeUnit
import kotlin.math.abs

object DateTimeUtil {
	//Covert milliseconds to textual format
	fun millisToTextFormat(millis: Long): String{
		return if(isWithinGivenDay(millis,Day.TODAY)) {
			"Today, ${
				Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalDateTime()
					.format(DateTimeFormatter.ofPattern("hh:mm a"))
			}"
		}else if(isWithinGivenDay(millis,Day.YESTERDAY)){
			"Yesterday, ${
				Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalDateTime()
					.format(DateTimeFormatter.ofPattern("hh:mm a"))
			}"
		}else if(isWithinGivenDay(millis, Day.TOMORROW)){
			"Tomorrow, ${
				Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalDateTime()
					.format(DateTimeFormatter.ofPattern("hh:mm a"))
			}"
		}else{
			Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalDateTime()
				.format(DateTimeFormatter.ofPattern("MMMM dd, yyyy hh:mm a"))
		}

	}

	private fun isWithinGivenDay(millis: Long, day: Day):Boolean{
		val calender = Calendar.getInstance()
		calender.add(Calendar.DAY_OF_YEAR,(day.value))//go to previous day by minus the given days
		calender.set(Calendar.HOUR_OF_DAY,0)
		calender.set(Calendar.MINUTE,0)
		calender.set(Calendar.SECOND,0)
		calender.set(Calendar.MILLISECOND,0)

		val startOfPreviousDaysMillis = calender.timeInMillis


		calender.add(Calendar.DAY_OF_YEAR, abs(day.value)) //add days to get end of current day

		val endOfCurrentDayMillis = calender.timeInMillis + TimeUnit.DAYS.toMillis(1)

		return millis in startOfPreviousDaysMillis until endOfCurrentDayMillis
	}
}

enum class Day(val value: Int){
	YESTERDAY(-1),
	TODAY(0),
	TOMORROW(1)
}
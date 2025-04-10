package com.example.facenote.core.model

import java.util.concurrent.TimeUnit

enum class RepeatInterval {
	NONE,
	DAILY,
	WEEKLY,
	MONTHLY,
	YEARLY;

	fun toMillis(): Long = when (this) {
		NONE -> 0L
		DAILY -> TimeUnit.DAYS.toMillis(1)
		WEEKLY -> TimeUnit.DAYS.toMillis(7)
		MONTHLY -> TimeUnit.DAYS.toMillis(30)
		YEARLY -> TimeUnit.DAYS.toMillis(365)
	}
}
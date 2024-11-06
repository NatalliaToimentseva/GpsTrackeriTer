package com.iTergt.routgpstracker.utils

import android.annotation.SuppressLint
import android.icu.util.Calendar
import java.text.SimpleDateFormat
import java.util.TimeZone

private const val TIME_PATTERN = "HH:mm:ss"
private const val TIME_ZONE = "UTC"

@SuppressLint("SimpleDateFormat")
fun convertTimeToString(timeInMls: Long): String {
    val formatter = SimpleDateFormat(TIME_PATTERN)
    formatter.timeZone = TimeZone.getTimeZone(TIME_ZONE)
    val calendar = Calendar.getInstance()
    calendar.timeInMillis = timeInMls
    return formatter.format(calendar.time)
}
package com.iTergt.routgpstracker.utils

import androidx.room.TypeConverter
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset

class DateConverter {

    @TypeConverter
    fun LocalDate.toLong(): Long {
        return this.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()
    }

    @TypeConverter
    fun Long.toLocalDate(): LocalDate {
        return Instant.ofEpochMilli(this).atZone(ZoneOffset.UTC).toLocalDate()
    }
}
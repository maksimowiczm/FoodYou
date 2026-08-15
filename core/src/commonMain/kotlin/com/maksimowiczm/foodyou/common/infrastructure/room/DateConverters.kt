package com.maksimowiczm.foodyou.common.infrastructure.room

import androidx.room.TypeConverter
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime

class DateConverters {
    @TypeConverter fun fromLocalDate(value: LocalDate): Long = value.toEpochDays()

    @TypeConverter fun toLocalDate(value: Long): LocalDate = LocalDate.fromEpochDays(value)

    @TypeConverter fun fromLocalTime(value: LocalTime): Long = value.toNanosecondOfDay()

    @TypeConverter fun toLocalTime(value: Long): LocalTime = LocalTime.fromNanosecondOfDay(value)
}

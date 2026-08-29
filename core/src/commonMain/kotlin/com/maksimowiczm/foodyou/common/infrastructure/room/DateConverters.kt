package com.maksimowiczm.foodyou.common.infrastructure.room

import androidx.room3.*
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime

class DateConverters {
    @ColumnTypeConverter fun fromLocalDate(value: LocalDate): Long = value.toEpochDays()

    @ColumnTypeConverter fun toLocalDate(value: Long): LocalDate = LocalDate.fromEpochDays(value)

    @ColumnTypeConverter fun fromLocalTime(value: LocalTime): Long = value.toNanosecondOfDay()

    @ColumnTypeConverter
    fun toLocalTime(value: Long): LocalTime = LocalTime.fromNanosecondOfDay(value)
}

package com.maksimowiczm.foodyou.feature.fooddiary.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.datetime.LocalTime

@Entity
data class Meal(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val fromHour: Int,
    val fromMinute: Int,
    val toHour: Int,
    val toMinute: Int,
    val rank: Int = 0
)

fun Meal.copy(
    id: Long = this.id,
    name: String = this.name,
    from: LocalTime = LocalTime(fromHour, fromMinute),
    to: LocalTime = LocalTime(toHour, toMinute),
    rank: Int = this.rank
) = Meal(
    id = id,
    name = name,
    fromHour = from.hour,
    fromMinute = from.minute,
    toHour = to.hour,
    toMinute = to.minute,
    rank = rank
)

val Meal.from: LocalTime
    get() = LocalTime(fromHour, fromMinute)

val Meal.to: LocalTime
    get() = LocalTime(toHour, toMinute)

val Meal.isAllDay: Boolean
    get() = from == to

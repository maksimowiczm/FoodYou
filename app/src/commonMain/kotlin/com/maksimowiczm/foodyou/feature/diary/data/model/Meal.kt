package com.maksimowiczm.foodyou.feature.diary.data.model

import com.maksimowiczm.foodyou.feature.diary.database.entity.MealEntity
import kotlinx.datetime.LocalTime

data class Meal(
    val id: Long,
    val name: String,
    val from: LocalTime,
    val to: LocalTime,
    val rank: Int
) {
    val isAllDay: Boolean
        get() = from == to
}

fun MealEntity.toDomain(): Meal {
    val from = LocalTime.fromSecondOfDay(fromHour * 60 * 60 + fromMinute * 60)
    val to = LocalTime.fromSecondOfDay(toHour * 60 * 60 + toMinute * 60)

    return Meal(
        id = id,
        name = name,
        from = from,
        to = to,
        rank = rank
    )
}

fun Meal.toEntity() = MealEntity(
    id = id,
    name = name,
    fromHour = from.hour,
    fromMinute = from.minute,
    toHour = to.hour,
    toMinute = to.minute,
    rank = rank
)

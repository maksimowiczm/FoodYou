package com.maksimowiczm.foodyou.core.feature.addfood.data.model

import com.maksimowiczm.foodyou.core.feature.addfood.database.MealEntity
import kotlinx.datetime.LocalTime

data class Meal(val id: Long, val name: String, val from: LocalTime, val to: LocalTime)

fun MealEntity.toDomain(): Meal {
    val from = LocalTime.fromSecondOfDay(fromHour * 60 * 60 + fromMinute * 60)
    val to = LocalTime.fromSecondOfDay(toHour * 60 * 60 + toMinute * 60)

    return Meal(
        id = id,
        name = name,
        from = from,
        to = to
    )
}

fun Meal.toEntity() = MealEntity(
    id = id,
    name = name,
    fromHour = from.hour,
    fromMinute = from.minute,
    toHour = to.hour,
    toMinute = to.minute
)

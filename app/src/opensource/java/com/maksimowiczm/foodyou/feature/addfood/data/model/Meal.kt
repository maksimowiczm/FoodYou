package com.maksimowiczm.foodyou.feature.addfood.data.model

import com.maksimowiczm.foodyou.feature.addfood.database.MealEntity
import kotlinx.datetime.LocalTime

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

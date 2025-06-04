package com.maksimowiczm.foodyou.core.domain

import com.maksimowiczm.foodyou.core.database.diary.MealEntity
import com.maksimowiczm.foodyou.core.model.Meal
import kotlinx.datetime.LocalTime

interface MealMapper {
    fun toMeal(entity: MealEntity): Meal
}

internal object MealMapperImpl : MealMapper {
    override fun toMeal(entity: MealEntity): Meal = Meal(
        id = entity.id,
        name = entity.name,
        from = LocalTime(entity.fromHour, entity.fromMinute),
        to = LocalTime(entity.toHour, entity.toMinute),
        rank = entity.rank
    )
}

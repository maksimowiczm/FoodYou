package com.maksimowiczm.foodyou.feature.food.diary.search.presentation

import androidx.compose.runtime.Immutable
import com.maksimowiczm.foodyou.fooddiary.domain.entity.Meal

@Immutable
internal data class MealModel(val id: Long, val name: String) {
    constructor(meal: Meal) : this(id = meal.id, name = meal.name)
}

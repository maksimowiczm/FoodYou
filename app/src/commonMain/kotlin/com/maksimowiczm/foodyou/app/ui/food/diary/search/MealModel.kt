package com.maksimowiczm.foodyou.app.ui.food.diary.search

import androidx.compose.runtime.*
import com.maksimowiczm.foodyou.fooddiary.domain.entity.Meal

@Immutable
internal data class MealModel(val id: Long, val name: String) {
    constructor(meal: Meal) : this(id = meal.id, name = meal.name)
}

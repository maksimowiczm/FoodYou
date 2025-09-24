package com.maksimowiczm.foodyou.app.ui.goals.master

import androidx.compose.runtime.*
import com.maksimowiczm.foodyou.common.domain.food.NutritionFacts

@Immutable
internal data class MealModel(
    val id: Long,
    val name: String,
    val nutritionFacts: NutritionFacts,
    val incompleteFoods: List<String>,
)

internal val List<MealModel>.incompleteFoods: List<String>
    get() = this.flatMap { it.incompleteFoods }.distinct()

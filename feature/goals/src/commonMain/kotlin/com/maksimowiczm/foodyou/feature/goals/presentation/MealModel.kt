package com.maksimowiczm.foodyou.feature.goals.presentation

import androidx.compose.runtime.Immutable
import com.maksimowiczm.foodyou.business.shared.domain.nutrients.NutritionFacts

@Immutable
internal data class MealModel(
    val id: Long,
    val name: String,
    val nutritionFacts: NutritionFacts,
    val incompleteFoods: List<String>,
)

internal val List<MealModel>.incompleteFoods: List<String>
    get() = this.flatMap { it.incompleteFoods }.distinct()

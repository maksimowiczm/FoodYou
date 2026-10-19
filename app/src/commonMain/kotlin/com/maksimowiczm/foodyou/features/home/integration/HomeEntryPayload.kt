package com.maksimowiczm.foodyou.features.home.integration

import com.maksimowiczm.foodyou.common.domain.food.MeasuredFoodSnapshot
import com.maksimowiczm.foodyou.common.domain.food.NutritionFacts
import kotlinx.serialization.Serializable

@Serializable
sealed interface HomeEntryPayload {
    @Serializable data class FoodDiaryEntry(val snapshot: MeasuredFoodSnapshot) : HomeEntryPayload

    @Serializable
    data class QuickAdd(
        val name: String,
        val nutritionFacts: NutritionFacts,
    ) : HomeEntryPayload
}

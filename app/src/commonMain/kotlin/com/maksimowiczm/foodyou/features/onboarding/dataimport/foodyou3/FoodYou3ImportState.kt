package com.maksimowiczm.foodyou.features.onboarding.dataimport.foodyou3

import androidx.compose.runtime.*
import com.maksimowiczm.foodyou.features.onboarding.ImportState
import kotlinx.serialization.Serializable

@Serializable
@Immutable
data class FoodYou3ImportState(
    val path: String? = null,
    val mealPlan: Boolean = true,
    val diaryEntries: Boolean = true,
    val yourProducts: Boolean = true,
    val yourRecipes: Boolean = true,
    val openFoodFacts: Boolean = true,
    val foodDataCentral: Boolean = true,
    val skipUnusedRemote: Boolean = true,
) : ImportState

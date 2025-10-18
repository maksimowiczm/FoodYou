package com.maksimowiczm.foodyou.app.ui.food.details

import androidx.compose.runtime.Immutable
import com.maksimowiczm.foodyou.food.domain.FoodImage
import com.maksimowiczm.foodyou.food.domain.FoodName
import com.maksimowiczm.foodyou.food.domain.FoodNote
import com.maksimowiczm.foodyou.food.domain.FoodProductIdentity
import com.maksimowiczm.foodyou.food.domain.FoodSource
import com.maksimowiczm.foodyou.food.domain.NutritionFacts

@Immutable
sealed interface FoodDetailsUiState {
    val identity: FoodProductIdentity

    @Immutable
    data class WithData(
        override val identity: FoodProductIdentity,
        val isLoading: Boolean,
        val foodName: FoodName?,
        val image: FoodImage?,
        val nutritionFacts: NutritionFacts?,
        val note: FoodNote?,
        val source: FoodSource?,
    ) : FoodDetailsUiState

    @Immutable data class NotFound(override val identity: FoodProductIdentity) : FoodDetailsUiState

    @Immutable
    data class Error(override val identity: FoodProductIdentity, val message: String?) :
        FoodDetailsUiState
}

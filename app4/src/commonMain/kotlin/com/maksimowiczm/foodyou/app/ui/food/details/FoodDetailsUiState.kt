package com.maksimowiczm.foodyou.app.ui.food.details

import androidx.compose.runtime.Immutable
import com.maksimowiczm.foodyou.food.domain.FoodImage
import com.maksimowiczm.foodyou.food.domain.FoodName
import com.maksimowiczm.foodyou.food.domain.FoodNameSelector
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
        val brand: String?,
        val image: FoodImageUiState,
        val nutritionFacts: NutritionFacts?,
        val note: FoodNote?,
        val source: FoodSource?,
    ) : FoodDetailsUiState {
        private var headlineCache: String? = null

        fun headline(nameSelector: FoodNameSelector): String? {
            if (headlineCache != null) return headlineCache
            if (foodName == null) return null

            headlineCache = buildString {
                val brandSuffix = brand?.let { " (${it})" } ?: ""
                append(nameSelector.select(foodName))
                append(brandSuffix)
            }

            return headlineCache
        }
    }

    @Immutable data class NotFound(override val identity: FoodProductIdentity) : FoodDetailsUiState

    @Immutable
    data class Error(override val identity: FoodProductIdentity, val message: String?) :
        FoodDetailsUiState
}

@Immutable
sealed interface FoodImageUiState {
    @Immutable data object Loading : FoodImageUiState

    @Immutable data object NoImage : FoodImageUiState

    @Immutable data class WithImage(val image: FoodImage) : FoodImageUiState
}

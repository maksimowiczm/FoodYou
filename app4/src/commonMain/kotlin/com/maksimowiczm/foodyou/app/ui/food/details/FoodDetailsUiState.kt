package com.maksimowiczm.foodyou.app.ui.food.details

import androidx.compose.runtime.*
import com.maksimowiczm.foodyou.common.domain.food.FoodImage
import com.maksimowiczm.foodyou.common.domain.food.FoodName
import com.maksimowiczm.foodyou.common.domain.food.FoodNameSelector
import com.maksimowiczm.foodyou.common.domain.food.FoodNote
import com.maksimowiczm.foodyou.common.domain.food.FoodSource
import com.maksimowiczm.foodyou.common.domain.food.NutritionFacts

@Immutable
sealed interface FoodDetailsUiState {
    val identity: Any

    @Immutable
    data class WithData(
        override val identity: Any,
        val isLoading: Boolean,
        val foodName: FoodName?,
        val brand: String?,
        val image: FoodImageUiState,
        val nutritionFacts: NutritionFacts?,
        val note: FoodNote?,
        val source: FoodSource?,
        val isFavorite: Boolean,
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

        companion object {
            fun loading(identity: Any) =
                WithData(
                    identity = identity,
                    isLoading = true,
                    foodName = null,
                    brand = null,
                    image = FoodImageUiState.Loading,
                    nutritionFacts = null,
                    note = null,
                    source = null,
                    isFavorite = false,
                )
        }
    }

    @Immutable data class NotFound(override val identity: Any) : FoodDetailsUiState

    @Immutable
    data class Error(override val identity: Any, val message: String?) : FoodDetailsUiState
}

@Immutable
sealed interface FoodImageUiState {
    @Immutable data object Loading : FoodImageUiState

    @Immutable data object NoImage : FoodImageUiState

    @Immutable data class WithImage(val image: FoodImage) : FoodImageUiState
}

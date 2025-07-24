package com.maksimowiczm.foodyou.feature.food.ui.search

import androidx.compose.runtime.*
import com.maksimowiczm.foodyou.feature.food.domain.FoodSource

@Immutable
internal data class FoodFilter(val source: FoodSource.Type? = null) {

    val isYourFood: Boolean
        get() = when (source) {
            null,
            FoodSource.Type.User -> true

            else -> false
        }

    val filterCount: Int
        get() {
            var count = 0

            if (source != null) {
                count++
            }

            return count
        }
}

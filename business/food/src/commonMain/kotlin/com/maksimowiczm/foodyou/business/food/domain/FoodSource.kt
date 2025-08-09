package com.maksimowiczm.foodyou.business.food.domain

import kotlinx.serialization.Serializable

@Serializable
data class FoodSource(val type: Type, val url: String? = null) {
    enum class Type {
        User,
        OpenFoodFacts,
        USDA,
        SwissFoodCompositionDatabase,
    }
}

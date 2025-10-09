package com.maksimowiczm.foodyou.food.domain

data class FoodSource(val type: Type, val url: String? = null) {
    enum class Type {
        User,
        OpenFoodFacts,
        USDA,
        SwissFoodCompositionDatabase,
    }
}

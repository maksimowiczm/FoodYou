package com.maksimowiczm.foodyou.foodyou3.domain

data class FoodYou3FoodSource(val type: Type, val url: String? = null) {
    enum class Type {
        User,
        OpenFoodFacts,
        USDA,
        SwissFoodCompositionDatabase,
    }
}

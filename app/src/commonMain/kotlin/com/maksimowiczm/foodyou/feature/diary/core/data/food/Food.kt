package com.maksimowiczm.foodyou.feature.diary.core.data.food

sealed interface Food {
    val id: FoodId
    val name: String
    val brand: String?
    val nutrients: Nutrients
    val packageWeight: PortionWeight.Package?
    val servingWeight: PortionWeight.Serving?
}

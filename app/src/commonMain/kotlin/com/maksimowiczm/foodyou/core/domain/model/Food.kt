package com.maksimowiczm.foodyou.core.domain.model

sealed interface Food {
    val id: FoodId
    val name: String
    val brand: String?
    val nutrients: Nutrients
    val packageWeight: PortionWeight.Package?
    val servingWeight: PortionWeight.Serving?
}

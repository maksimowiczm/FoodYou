package com.maksimowiczm.foodyou.navigation.domain

import com.maksimowiczm.foodyou.shared.common.domain.food.FoodId
import kotlinx.serialization.Serializable

@Serializable
data class UpdateProductDestination(val productId: Long) : NavigationDestination {
    constructor(foodId: FoodId.Product) : this(foodId.id)

    val foodId: FoodId.Product
        get() = FoodId.Product(productId)
}

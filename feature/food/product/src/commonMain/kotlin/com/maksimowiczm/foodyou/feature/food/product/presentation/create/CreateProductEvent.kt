package com.maksimowiczm.foodyou.feature.food.product.presentation.create

import com.maksimowiczm.foodyou.core.food.domain.entity.FoodId

internal sealed interface CreateProductEvent {

    data class Created(val productId: FoodId.Product) : CreateProductEvent
}

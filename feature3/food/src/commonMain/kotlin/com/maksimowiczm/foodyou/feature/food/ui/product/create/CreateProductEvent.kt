package com.maksimowiczm.foodyou.feature.food.ui.product.create

import com.maksimowiczm.foodyou.feature.food.domain.FoodId

internal sealed interface CreateProductEvent {

    data class Created(val productId: FoodId.Product) : CreateProductEvent
}

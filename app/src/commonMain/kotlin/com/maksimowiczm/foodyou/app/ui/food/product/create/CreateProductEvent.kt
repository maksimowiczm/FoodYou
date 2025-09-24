package com.maksimowiczm.foodyou.app.ui.food.product.create

import com.maksimowiczm.foodyou.food.domain.entity.FoodId

internal sealed interface CreateProductEvent {

    data class Created(val productId: FoodId.Product) : CreateProductEvent
}

package com.maksimowiczm.foodyou.feature.food.product.presentation.create

import com.maksimowiczm.foodyou.business.shared.domain.food.FoodId

internal sealed interface CreateProductEvent {

    data class Created(val productId: FoodId.Product) : CreateProductEvent
}

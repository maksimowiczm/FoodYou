package com.maksimowiczm.foodyou.app.ui.product.create

import com.maksimowiczm.foodyou.food.domain.FoodProductIdentity

sealed interface CreateProductEvent {
    data class Created(val id: FoodProductIdentity.Local) : CreateProductEvent
}

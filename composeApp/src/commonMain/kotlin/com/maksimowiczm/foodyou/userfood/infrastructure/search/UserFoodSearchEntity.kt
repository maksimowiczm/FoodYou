package com.maksimowiczm.foodyou.userfood.infrastructure.search

import androidx.room.Embedded
import com.maksimowiczm.foodyou.userfood.infrastructure.product.ProductEntity

internal data class UserFoodSearchEntity(
    @Embedded("p_") val product: ProductEntity,
    val simpleName: String,
)

package com.maksimowiczm.foodyou.userfood.infrastructure.room.search

import androidx.room.Embedded
import com.maksimowiczm.foodyou.userfood.infrastructure.room.product.ProductEntity

internal data class UserFoodSearchEntity(
    @Embedded("p_") val product: ProductEntity?,
    val recipeId: String?,
)

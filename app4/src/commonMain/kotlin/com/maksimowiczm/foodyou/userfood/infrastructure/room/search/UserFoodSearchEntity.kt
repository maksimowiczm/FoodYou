package com.maksimowiczm.foodyou.userfood.infrastructure.room.search

import androidx.room.Embedded
import com.maksimowiczm.foodyou.userfood.infrastructure.room.product.ProductEntity
import com.maksimowiczm.foodyou.userfood.infrastructure.room.recipe.RecipeEntity

internal data class UserFoodSearchEntity(
    @Embedded("p_") val product: ProductEntity?,
    @Embedded("r_") val recipe: RecipeEntity?,
    val simpleName: String,
)

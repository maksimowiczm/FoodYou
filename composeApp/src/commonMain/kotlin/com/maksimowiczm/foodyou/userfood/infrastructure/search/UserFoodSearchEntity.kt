package com.maksimowiczm.foodyou.userfood.infrastructure.search

import androidx.room.Embedded
import com.maksimowiczm.foodyou.userfood.infrastructure.product.ProductEntity
import com.maksimowiczm.foodyou.userfood.infrastructure.recipe.RecipeEntity

internal data class UserFoodSearchEntity(
    @Embedded("p_") val product: ProductEntity?,
    @Embedded("r_") val recipe: RecipeEntity?,
    val simpleName: String,
)

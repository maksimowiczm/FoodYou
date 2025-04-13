package com.maksimowiczm.foodyou.core.database.recipe

import androidx.room.DatabaseView
import androidx.room.Embedded
import com.maksimowiczm.foodyou.core.database.product.ProductEntity

@DatabaseView(
    """
    SELECT
        p.id AS p_id,
        p.name AS p_name,
        p.brand AS p_brand,
        p.barcode AS p_barcode,
        p.calories AS p_calories,
        p.proteins AS p_proteins,
        p.carbohydrates AS p_carbohydrates,
        p.sugars AS p_sugars,
        p.fats AS p_fats,
        p.saturatedFats AS p_saturatedFats,
        p.salt AS p_salt,
        p.sodium AS p_sodium,
        p.fiber AS p_fiber,
        p.packageWeight AS p_packageWeight,
        p.servingWeight AS p_servingWeight,
        p.productSource AS p_productSource,
        i.id AS r_id,
        i.recipeId AS r_recipeId,
        i.productId AS r_productId,
        i.measurement AS r_measurement,
        i.quantity AS r_quantity
    FROM RecipeIngredientEntity i
    JOIN ProductEntity p ON i.productId = p.id
    """
)
data class RecipeIngredientWithProductView(
    @Embedded(prefix = "p_")
    val product: ProductEntity,

    @Embedded(prefix = "r_")
    val recipeIngredientEntity: RecipeIngredientEntity
)

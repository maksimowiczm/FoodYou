package com.maksimowiczm.foodyou.core.data.model.recipe

import androidx.room.DatabaseView
import androidx.room.Embedded
import com.maksimowiczm.foodyou.core.data.model.product.ProductEntity

@DatabaseView(
    """
    SELECT
        p.id AS p_id,
        p.name AS p_name,
        p.brand AS p_brand,
        p.barcode AS p_barcode,
        p.proteins AS p_proteins,
        p.carbohydrates AS p_carbohydrates,
        p.fats AS p_fats,
        p.calories AS p_calories,
        p.saturatedFats AS p_saturatedFats,
        p.monounsaturatedFats AS p_monounsaturatedFats,
        p.polyunsaturatedFats AS p_polyunsaturatedFats,
        p.omega3 AS p_omega3,
        p.omega6 AS p_omega6,
        p.sugars AS p_sugars,
        p.salt AS p_salt,
        p.fiber AS p_fiber,
        p.cholesterolMilli AS p_cholesterolMilli,
        p.caffeineMilli AS p_caffeineMilli,
        p.vitaminAMicro AS p_vitaminAMicro,
        p.vitaminB1Milli AS p_vitaminB1Milli,
        p.vitaminB2Milli AS p_vitaminB2Milli,
        p.vitaminB3Milli AS p_vitaminB3Milli,
        p.vitaminB5Milli AS p_vitaminB5Milli,
        p.vitaminB6Milli AS p_vitaminB6Milli,
        p.vitaminB7Micro AS p_vitaminB7Micro,
        p.vitaminB9Micro AS p_vitaminB9Micro,
        p.vitaminB12Micro AS p_vitaminB12Micro,
        p.vitaminCMilli AS p_vitaminCMilli,
        p.vitaminDMicro AS p_vitaminDMicro,
        p.vitaminEMilli AS p_vitaminEMilli,
        p.vitaminKMicro AS p_vitaminKMicro,
        p.manganeseMilli AS p_manganeseMilli,
        p.magnesiumMilli AS p_magnesiumMilli,
        p.potassiumMilli AS p_potassiumMilli,
        p.calciumMilli AS p_calciumMilli,
        p.copperMilli AS p_copperMilli,
        p.zincMilli AS p_zincMilli,
        p.sodiumMilli AS p_sodiumMilli,
        p.ironMilli AS p_ironMilli,
        p.phosphorusMilli AS p_phosphorusMilli,
        p.seleniumMicro AS p_seleniumMicro,
        p.iodineMicro AS p_iodineMicro,
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
data class RecipeIngredientProductDetails(
    @Embedded(prefix = "p_")
    val product: ProductEntity,

    @Embedded(prefix = "r_")
    val recipeIngredientEntity: RecipeIngredientEntity
)

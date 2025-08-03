package com.maksimowiczm.foodyou.feature.fooddiary.data

import androidx.room.Embedded
import androidx.room.Relation
import com.maksimowiczm.foodyou.feature.food.data.database.food.Product
import com.maksimowiczm.foodyou.feature.food.data.database.food.Recipe

data class FoodWithMeasurement(
    @Embedded
    val measurement: Measurement,

    @Relation(
        parentColumn = "productId",
        entityColumn = "id",
        entity = Product::class
    )
    val product: Product?,

    @Relation(
        parentColumn = "recipeId",
        entityColumn = "id",
        entity = Recipe::class
    )
    val recipe: Recipe?
)

package com.maksimowiczm.foodyou.core.data.model.measurement

import androidx.room.Embedded
import androidx.room.Relation
import com.maksimowiczm.foodyou.core.data.model.recipe.RecipeEntity
import com.maksimowiczm.foodyou.core.data.model.recipe.RecipeIngredientProductDetails

data class RecipeWithMeasurement(
    @Embedded(prefix = "r_")
    val recipe: RecipeEntity,

    @Embedded(prefix = "m_")
    val measurement: RecipeMeasurementEntity,

    @Relation(
        parentColumn = "r_id",
        entityColumn = "r_recipeId",
        entity = RecipeIngredientProductDetails::class
    )
    val ingredients: List<RecipeIngredientProductDetails>
)

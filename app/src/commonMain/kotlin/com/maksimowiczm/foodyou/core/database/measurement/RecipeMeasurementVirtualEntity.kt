package com.maksimowiczm.foodyou.core.database.measurement

import androidx.room.Embedded
import androidx.room.Relation
import com.maksimowiczm.foodyou.core.database.recipe.RecipeEntity

data class RecipeMeasurementVirtualEntity(
    @Embedded
    val recipe: RecipeEntity,

    @Relation(
        parentColumn = "id",
        entityColumn = "recipeId",
        entity = RecipeMeasurementEntity::class
    )
    val measurement: RecipeMeasurementEntity
)

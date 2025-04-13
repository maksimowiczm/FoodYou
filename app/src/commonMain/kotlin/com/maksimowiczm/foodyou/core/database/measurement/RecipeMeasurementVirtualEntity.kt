package com.maksimowiczm.foodyou.core.database.measurement

import androidx.room.Embedded
import androidx.room.Relation
import com.maksimowiczm.foodyou.core.database.recipe.RecipeEntity
import com.maksimowiczm.foodyou.core.database.recipe.RecipeIngredientWithProductView

data class RecipeMeasurementVirtualEntity(
    @Embedded(prefix = "r_")
    val recipe: RecipeEntity,

    @Embedded(prefix = "m_")
    val measurement: RecipeMeasurementEntity,

    @Relation(
        parentColumn = "r_id",
        entityColumn = "r_recipeId",
        entity = RecipeIngredientWithProductView::class
    )
    val ingredients: List<RecipeIngredientWithProductView>
)

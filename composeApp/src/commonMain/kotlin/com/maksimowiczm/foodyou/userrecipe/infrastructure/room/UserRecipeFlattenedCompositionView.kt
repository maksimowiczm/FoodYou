package com.maksimowiczm.foodyou.userrecipe.infrastructure.room

import androidx.room.DatabaseView
import com.maksimowiczm.foodyou.common.domain.food.FoodCompositionComponentIdentity
import kotlin.uuid.Uuid

@DatabaseView(
    viewName = "UserRecipeFlattenedComposition",
    value =
        """
        WITH RECURSIVE
          flattened(recipeId, componentIdentity) AS (
            SELECT recipeId, componentIdentity
            FROM UserRecipeCompositionReference
            UNION
            SELECT r.recipeId, f.componentIdentity
            FROM UserRecipeCompositionReference r
            JOIN flattened f ON r.componentIdentity = 'RECIPE:' || CAST(f.recipeId AS TEXT)
          )
        SELECT * FROM flattened
    """,
)
data class UserRecipeFlattenedCompositionView(
    val recipeId: Uuid,
    val componentIdentity: FoodCompositionComponentIdentity.Identified,
)

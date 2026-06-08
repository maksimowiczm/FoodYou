package com.maksimowiczm.foodyou.userrecipe.infrastructure.room

import androidx.room.DatabaseView
import kotlin.uuid.Uuid

@DatabaseView(
    viewName = "UserRecipeFlattenedComposition",
    value =
        """
        WITH RECURSIVE
          flattened(recipeId, componentType, componentValue) AS (
            SELECT recipeId, componentType, componentValue
            FROM UserRecipeCompositionReference
            UNION
            SELECT r.recipeId, f.componentType, f.componentValue
            FROM UserRecipeCompositionReference r
            JOIN flattened f ON r.componentType = 'RECIPE' AND r.componentValue = CAST(f.recipeId AS TEXT)
          )
        SELECT * FROM flattened
    """,
)
data class UserRecipeFlattenedCompositionView(
    val recipeId: Uuid,
    val componentType: String,
    val componentValue: String,
)

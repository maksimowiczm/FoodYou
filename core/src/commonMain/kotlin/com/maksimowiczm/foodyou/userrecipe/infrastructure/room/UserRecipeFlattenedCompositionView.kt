package com.maksimowiczm.foodyou.userrecipe.infrastructure.room

import androidx.room3.*
import com.maksimowiczm.foodyou.common.domain.food.FoodSnapshotId
import kotlin.uuid.Uuid

@DatabaseView(
    viewName = "UserRecipeFlattenedComposition",
    value =
        """
        WITH RECURSIVE
          flattened(recipeId, snapshotId) AS (
            SELECT recipeId, snapshotId
            FROM UserRecipeCompositionReference
            UNION
            SELECT r.recipeId, f.snapshotId
            FROM UserRecipeCompositionReference r
            JOIN flattened f ON r.snapshotId = 'RECIPE:' || CAST(f.recipeId AS TEXT)
          )
        SELECT * FROM flattened
    """,
)
data class UserRecipeFlattenedCompositionView(
    val recipeId: Uuid,
    val snapshotId: FoodSnapshotId.Tracked,
)

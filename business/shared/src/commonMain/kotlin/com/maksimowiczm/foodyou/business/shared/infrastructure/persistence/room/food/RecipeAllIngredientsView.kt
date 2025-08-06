package com.maksimowiczm.foodyou.business.shared.infrastructure.persistence.room.food

import androidx.room.DatabaseView

@DatabaseView(
    """
    WITH RECURSIVE recipeIngredients AS (
        -- Base case: Direct ingredients of all recipes
        SELECT 
            ri.recipeId AS targetRecipeId,
            ri.recipeId AS parentRecipeId,
            ri.ingredientProductId AS productId,
            ri.ingredientRecipeId AS recipeId,
            ri.measurement,
            ri.quantity,
            1 AS depthLevel
        FROM RecipeIngredient ri
        
        UNION ALL
        
        -- Recursive case: Ingredients of sub-recipes
        SELECT 
            prev.targetRecipeId,
            subRi.recipeId AS parentRecipeId,
            subRi.ingredientProductId AS productId,
            subRi.ingredientRecipeId AS recipeId,
            subRi.measurement,
            subRi.quantity,
            prev.depthLevel + 1 AS depthLevel
        FROM RecipeIngredient subRi
        INNER JOIN recipeIngredients prev ON subRi.recipeId = prev.recipeId
        WHERE prev.recipeId IS NOT NULL
    )
    SELECT DISTINCT
        targetRecipeId,
        COALESCE(productId, recipeId) AS ingredientId
    FROM recipeIngredients
    """
)
data class RecipeAllIngredientsView(val targetRecipeId: Long, val ingredientId: Long)

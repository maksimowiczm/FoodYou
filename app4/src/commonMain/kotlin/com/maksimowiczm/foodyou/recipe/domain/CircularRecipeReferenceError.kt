package com.maksimowiczm.foodyou.recipe.domain

/**
 * Exception thrown when a circular reference is detected in recipe ingredients.
 *
 * A circular reference occurs when:
 * - Recipe A contains Recipe B as ingredient
 * - Recipe B contains Recipe A as ingredient (direct cycle) OR
 * - Recipe A contains Recipe B, B contains Recipe C, C contains Recipe A (indirect cycle)
 *
 * Circular references are not allowed because they:
 * - Make cascade deletion infinite
 * - Make nutrition calculation impossible
 * - Create logical inconsistencies
 */
class CircularRecipeReferenceError(val recipeId: String, val cyclePath: List<String>) {
    val message
        get() = "Circular reference detected: ${cyclePath.joinToString(" → ")} → $recipeId"
}

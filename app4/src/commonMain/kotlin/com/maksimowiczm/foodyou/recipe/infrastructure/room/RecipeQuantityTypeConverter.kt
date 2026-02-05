package com.maksimowiczm.foodyou.recipe.infrastructure.room

import androidx.room.TypeConverter

internal class RecipeQuantityTypeConverter {
    @TypeConverter
    fun fromRecipeQuantityType(value: RecipeQuantityType): Int =
        when (value) {
            RecipeQuantityType.Weight -> WEIGHT
            RecipeQuantityType.Volume -> VOLUME
            RecipeQuantityType.Package -> PACKAGE
            RecipeQuantityType.Serving -> SERVING
        }

    @TypeConverter
    fun toRecipeQuantityType(value: Int): RecipeQuantityType =
        when (value) {
            WEIGHT -> RecipeQuantityType.Weight
            VOLUME -> RecipeQuantityType.Volume
            PACKAGE -> RecipeQuantityType.Package
            SERVING -> RecipeQuantityType.Serving
            else -> error("Unknown recipe quantity type: $value")
        }

    private companion object {
        private const val WEIGHT = 0
        private const val VOLUME = 1
        private const val PACKAGE = 2
        private const val SERVING = 3
    }
}

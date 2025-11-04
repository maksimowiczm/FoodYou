package com.maksimowiczm.foodyou.account.infrastructure.room

import androidx.room.TypeConverter

class FoodIdentityTypeConverter {
    @TypeConverter
    fun toFoodIdentityType(value: Int): FoodIdentityType =
        when (value) {
            0 -> FoodIdentityType.LocalProduct
            1 -> FoodIdentityType.OpenFoodFacts
            2 -> FoodIdentityType.FoodDataCentral
            else -> error("Unknown food identity type value: $value")
        }

    @TypeConverter
    fun fromFoodIdentityType(foodIdentityType: FoodIdentityType): Int =
        when (foodIdentityType) {
            FoodIdentityType.LocalProduct -> 0
            FoodIdentityType.OpenFoodFacts -> 1
            FoodIdentityType.FoodDataCentral -> 2
        }
}

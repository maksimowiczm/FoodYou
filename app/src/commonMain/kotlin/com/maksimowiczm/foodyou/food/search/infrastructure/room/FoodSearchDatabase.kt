package com.maksimowiczm.foodyou.food.search.infrastructure.room

interface FoodSearchDatabase {
    val foodSearchDao: FoodSearchDao
    val openFoodFactsPagingKeyDao: OpenFoodFactsPagingKeyDao
}

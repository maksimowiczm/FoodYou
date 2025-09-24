package com.maksimowiczm.foodyou.food.search.infrastructure.room

interface FoodSearchDatabase {
    val foodSearchDao: FoodSearchDao
    val usdaPagingKeyDao: USDAPagingKeyDao
    val openFoodFactsPagingKeyDao: OpenFoodFactsPagingKeyDao
}

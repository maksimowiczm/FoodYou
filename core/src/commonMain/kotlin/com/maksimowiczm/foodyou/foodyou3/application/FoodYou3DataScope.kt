package com.maksimowiczm.foodyou.foodyou3.application

import com.maksimowiczm.foodyou.foodyou3.domain.food.FoodYou3FoodSearchRepository

interface FoodYou3DataScope {
    val foodSearchRepository: FoodYou3FoodSearchRepository

    suspend fun close()
}

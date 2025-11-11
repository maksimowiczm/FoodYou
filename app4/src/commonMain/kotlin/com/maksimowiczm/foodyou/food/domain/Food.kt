package com.maksimowiczm.foodyou.food.domain

sealed interface Food {
    val identity: FoodIdentity
}

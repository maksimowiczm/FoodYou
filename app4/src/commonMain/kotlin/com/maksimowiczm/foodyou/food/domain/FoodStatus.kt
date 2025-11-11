package com.maksimowiczm.foodyou.food.domain

sealed interface FoodStatus<T> {
    val identity: FoodIdentity

    /**
     * Indicates that the food product is currently being loaded. If partial data is available, it
     * is included.
     */
    data class Loading<T>(override val identity: FoodIdentity, val food: T?) : FoodStatus<T>

    data class NotFound<T>(override val identity: FoodIdentity) : FoodStatus<T>

    data class Available<T>(override val identity: FoodIdentity, val food: T) : FoodStatus<T>

    /**
     * Indicates that an error occurred while loading the food product. If partial data is
     * available, it is included.
     */
    data class Error<T>(
        override val identity: FoodIdentity,
        val food: T?,
        val error: FoodDatabaseError,
    ) : FoodStatus<T>
}

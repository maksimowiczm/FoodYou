package com.maksimowiczm.foodyou.food.domain

sealed interface FoodStatus<out F : Food> {
    val identity: FoodIdentity

    /**
     * Indicates that the food product is currently being loaded. If partial data is available, it
     * is included.
     */
    data class Loading<F : Food>(override val identity: FoodIdentity, val food: F?) : FoodStatus<F>

    data class NotFound<F : Food>(override val identity: FoodIdentity) : FoodStatus<F>

    data class Available<F : Food>(val food: F) : FoodStatus<F> {
        override val identity: FoodIdentity = food.identity
    }

    /**
     * Indicates that an error occurred while loading the food product. If partial data is
     * available, it is included.
     */
    data class Error<F : Food>(
        override val identity: FoodIdentity,
        val food: F?,
        val error: FoodDatabaseError,
    ) : FoodStatus<F>
}

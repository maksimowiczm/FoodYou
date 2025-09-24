package com.maksimowiczm.foodyou.food.domain.entity

import kotlin.time.Instant

sealed interface FoodHistory {
    val timestamp: Instant

    sealed interface CreationHistory : FoodHistory

    class Created(override val timestamp: Instant) : CreationHistory

    class Downloaded(override val timestamp: Instant, val url: String?) : CreationHistory

    class Imported(override val timestamp: Instant) : CreationHistory

    class Edited(override val timestamp: Instant) : FoodHistory

    /**
     * Represents an event when a food item is imported from FoodYou2. This event is used to track
     * the migration of data from the old app.
     */
    class ImportedFromFoodYou2(override val timestamp: Instant) : FoodHistory
}

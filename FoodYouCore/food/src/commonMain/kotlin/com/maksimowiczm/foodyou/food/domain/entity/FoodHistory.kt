package com.maksimowiczm.foodyou.food.domain.entity

import kotlinx.datetime.LocalDateTime

sealed interface FoodHistory {
    val date: LocalDateTime

    sealed interface CreationHistory : FoodHistory

    class Created(override val date: LocalDateTime) : CreationHistory

    class Downloaded(override val date: LocalDateTime, val url: String?) : CreationHistory

    class Imported(override val date: LocalDateTime) : CreationHistory

    class Edited(override val date: LocalDateTime) : FoodHistory

    /**
     * Represents an event when a food item is imported from FoodYou2. This event is used to track
     * the migration of data from the old app.
     */
    class ImportedFromFoodYou2(override val date: LocalDateTime) : FoodHistory
}

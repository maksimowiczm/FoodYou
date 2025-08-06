package com.maksimowiczm.foodyou.business.food.domain

import kotlinx.datetime.LocalDateTime

sealed interface FoodEvent {
    val date: LocalDateTime

    sealed interface FoodCreationEvent : FoodEvent

    class Created(override val date: LocalDateTime) : FoodCreationEvent

    class Downloaded(override val date: LocalDateTime, val url: String?) : FoodCreationEvent

    class Imported(override val date: LocalDateTime) : FoodCreationEvent

    class Edited(override val date: LocalDateTime) : FoodEvent

    /**
     * Represents an event when a food item is imported from FoodYou2. This event is used to track
     * the migration of data from the old app.
     */
    class ImportedFromFoodYou2(override val date: LocalDateTime) : FoodEvent
}

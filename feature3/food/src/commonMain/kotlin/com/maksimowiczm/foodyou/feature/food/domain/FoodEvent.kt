package com.maksimowiczm.foodyou.feature.food.domain

import kotlinx.datetime.LocalDateTime

sealed interface FoodEvent {
    val date: LocalDateTime

    sealed interface FoodCreationEvent : FoodEvent

    class Created(override val date: LocalDateTime) : FoodCreationEvent

    class Downloaded(override val date: LocalDateTime, val url: String?) : FoodCreationEvent

    class Imported(override val date: LocalDateTime) : FoodCreationEvent

    class Edited(override val date: LocalDateTime, val oldFood: Food) : FoodEvent

    /**
     * Represents an event when a food item is used by an user in any way.
     */
    class Used(override val date: LocalDateTime) : FoodEvent
}

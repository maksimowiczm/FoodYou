package com.maksimowiczm.foodyou.mealplan.domain

import kotlin.uuid.Uuid
import kotlinx.datetime.LocalTime
import kotlinx.serialization.Serializable

@Serializable data class MealIdentity(val id: Uuid)

@Serializable
data class Meal(
    val identity: MealIdentity,
    val name: String,
    val timeWindow: TimeWindow,
) {
    init {
        require(name.isNotBlank()) { "Meal name must not be blank" }
    }

    @Serializable
    sealed interface TimeWindow {
        @Serializable data class Range(val start: LocalTime, val end: LocalTime) : TimeWindow

        @Serializable data object AllDay : TimeWindow
    }
}

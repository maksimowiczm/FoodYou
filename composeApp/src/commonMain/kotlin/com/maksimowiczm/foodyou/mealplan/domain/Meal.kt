package com.maksimowiczm.foodyou.mealplan.domain

import kotlin.uuid.Uuid
import kotlinx.datetime.LocalTime
import kotlinx.serialization.Serializable

@Serializable data class MealIdentity(val id: Uuid)

@Serializable
sealed interface Meal {
    val identity: MealIdentity
    val timeWindow: TimeWindow

    @Serializable
    data class Standard(
        override val identity: MealIdentity,
        override val timeWindow: TimeWindow,
        val mealType: MealType,
    ) : Meal

    @Serializable
    data class Custom(
        override val identity: MealIdentity,
        val name: String,
        override val timeWindow: TimeWindow,
    ) : Meal {
        init {
            require(name.isNotBlank()) { "Meal name must not be blank" }
        }
    }

    @Serializable
    sealed interface TimeWindow {
        @Serializable data object AllDay : TimeWindow

        @Serializable data class Range(val from: LocalTime, val until: LocalTime) : TimeWindow
    }
}

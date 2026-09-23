package com.maksimowiczm.foodyou.mealplan.domain

import com.maksimowiczm.foodyou.common.domain.Language
import com.maksimowiczm.foodyou.common.event.DomainEvent
import kotlinx.serialization.Serializable

@Serializable sealed interface MealPlanEvent : DomainEvent

@Serializable
data class MealPlanInitializedEvent(
    val language: Language,
    val meals: List<Meal>,
) : MealPlanEvent

@Serializable data class MealAddedEvent(val meal: Meal) : MealPlanEvent

@Serializable data class MealUpdatedEvent(val meal: Meal) : MealPlanEvent

@Serializable data class MealDeletedEvent(val mealId: MealId) : MealPlanEvent

@Serializable data class MealsReorderedEvent(val order: List<MealId>) : MealPlanEvent

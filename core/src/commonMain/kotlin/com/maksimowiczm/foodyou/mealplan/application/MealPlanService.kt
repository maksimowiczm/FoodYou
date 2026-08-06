package com.maksimowiczm.foodyou.mealplan.application

import com.maksimowiczm.foodyou.common.domain.EventStore
import com.maksimowiczm.foodyou.common.domain.load
import com.maksimowiczm.foodyou.common.domain.observe
import com.maksimowiczm.foodyou.common.event.EventBus
import com.maksimowiczm.foodyou.mealplan.domain.MealPlan
import com.maksimowiczm.foodyou.mealplan.domain.MealPlanEvent
import com.maksimowiczm.foodyou.mealplan.domain.toMealPlan
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class MealPlanService(
    private val eventStore: EventStore,
    private val eventBus: EventBus,
) {
    private val streamId = "MealPlan"

    suspend fun transact(block: (MealPlan) -> List<MealPlanEvent>) {
        val plan = eventStore.load<MealPlanEvent>(streamId).toMealPlan()
        val newEvents = block(plan)
        if (newEvents.isNotEmpty()) {
            eventStore.append(streamId, newEvents)
            eventBus.publish(newEvents)
        }
    }

    fun observe(): Flow<MealPlan> =
        eventStore.observe<MealPlanEvent>(streamId).map { it.toMealPlan() }
}

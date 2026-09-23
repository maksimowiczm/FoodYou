package com.maksimowiczm.foodyou.mealplan.application

import com.maksimowiczm.foodyou.common.event.EventNotifier
import com.maksimowiczm.foodyou.common.event.EventStore
import com.maksimowiczm.foodyou.common.event.asEventSink
import com.maksimowiczm.foodyou.common.event.asHandler
import com.maksimowiczm.foodyou.common.observe
import com.maksimowiczm.foodyou.common.plus
import com.maksimowiczm.foodyou.mealplan.domain.MealPlan
import com.maksimowiczm.foodyou.mealplan.domain.MealPlanCommand
import com.maksimowiczm.foodyou.mealplan.domain.MealPlanEvent
import com.maksimowiczm.foodyou.mealplan.domain.mealPlanDecider
import com.maksimowiczm.foodyou.mealplan.domain.toMealPlan
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class MealPlanService(
    private val eventStore: EventStore,
    eventNotifier: EventNotifier,
) {
    private val commandHandler =
        mealPlanDecider.asHandler(
            eventStore,
            eventStore + eventNotifier.asEventSink(),
        )

    suspend fun handle(command: MealPlanCommand) {
        val _ = commandHandler(STREAM, command)
    }

    fun observe(): Flow<MealPlan?> =
        eventStore.observe<MealPlanEvent>(STREAM).map { events ->
            if (events.none()) null else events.toMealPlan()
        }

    private companion object {
        private const val STREAM = "MealPlan"
    }
}

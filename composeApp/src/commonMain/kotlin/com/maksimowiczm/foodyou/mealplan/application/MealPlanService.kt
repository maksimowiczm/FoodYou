package com.maksimowiczm.foodyou.mealplan.application

import com.maksimowiczm.foodyou.common.domain.DeleteStrategy
import com.maksimowiczm.foodyou.common.domain.EventStore
import com.maksimowiczm.foodyou.common.domain.load
import com.maksimowiczm.foodyou.common.event.EventBus
import com.maksimowiczm.foodyou.mealplan.domain.Meal
import com.maksimowiczm.foodyou.mealplan.domain.MealIdentity
import com.maksimowiczm.foodyou.mealplan.domain.MealPlan
import com.maksimowiczm.foodyou.mealplan.domain.MealPlanEvent
import com.maksimowiczm.foodyou.mealplan.domain.add
import com.maksimowiczm.foodyou.mealplan.domain.edit
import com.maksimowiczm.foodyou.mealplan.domain.remove
import com.maksimowiczm.foodyou.mealplan.domain.reorder
import com.maksimowiczm.foodyou.mealplan.domain.toMealPlan
import kotlin.uuid.Uuid

class MealPlanService(
    private val eventStore: EventStore,
    private val eventBus: EventBus,
) {
    private val streamId = "MealPlan"

    private suspend inline fun transact(block: (MealPlan) -> List<MealPlanEvent>) {
        val plan = eventStore.load<MealPlanEvent>(streamId).toMealPlan()
        val newEvents = block(plan)
        if (newEvents.isNotEmpty()) {
            eventStore.append(streamId, newEvents)
            eventBus.publish(newEvents)
        }
    }

    suspend fun create(
        name: String,
        timeWindow: Meal.TimeWindow,
    ): MealIdentity {
        val identity = MealIdentity(Uuid.random())
        transact { plan ->
            plan.add(
                Meal(
                    identity = identity,
                    name = name,
                    timeWindow = timeWindow,
                )
            )
        }
        return identity
    }

    suspend fun edit(
        identity: MealIdentity,
        name: String?,
        timeWindow: Meal.TimeWindow,
    ) {
        transact { plan ->
            if (name != null) {
                plan.edit(identity, name, timeWindow)
            } else {
                plan.edit(identity, timeWindow)
            }
        }
    }

    suspend fun delete(identity: MealIdentity, strategy: DeleteStrategy) {
        transact { plan ->
            plan.remove(identity, strategy)
        }
    }

    suspend fun reorder(fromIndex: Int, toIndex: Int) {
        transact { plan ->
            plan.reorder(fromIndex, toIndex)
        }
    }
}

package com.maksimowiczm.foodyou.fooddiary.application

import com.maksimowiczm.foodyou.common.event.EventNotifier
import com.maksimowiczm.foodyou.common.event.EventStore
import com.maksimowiczm.foodyou.common.event.asEventSink
import com.maksimowiczm.foodyou.common.event.asHandler
import com.maksimowiczm.foodyou.common.observe
import com.maksimowiczm.foodyou.common.plus
import com.maksimowiczm.foodyou.fooddiary.domain.QuickFoodDiaryEntry
import com.maksimowiczm.foodyou.fooddiary.domain.QuickFoodDiaryEntryCommand
import com.maksimowiczm.foodyou.fooddiary.domain.QuickFoodDiaryEntryEvent
import com.maksimowiczm.foodyou.fooddiary.domain.QuickFoodDiaryEntryId
import com.maksimowiczm.foodyou.fooddiary.domain.QuickFoodDiaryEntryMealRepository
import com.maksimowiczm.foodyou.fooddiary.domain.quickFoodDiaryDecider
import com.maksimowiczm.foodyou.fooddiary.domain.toAnonymousFoodDiaryEntry
import com.maksimowiczm.foodyou.mealplan.domain.MealId
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class QuickFoodDiaryService(
    private val eventStore: EventStore,
    eventNotifier: EventNotifier,
    private val mealRepository: QuickFoodDiaryEntryMealRepository,
) {
    private val commandHandler =
        quickFoodDiaryDecider.asHandler(
            eventStore,
            eventStore + eventNotifier.asEventSink(),
        )

    private fun stream(id: QuickFoodDiaryEntryId) = "QuickFoodDiaryEntry-${id.id}"

    suspend fun handle(id: QuickFoodDiaryEntryId, command: QuickFoodDiaryEntryCommand) {
        val _ = commandHandler(stream(id), command)
    }

    fun observe(id: QuickFoodDiaryEntryId): Flow<QuickFoodDiaryEntry?> =
        eventStore.observe<QuickFoodDiaryEntryEvent>(stream(id)).map {
            it.toAnonymousFoodDiaryEntry()
        }

    suspend fun unlinkEntriesFromMeal(mealId: MealId) = coroutineScope {
        mealRepository
            .findEntriesUsing(mealId)
            .map { entryId ->
                async {
                    handle(
                        id = entryId,
                        command = QuickFoodDiaryEntryCommand.UnlinkFromMeal,
                    )
                }
            }
            .awaitAll()
    }
}

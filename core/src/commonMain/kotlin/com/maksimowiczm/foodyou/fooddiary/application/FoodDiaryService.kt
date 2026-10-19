package com.maksimowiczm.foodyou.fooddiary.application

import com.maksimowiczm.foodyou.common.domain.food.AbsoluteQuantity
import com.maksimowiczm.foodyou.common.domain.food.FoodSnapshotId
import com.maksimowiczm.foodyou.common.domain.food.FoodSnapshotQuantityUpdateService
import com.maksimowiczm.foodyou.common.domain.food.FoodSnapshotUpdateService
import com.maksimowiczm.foodyou.common.domain.food.TrackedFoodSnapshot
import com.maksimowiczm.foodyou.common.event.EventNotifier
import com.maksimowiczm.foodyou.common.event.EventStore
import com.maksimowiczm.foodyou.common.event.asEventSink
import com.maksimowiczm.foodyou.common.event.asHandler
import com.maksimowiczm.foodyou.common.observe
import com.maksimowiczm.foodyou.common.plus
import com.maksimowiczm.foodyou.fooddiary.domain.FoodDiaryEntry
import com.maksimowiczm.foodyou.fooddiary.domain.FoodDiaryEntryCommand
import com.maksimowiczm.foodyou.fooddiary.domain.FoodDiaryEntryCompositionRepository
import com.maksimowiczm.foodyou.fooddiary.domain.FoodDiaryEntryEvent
import com.maksimowiczm.foodyou.fooddiary.domain.FoodDiaryEntryId
import com.maksimowiczm.foodyou.fooddiary.domain.FoodDiaryEntryMealRepository
import com.maksimowiczm.foodyou.fooddiary.domain.foodDiaryDecider
import com.maksimowiczm.foodyou.fooddiary.domain.toFoodDiaryEntry
import com.maksimowiczm.foodyou.mealplan.domain.MealId
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FoodDiaryService(
    private val eventStore: EventStore,
    eventNotifier: EventNotifier,
    private val compositionRepository: FoodDiaryEntryCompositionRepository,
    private val mealRepository: FoodDiaryEntryMealRepository,
) {
    private val commandHandler =
        foodDiaryDecider.asHandler(
            eventStore,
            eventStore + eventNotifier.asEventSink(),
        )

    private fun stream(id: FoodDiaryEntryId) = "FoodDiaryEntry-${id.id}"

    suspend fun handle(id: FoodDiaryEntryId, command: FoodDiaryEntryCommand) {
        val _ = commandHandler(stream(id), command)
    }

    fun observe(id: FoodDiaryEntryId): Flow<FoodDiaryEntry?> =
        eventStore.observe<FoodDiaryEntryEvent>(stream(id)).map { it.toFoodDiaryEntry() }

    /**
     * Updates all food diary entries containing the food identified by [TrackedFoodSnapshot.id]
     * with the new [snapshot]. The quantity is also updated to match new [servingWeight] and
     * [packageWeight].
     */
    suspend fun updateEntriesUsing(
        snapshot: TrackedFoodSnapshot,
        servingQuantity: AbsoluteQuantity? = null,
        packageQuantity: AbsoluteQuantity? = null,
    ) = coroutineScope {
        compositionRepository
            .findEntriesUsing(snapshot.id)
            .map { entryId ->
                async {
                    handle(
                        id = entryId,
                        command =
                            FoodDiaryEntryCommand.Update { entry ->
                                val updated =
                                    FoodSnapshotUpdateService.update(
                                        components = listOf(entry.snapshot),
                                        id = snapshot.id,
                                        transform = { current ->
                                            current.copy(
                                                snapshot = snapshot,
                                                quantity =
                                                    FoodSnapshotQuantityUpdateService.update(
                                                        current = current.quantity,
                                                        servingQuantity = servingQuantity,
                                                        packageQuantity = packageQuantity,
                                                    ),
                                            )
                                        },
                                    )
                                entry.copy(snapshot = updated.first())
                            },
                    )
                }
            }
            .awaitAll()
    }

    suspend fun removeComponentFromEntries(id: FoodSnapshotId.Tracked) = coroutineScope {
        compositionRepository
            .findEntriesUsing(id)
            .map { entryId ->
                async {
                    handle(
                        id = entryId,
                        command =
                            FoodDiaryEntryCommand.Update { entry ->
                                val wrapped = listOf(entry.snapshot)
                                val updatedSnapshot = FoodSnapshotUpdateService.remove(wrapped, id)

                                if (updatedSnapshot.isEmpty()) null
                                else entry.copy(snapshot = updatedSnapshot.first())
                            },
                    )
                }
            }
            .awaitAll()
    }

    suspend fun unlinkComponentFromEntries(id: FoodSnapshotId.Tracked) = coroutineScope {
        compositionRepository
            .findEntriesUsing(id)
            .map { entryId ->
                async {
                    handle(
                        id = entryId,
                        command =
                            FoodDiaryEntryCommand.Update { entry ->
                                val wrapped = listOf(entry.snapshot)
                                val updatedSnapshot = FoodSnapshotUpdateService.unlink(wrapped, id)
                                entry.copy(snapshot = updatedSnapshot.first())
                            },
                    )
                }
            }
            .awaitAll()
    }

    suspend fun unlinkEntriesFromMeal(mealId: MealId) = coroutineScope {
        mealRepository
            .findEntriesUsing(mealId)
            .map { entryId ->
                async {
                    handle(
                        id = entryId,
                        command = FoodDiaryEntryCommand.UnlinkFromMeal,
                    )
                }
            }
            .awaitAll()
    }
}

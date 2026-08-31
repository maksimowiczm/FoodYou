package com.maksimowiczm.foodyou.fooddiary.application

import com.maksimowiczm.foodyou.common.domain.DeleteStrategy
import com.maksimowiczm.foodyou.common.domain.EventStore
import com.maksimowiczm.foodyou.common.domain.ProfileId
import com.maksimowiczm.foodyou.common.domain.Weight
import com.maksimowiczm.foodyou.common.domain.food.FoodSnapshotId
import com.maksimowiczm.foodyou.common.domain.food.FoodSnapshotQuantity
import com.maksimowiczm.foodyou.common.domain.food.FoodSnapshotQuantityUpdateService
import com.maksimowiczm.foodyou.common.domain.food.FoodSnapshotUpdateService
import com.maksimowiczm.foodyou.common.domain.food.MeasuredFoodSnapshot
import com.maksimowiczm.foodyou.common.domain.food.TrackedFoodSnapshot
import com.maksimowiczm.foodyou.common.domain.load
import com.maksimowiczm.foodyou.common.domain.observe
import com.maksimowiczm.foodyou.common.event.EventBus
import com.maksimowiczm.foodyou.fooddiary.domain.FoodDiaryCompositionRepository
import com.maksimowiczm.foodyou.fooddiary.domain.FoodDiaryEntry
import com.maksimowiczm.foodyou.fooddiary.domain.FoodDiaryEntryId
import com.maksimowiczm.foodyou.fooddiary.domain.FoodDiaryEvent
import com.maksimowiczm.foodyou.fooddiary.domain.FoodDiaryMealRepository
import com.maksimowiczm.foodyou.fooddiary.domain.edit
import com.maksimowiczm.foodyou.fooddiary.domain.remove
import com.maksimowiczm.foodyou.fooddiary.domain.toFoodDiaryEntry
import com.maksimowiczm.foodyou.fooddiary.domain.unlinkFromMeal
import com.maksimowiczm.foodyou.mealplan.domain.MealId
import kotlin.time.Instant
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FoodDiaryService(
    private val eventStore: EventStore,
    private val eventBus: EventBus,
    private val compositionRepository: FoodDiaryCompositionRepository,
    private val mealRepository: FoodDiaryMealRepository,
) {
    private fun streamId(id: FoodDiaryEntryId) = "FoodDiaryEntry-${id.id}"

    private suspend inline fun transact(
        id: FoodDiaryEntryId,
        block: (FoodDiaryEntry?) -> List<FoodDiaryEvent>,
    ) {
        val entry = eventStore.load<FoodDiaryEvent>(streamId(id)).toFoodDiaryEntry()
        val newEvents = block(entry)
        if (newEvents.isNotEmpty()) {
            eventStore.append(streamId(id), newEvents)
            eventBus.publish(newEvents)
        }
    }

    fun observe(id: FoodDiaryEntryId): Flow<FoodDiaryEntry?> =
        eventStore.observe<FoodDiaryEvent>(streamId(id)).map { it.toFoodDiaryEntry() }

    suspend fun create(
        profileIds: Set<ProfileId>,
        snapshot: MeasuredFoodSnapshot,
        mealId: MealId,
        timestamp: Instant,
    ): FoodDiaryEntryId {
        val id = FoodDiaryEntryId()
        transact(id) {
            FoodDiaryEntry.create(
                id = id,
                profileIds = profileIds,
                snapshot = snapshot,
                mealId = mealId,
                timestamp = timestamp,
            )
        }
        return id
    }

    suspend fun edit(
        id: FoodDiaryEntryId,
        profileIds: Set<ProfileId>,
        quantity: FoodSnapshotQuantity,
        timestamp: Instant,
    ) {
        transact(id) { entry ->
            checkNotNull(entry) { "Food diary entry with ID $id not found" }
            entry.edit(
                profileIds = profileIds,
                snapshot = entry.snapshot.withNewQuantity(quantity),
                timestamp = timestamp,
            )
        }
    }

    suspend fun delete(id: FoodDiaryEntryId, strategy: DeleteStrategy) {
        transact(id) { entry ->
            checkNotNull(entry) { "Food diary entry with ID $id not found" }
            entry.remove(strategy)
        }
    }

    /**
     * Updates all food diary entries containing the food identified by [TrackedFoodSnapshot.id]
     * with the new [snapshot]. The quantity is also updated to match new [servingWeight] and
     * [packageWeight].
     */
    suspend fun updateEntriesUsing(
        snapshot: TrackedFoodSnapshot,
        servingWeight: Weight? = null,
        packageWeight: Weight? = null,
    ) = coroutineScope {
        compositionRepository
            .findEntriesUsing(snapshot.id)
            .map { entryId ->
                async {
                    transact(entryId) { entry ->
                        if (entry == null) return@transact emptyList()
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
                                                servingWeight = servingWeight,
                                                packageWeight = packageWeight,
                                            ),
                                    )
                                },
                            )
                        entry.edit(snapshot = updated.first())
                    }
                }
            }
            .awaitAll()
    }

    suspend fun removeComponentFromEntries(id: FoodSnapshotId.Tracked) = coroutineScope {
        compositionRepository
            .findEntriesUsing(id)
            .map { entryId ->
                async {
                    transact(entryId) { entry ->
                        if (entry == null) return@transact emptyList()
                        val wrapped = listOf(entry.snapshot)
                        val updatedSnapshot = FoodSnapshotUpdateService.remove(wrapped, id)

                        if (updatedSnapshot.isEmpty()) entry.remove(DeleteStrategy.Delete)
                        else entry.edit(snapshot = updatedSnapshot.first())
                    }
                }
            }
            .awaitAll()
    }

    suspend fun unlinkComponentFromEntries(id: FoodSnapshotId.Tracked) = coroutineScope {
        compositionRepository
            .findEntriesUsing(id)
            .map { entryId ->
                async {
                    transact(entryId) { entry ->
                        entry ?: return@transact emptyList()
                        val wrapped = listOf(entry.snapshot)
                        val updatedSnapshot = FoodSnapshotUpdateService.unlink(wrapped, id)
                        entry.edit(snapshot = updatedSnapshot.first())
                    }
                }
            }
            .awaitAll()
    }

    suspend fun unlinkEntriesFromMeal(mealId: MealId) = coroutineScope {
        mealRepository
            .findEntriesUsing(mealId)
            .map { entryId ->
                async {
                    transact(entryId) { entry ->
                        entry?.unlinkFromMeal() ?: emptyList()
                    }
                }
            }
            .awaitAll()
    }
}

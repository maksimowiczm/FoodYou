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
import com.maksimowiczm.foodyou.fooddiary.domain.FoodDiaryEntryIdentity
import com.maksimowiczm.foodyou.fooddiary.domain.FoodDiaryEvent
import com.maksimowiczm.foodyou.fooddiary.domain.FoodDiaryMealRepository
import com.maksimowiczm.foodyou.fooddiary.domain.edit
import com.maksimowiczm.foodyou.fooddiary.domain.remove
import com.maksimowiczm.foodyou.fooddiary.domain.toFoodDiaryEntry
import com.maksimowiczm.foodyou.fooddiary.domain.unlinkFromMeal
import com.maksimowiczm.foodyou.mealplan.domain.MealIdentity
import kotlin.time.Instant
import kotlin.uuid.Uuid
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
    private fun streamId(identity: FoodDiaryEntryIdentity) = "FoodDiaryEntry-${identity.id}"

    private suspend inline fun transact(
        identity: FoodDiaryEntryIdentity,
        block: (FoodDiaryEntry?) -> List<FoodDiaryEvent>,
    ) {
        val entry = eventStore.load<FoodDiaryEvent>(streamId(identity)).toFoodDiaryEntry()
        val newEvents = block(entry)
        if (newEvents.isNotEmpty()) {
            eventStore.append(streamId(identity), newEvents)
            eventBus.publish(newEvents)
        }
    }

    fun observe(identity: FoodDiaryEntryIdentity): Flow<FoodDiaryEntry?> =
        eventStore.observe<FoodDiaryEvent>(streamId(identity)).map { it.toFoodDiaryEntry() }

    suspend fun create(
        profileIds: Set<ProfileId>,
        composition: MeasuredFoodSnapshot,
        mealIdentity: MealIdentity,
        timestamp: Instant,
    ): FoodDiaryEntryIdentity {
        val identity = FoodDiaryEntryIdentity(Uuid.random())
        transact(identity) {
            FoodDiaryEntry.create(
                identity = identity,
                profileIds = profileIds,
                composition = composition,
                mealIdentity = mealIdentity,
                timestamp = timestamp,
            )
        }
        return identity
    }

    suspend fun edit(
        identity: FoodDiaryEntryIdentity,
        profileIds: Set<ProfileId>,
        quantity: FoodSnapshotQuantity,
        timestamp: Instant,
    ) {
        transact(identity) { entry ->
            checkNotNull(entry) { "Food diary entry with ID $identity not found" }
            entry.edit(
                profileIds = profileIds,
                composition = entry.composition.withNewQuantity(quantity),
                timestamp = timestamp,
            )
        }
    }

    suspend fun delete(identity: FoodDiaryEntryIdentity, strategy: DeleteStrategy) {
        transact(identity) { entry ->
            checkNotNull(entry) { "Food diary entry with ID $identity not found" }
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
            .map { entryIdentity ->
                async {
                    transact(entryIdentity) { entry ->
                        if (entry == null) return@transact emptyList()
                        val updated =
                            FoodSnapshotUpdateService.update(
                                components = listOf(entry.composition),
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
                        entry.edit(composition = updated.first())
                    }
                }
            }
            .awaitAll()
    }

    suspend fun removeComponentFromEntries(identity: FoodSnapshotId.Tracked) = coroutineScope {
        compositionRepository
            .findEntriesUsing(identity)
            .map { entryIdentity ->
                async {
                    transact(entryIdentity) { entry ->
                        if (entry == null) return@transact emptyList()
                        val wrapped = listOf(entry.composition)
                        val updatedComposition = FoodSnapshotUpdateService.remove(wrapped, identity)

                        if (updatedComposition.isEmpty()) entry.remove(DeleteStrategy.Delete)
                        else entry.edit(composition = updatedComposition.first())
                    }
                }
            }
            .awaitAll()
    }

    suspend fun unlinkComponentFromEntries(identity: FoodSnapshotId.Tracked) = coroutineScope {
        compositionRepository
            .findEntriesUsing(identity)
            .map { entryIdentity ->
                async {
                    transact(entryIdentity) { entry ->
                        entry ?: return@transact emptyList()
                        val wrapped = listOf(entry.composition)
                        val updatedComposition = FoodSnapshotUpdateService.unlink(wrapped, identity)
                        entry.edit(composition = updatedComposition.first())
                    }
                }
            }
            .awaitAll()
    }

    suspend fun unlinkEntriesFromMeal(mealIdentity: MealIdentity) = coroutineScope {
        mealRepository
            .findEntriesUsing(mealIdentity)
            .map { entryIdentity ->
                async {
                    transact(entryIdentity) { entry ->
                        entry?.unlinkFromMeal() ?: emptyList()
                    }
                }
            }
            .awaitAll()
    }
}

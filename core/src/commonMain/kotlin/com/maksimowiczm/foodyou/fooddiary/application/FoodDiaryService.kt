package com.maksimowiczm.foodyou.fooddiary.application

import com.maksimowiczm.foodyou.common.domain.DeleteStrategy
import com.maksimowiczm.foodyou.common.domain.EventStore
import com.maksimowiczm.foodyou.common.domain.ProfileId
import com.maksimowiczm.foodyou.common.domain.Weight
import com.maksimowiczm.foodyou.common.domain.food.FoodComponentComponentQuantity
import com.maksimowiczm.foodyou.common.domain.food.FoodComponentQuantityUpdateService
import com.maksimowiczm.foodyou.common.domain.food.FoodCompositionComponent
import com.maksimowiczm.foodyou.common.domain.food.FoodCompositionComponentIdentity
import com.maksimowiczm.foodyou.common.domain.food.FoodCompositionComponentImage
import com.maksimowiczm.foodyou.common.domain.food.FoodCompositionUpdateService
import com.maksimowiczm.foodyou.common.domain.food.FoodName
import com.maksimowiczm.foodyou.common.domain.food.NutritionFacts
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
        composition: FoodCompositionComponent,
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
        quantity: FoodComponentComponentQuantity,
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

    suspend fun updateEntriesWithComponent(
        identity: FoodCompositionComponentIdentity.Leaf,
        name: FoodName,
        nutritionFacts: NutritionFacts,
        servingWeight: Weight? = null,
        packageWeight: Weight? = null,
        image: FoodCompositionComponentImage? = null,
    ) = coroutineScope {
        compositionRepository
            .findEntriesUsing(identity)
            .map { entryIdentity ->
                async {
                    transact(entryIdentity) { entry ->
                        if (entry == null) return@transact emptyList()
                        val wrapped = listOf(entry.composition)
                        val updated =
                            FoodCompositionUpdateService.update(
                                components = wrapped,
                                identity = identity,
                                name = name,
                                nutritionFacts = nutritionFacts,
                                quantity = { current ->
                                    FoodComponentQuantityUpdateService.update(
                                        current = current,
                                        servingWeight = servingWeight,
                                        packageWeight = packageWeight,
                                    )
                                },
                                image = image,
                            )
                        entry.edit(composition = updated.first())
                    }
                }
            }
            .awaitAll()
    }

    suspend fun updateEntriesWithRecipe(
        identity: FoodCompositionComponentIdentity.Composite,
        name: FoodName,
        components: List<FoodCompositionComponent>,
        servingWeight: Weight? = null,
        packageWeight: Weight? = null,
        image: FoodCompositionComponentImage? = null,
    ) = coroutineScope {
        compositionRepository
            .findEntriesUsing(identity)
            .map { entryIdentity ->
                async {
                    transact(entryIdentity) { entry ->
                        if (entry == null) return@transact emptyList()
                        val wrapped = listOf(entry.composition)
                        val updated =
                            FoodCompositionUpdateService.update(
                                components = wrapped,
                                identity = identity,
                                name = name,
                                newComponents = components,
                                quantity = { current ->
                                    FoodComponentQuantityUpdateService.update(
                                        current = current,
                                        servingWeight = servingWeight,
                                        packageWeight = packageWeight,
                                    )
                                },
                                image = image,
                            )
                        entry.edit(composition = updated.first())
                    }
                }
            }
            .awaitAll()
    }

    suspend fun removeComponentFromEntries(identity: FoodCompositionComponentIdentity.Identified) =
        coroutineScope {
            compositionRepository
                .findEntriesUsing(identity)
                .map { entryIdentity ->
                    async {
                        transact(entryIdentity) { entry ->
                            if (entry == null) return@transact emptyList()
                            val wrapped = listOf(entry.composition)
                            val updatedComposition =
                                FoodCompositionUpdateService.remove(wrapped, identity)

                            if (updatedComposition.isEmpty()) entry.remove(DeleteStrategy.Delete)
                            else entry.edit(composition = updatedComposition.first())
                        }
                    }
                }
                .awaitAll()
        }

    suspend fun unlinkComponentFromEntries(identity: FoodCompositionComponentIdentity.Identified) =
        coroutineScope {
            compositionRepository
                .findEntriesUsing(identity)
                .map { entryIdentity ->
                    async {
                        transact(entryIdentity) { entry ->
                            entry ?: return@transact emptyList()
                            val wrapped = listOf(entry.composition)
                            val updatedComposition =
                                FoodCompositionUpdateService.unlink(wrapped, identity)
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

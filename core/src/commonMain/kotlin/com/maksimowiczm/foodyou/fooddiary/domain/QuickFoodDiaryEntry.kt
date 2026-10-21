@file:MustUseReturnValues

package com.maksimowiczm.foodyou.fooddiary.domain

import com.maksimowiczm.foodyou.common.Decider
import com.maksimowiczm.foodyou.common.domain.ProfileId
import com.maksimowiczm.foodyou.common.domain.food.NutritionFacts
import com.maksimowiczm.foodyou.mealplan.domain.MealId
import kotlin.time.Instant
import kotlin.uuid.Uuid
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.InstantComponentSerializer

@Serializable data class QuickFoodDiaryEntryId(val id: Uuid = Uuid.random())

@Serializable
data class QuickFoodDiaryEntry(
    val id: QuickFoodDiaryEntryId,
    val profileIds: Set<ProfileId>,
    val name: String,
    val nutritionFacts: NutritionFacts,
    @Serializable(with = InstantComponentSerializer::class) val timestamp: Instant,
    val mealId: MealId?,
)

fun QuickFoodDiaryEntry?.decide(
    command: QuickFoodDiaryEntryCommand
): List<QuickFoodDiaryEntryEvent> =
    when (command) {
        is QuickFoodDiaryEntryCommand.Create ->
            if (this == null) {
                require(command.profileIds.isNotEmpty()) {
                    "Quick food diary entry must have at least one profile"
                }
                require(command.name.isNotBlank()) {
                    "Quick food diary entry must have a name"
                }
                listOf(
                    QuickFoodDiaryEntryCreatedEntryEvent(
                        diaryEntryId = command.id,
                        profileIds = command.profileIds,
                        name = command.name,
                        nutritionFacts = command.nutritionFacts,
                        mealId = command.mealId,
                        entryTimestamp = command.entryTimestamp,
                    )
                )
            } else {
                emptyList()
            }

        is QuickFoodDiaryEntryCommand.ImportFoodYou3 ->
            if (this == null) {
                listOf(
                    QuickFoodDiaryEntryImportedFoodYou3Event(
                        diaryEntryId = command.id,
                        profileId = command.profileId,
                        name = command.name,
                        nutritionFacts = command.nutritionFacts,
                        mealId = command.mealId,
                        entryDate = command.entryDate,
                    )
                )
            } else {
                emptyList()
            }

        is QuickFoodDiaryEntryCommand.Update ->
            this?.let { entry ->
                val updated = command.transform(entry)
                if (updated == null)
                    listOf(QuickFoodDiaryEntryDeletedEntryEvent(diaryEntryId = entry.id))
                else {
                    require(updated.profileIds.isNotEmpty()) {
                        "Quick food diary entry must have at least one profile"
                    }
                    buildList {
                        if (updated.profileIds != entry.profileIds) {
                            add(
                                QuickFoodDiaryEntryProfileIdsChangedEntryEvent(
                                    diaryEntryId = entry.id,
                                    profileIds = updated.profileIds,
                                )
                            )
                        }
                        if (updated.name != entry.name) {
                            add(
                                QuickFoodDiaryEntryNameChangedEntryEvent(
                                    diaryEntryId = entry.id,
                                    name = updated.name,
                                )
                            )
                        }
                        if (updated.nutritionFacts != entry.nutritionFacts) {
                            add(
                                QuickFoodDiaryEntryNutritionFactsChangedEntryEvent(
                                    diaryEntryId = entry.id,
                                    nutritionFacts = updated.nutritionFacts,
                                )
                            )
                        }
                        if (updated.timestamp != entry.timestamp) {
                            add(
                                QuickFoodDiaryEntryTimestampChangedEntryEvent(
                                    diaryEntryId = entry.id,
                                    entryTimestamp = updated.timestamp,
                                )
                            )
                        }
                        if (updated.mealId != entry.mealId) {
                            if (updated.mealId != null) {
                                add(
                                    QuickFoodDiaryEntryMealLinkedEntryEvent(
                                        diaryEntryId = entry.id,
                                        mealId = updated.mealId,
                                    )
                                )
                            } else {
                                add(
                                    QuickFoodDiaryEntryMealUnlinkedEntryEvent(
                                        diaryEntryId = entry.id
                                    )
                                )
                            }
                        }
                    }
                }
            } ?: emptyList()

        is QuickFoodDiaryEntryCommand.LinkToMeal ->
            this?.let { entry ->
                listOf(
                    QuickFoodDiaryEntryMealLinkedEntryEvent(
                        diaryEntryId = entry.id,
                        mealId = command.mealId,
                    )
                )
            } ?: emptyList()

        is QuickFoodDiaryEntryCommand.UnlinkFromMeal ->
            this?.let { entry ->
                listOf(QuickFoodDiaryEntryMealUnlinkedEntryEvent(diaryEntryId = entry.id))
            } ?: emptyList()
    }

fun QuickFoodDiaryEntry?.apply(event: QuickFoodDiaryEntryEvent): QuickFoodDiaryEntry? =
    when (event) {
        is QuickFoodDiaryEntryCreatedEntryEvent ->
            QuickFoodDiaryEntry(
                id = event.diaryEntryId,
                profileIds = event.profileIds,
                name = event.name,
                nutritionFacts = event.nutritionFacts,
                timestamp = event.entryTimestamp,
                mealId = event.mealId,
            )

        is QuickFoodDiaryEntryImportedFoodYou3Event ->
            QuickFoodDiaryEntry(
                id = event.diaryEntryId,
                profileIds = setOf(event.profileId),
                name = event.name,
                nutritionFacts = event.nutritionFacts,
                timestamp = event.entryDate.atStartOfDayIn(TimeZone.currentSystemDefault()),
                mealId = event.mealId,
            )

        is QuickFoodDiaryEntryProfileIdsChangedEntryEvent ->
            this?.copy(profileIds = event.profileIds)

        is QuickFoodDiaryEntryNameChangedEntryEvent -> this?.copy(name = event.name)

        is QuickFoodDiaryEntryNutritionFactsChangedEntryEvent ->
            this?.copy(nutritionFacts = event.nutritionFacts)

        is QuickFoodDiaryEntryTimestampChangedEntryEvent ->
            this?.copy(timestamp = event.entryTimestamp)

        is QuickFoodDiaryEntryMealLinkedEntryEvent -> this?.copy(mealId = event.mealId)

        is QuickFoodDiaryEntryMealUnlinkedEntryEvent -> this?.copy(mealId = null)

        is QuickFoodDiaryEntryDeletedEntryEvent -> null
    }

fun Iterable<QuickFoodDiaryEntryEvent>.toAnonymousFoodDiaryEntry(): QuickFoodDiaryEntry? =
    fold(null) { state, event -> state.apply(event) }

val quickFoodDiaryDecider =
    Decider<QuickFoodDiaryEntryCommand, QuickFoodDiaryEntryEvent, QuickFoodDiaryEntry?>(
        decide = { command, state -> state.decide(command) },
        evolve = { state, event -> state.apply(event) },
        initialState = null,
    )

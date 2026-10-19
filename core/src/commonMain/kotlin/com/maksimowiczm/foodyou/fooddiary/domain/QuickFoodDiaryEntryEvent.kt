package com.maksimowiczm.foodyou.fooddiary.domain

import com.maksimowiczm.foodyou.common.domain.ProfileId
import com.maksimowiczm.foodyou.common.domain.food.NutritionFacts
import com.maksimowiczm.foodyou.common.event.DomainEvent
import com.maksimowiczm.foodyou.mealplan.domain.MealId
import kotlin.time.Instant
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.InstantComponentSerializer

@Serializable sealed interface QuickFoodDiaryEntryEvent : DomainEvent

@Serializable
data class QuickFoodDiaryEntryCreatedEntryEvent(
    val diaryEntryId: QuickFoodDiaryEntryId,
    val profileIds: Set<ProfileId>,
    val name: String,
    val nutritionFacts: NutritionFacts,
    val mealId: MealId,
    @Serializable(with = InstantComponentSerializer::class) val entryTimestamp: Instant,
) : QuickFoodDiaryEntryEvent

@Serializable
data class QuickFoodDiaryEntryProfileIdsChangedEntryEvent(
    val diaryEntryId: QuickFoodDiaryEntryId,
    val profileIds: Set<ProfileId>,
) : QuickFoodDiaryEntryEvent

@Serializable
data class QuickFoodDiaryEntryNameChangedEntryEvent(
    val diaryEntryId: QuickFoodDiaryEntryId,
    val name: String,
) : QuickFoodDiaryEntryEvent

@Serializable
data class QuickFoodDiaryEntryNutritionFactsChangedEntryEvent(
    val diaryEntryId: QuickFoodDiaryEntryId,
    val nutritionFacts: NutritionFacts,
) : QuickFoodDiaryEntryEvent

@Serializable
data class QuickFoodDiaryEntryTimestampChangedEntryEvent(
    val diaryEntryId: QuickFoodDiaryEntryId,
    @Serializable(with = InstantComponentSerializer::class) val entryTimestamp: Instant,
) : QuickFoodDiaryEntryEvent

@Serializable
data class QuickFoodDiaryEntryMealLinkedEntryEvent(
    val diaryEntryId: QuickFoodDiaryEntryId,
    val mealId: MealId,
) : QuickFoodDiaryEntryEvent

@Serializable
data class QuickFoodDiaryEntryMealUnlinkedEntryEvent(val diaryEntryId: QuickFoodDiaryEntryId) :
    QuickFoodDiaryEntryEvent

@Serializable
data class QuickFoodDiaryEntryDeletedEntryEvent(val diaryEntryId: QuickFoodDiaryEntryId) :
    QuickFoodDiaryEntryEvent

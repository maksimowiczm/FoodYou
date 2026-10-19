package com.maksimowiczm.foodyou.features.home.integration

import com.maksimowiczm.foodyou.common.event.EventHandler
import com.maksimowiczm.foodyou.fooddiary.domain.QuickFoodDiaryEntryCreatedEntryEvent
import com.maksimowiczm.foodyou.fooddiary.domain.QuickFoodDiaryEntryDeletedEntryEvent
import com.maksimowiczm.foodyou.fooddiary.domain.QuickFoodDiaryEntryEvent
import com.maksimowiczm.foodyou.fooddiary.domain.QuickFoodDiaryEntryMealLinkedEntryEvent
import com.maksimowiczm.foodyou.fooddiary.domain.QuickFoodDiaryEntryMealUnlinkedEntryEvent
import com.maksimowiczm.foodyou.fooddiary.domain.QuickFoodDiaryEntryNameChangedEntryEvent
import com.maksimowiczm.foodyou.fooddiary.domain.QuickFoodDiaryEntryNutritionFactsChangedEntryEvent
import com.maksimowiczm.foodyou.fooddiary.domain.QuickFoodDiaryEntryProfileIdsChangedEntryEvent
import com.maksimowiczm.foodyou.fooddiary.domain.QuickFoodDiaryEntryTimestampChangedEntryEvent
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class QuickHomeProjection(private val homeDao: HomeDao) : EventHandler<QuickFoodDiaryEntryEvent> {
    override suspend fun handle(event: QuickFoodDiaryEntryEvent) {
        when (event) {
            is QuickFoodDiaryEntryCreatedEntryEvent -> {
                val dt = event.entryTimestamp.toLocalDateTime(TimeZone.currentSystemDefault())
                homeDao.replaceEntries(
                    entryId = event.diaryEntryId.id,
                    entities =
                        event.profileIds.map {
                            HomeEntryEntity(
                                entryId = event.diaryEntryId.id,
                                profileId = it.value,
                                mealId = event.mealId.value,
                                date = dt.date,
                                time = dt.time,
                                payload =
                                    HomeEntryPayload.QuickAdd(
                                        name = event.name,
                                        nutritionFacts = event.nutritionFacts,
                                    ),
                            )
                        },
                )
            }

            is QuickFoodDiaryEntryProfileIdsChangedEntryEvent ->
                homeDao.replaceProfiles(
                    entryId = event.diaryEntryId.id,
                    profileIds = event.profileIds.map { it.value }.toSet(),
                )

            is QuickFoodDiaryEntryNameChangedEntryEvent ->
                homeDao.updateEach(event.diaryEntryId.id) { entity ->
                    val payload =
                        entity.payload as? HomeEntryPayload.QuickAdd ?: return@updateEach entity
                    entity.copy(payload = payload.copy(name = event.name))
                }

            is QuickFoodDiaryEntryNutritionFactsChangedEntryEvent ->
                homeDao.updateEach(event.diaryEntryId.id) { entity ->
                    val payload =
                        entity.payload as? HomeEntryPayload.QuickAdd ?: return@updateEach entity
                    entity.copy(payload = payload.copy(nutritionFacts = event.nutritionFacts))
                }

            is QuickFoodDiaryEntryTimestampChangedEntryEvent -> {
                val dt = event.entryTimestamp.toLocalDateTime(TimeZone.currentSystemDefault())
                homeDao.updateTimestamp(
                    entryId = event.diaryEntryId.id,
                    date = dt.date,
                    time = dt.time,
                )
            }

            is QuickFoodDiaryEntryMealLinkedEntryEvent ->
                homeDao.updateMealId(
                    entryId = event.diaryEntryId.id,
                    mealId = event.mealId.value,
                )

            is QuickFoodDiaryEntryMealUnlinkedEntryEvent ->
                homeDao.clearMealId(event.diaryEntryId.id)

            is QuickFoodDiaryEntryDeletedEntryEvent -> homeDao.delete(event.diaryEntryId.id)
        }
    }
}

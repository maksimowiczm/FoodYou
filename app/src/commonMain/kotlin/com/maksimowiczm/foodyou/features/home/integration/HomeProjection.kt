package com.maksimowiczm.foodyou.features.home.integration

import com.maksimowiczm.foodyou.common.domain.DeleteStrategy
import com.maksimowiczm.foodyou.common.event.EventHandler
import com.maksimowiczm.foodyou.fooddiary.domain.FoodDiaryEntryCreatedEvent
import com.maksimowiczm.foodyou.fooddiary.domain.FoodDiaryEntryDeletedEvent
import com.maksimowiczm.foodyou.fooddiary.domain.FoodDiaryEntryUnlinkedFromMealEvent
import com.maksimowiczm.foodyou.fooddiary.domain.FoodDiaryEntryUpdatedEvent
import com.maksimowiczm.foodyou.fooddiary.domain.FoodDiaryEvent
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class HomeProjection(private val homeDao: HomeDao) : EventHandler<FoodDiaryEvent> {
    override suspend fun handle(event: FoodDiaryEvent) {
        when (event) {
            is FoodDiaryEntryCreatedEvent -> {
                val dt = event.entryTimestamp.toLocalDateTime(TimeZone.currentSystemDefault())
                homeDao.replaceEntries(
                    entryId = event.identity.id,
                    entities =
                        event.profileIds.map {
                            HomeEntryEntity(
                                entryId = event.identity.id,
                                profileId = it.value,
                                mealId = event.mealIdentity.id,
                                date = dt.date,
                                time = dt.time,
                                composition = event.composition,
                            )
                        },
                )
            }

            is FoodDiaryEntryUpdatedEvent -> {
                val dt = event.entryTimestamp.toLocalDateTime(TimeZone.currentSystemDefault())
                homeDao.replaceEntries(
                    entryId = event.identity.id,
                    entities =
                        event.profileIds.map {
                            HomeEntryEntity(
                                entryId = event.identity.id,
                                profileId = it.value,
                                mealId = event.mealIdentity?.id,
                                date = dt.date,
                                time = dt.time,
                                composition = event.composition,
                            )
                        },
                )
            }

            is FoodDiaryEntryDeletedEvent ->
                if (event.strategy == DeleteStrategy.Delete) {
                    homeDao.delete(event.identity.id)
                } else {
                    // Unlink strategy - entry becomes anonymous
                    homeDao.updateEach(event.identity.id) {
                        it.copy(composition = it.composition.anonymize())
                    }
                }

            is FoodDiaryEntryUnlinkedFromMealEvent -> homeDao.clearMealId(event.identity.id)
        }
    }
}

package com.maksimowiczm.foodyou.features.home.integration

import com.maksimowiczm.foodyou.common.event.EventHandler
import com.maksimowiczm.foodyou.fooddiary.domain.FoodDiaryEntryAnonymizedEvent
import com.maksimowiczm.foodyou.fooddiary.domain.FoodDiaryEntryCreatedEvent
import com.maksimowiczm.foodyou.fooddiary.domain.FoodDiaryEntryDeletedEvent
import com.maksimowiczm.foodyou.fooddiary.domain.FoodDiaryEntryMealLinkedEvent
import com.maksimowiczm.foodyou.fooddiary.domain.FoodDiaryEntryMealUnlinkedEvent
import com.maksimowiczm.foodyou.fooddiary.domain.FoodDiaryEntryProfileIdsChangedEvent
import com.maksimowiczm.foodyou.fooddiary.domain.FoodDiaryEntrySnapshotChangedEvent
import com.maksimowiczm.foodyou.fooddiary.domain.FoodDiaryEntryTimestampChangedEvent
import com.maksimowiczm.foodyou.fooddiary.domain.FoodDiaryEvent
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class HomeProjection(private val homeDao: HomeDao) : EventHandler<FoodDiaryEvent> {
    override suspend fun handle(event: FoodDiaryEvent) {
        when (event) {
            is FoodDiaryEntryCreatedEvent -> {
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
                                snapshot = event.snapshot,
                            )
                        },
                )
            }

            is FoodDiaryEntryProfileIdsChangedEvent ->
                homeDao.replaceProfiles(
                    entryId = event.diaryEntryId.id,
                    profileIds = event.profileIds.map { it.value }.toSet(),
                )

            is FoodDiaryEntrySnapshotChangedEvent ->
                homeDao.updateSnapshot(
                    entryId = event.diaryEntryId.id,
                    snapshot = event.snapshot,
                )

            is FoodDiaryEntryTimestampChangedEvent -> {
                val dt = event.entryTimestamp.toLocalDateTime(TimeZone.currentSystemDefault())
                homeDao.updateTimestamp(
                    entryId = event.diaryEntryId.id,
                    date = dt.date,
                    time = dt.time,
                )
            }

            is FoodDiaryEntryMealLinkedEvent ->
                homeDao.updateMealId(
                    entryId = event.diaryEntryId.id,
                    mealId = event.mealId.value,
                )

            is FoodDiaryEntryMealUnlinkedEvent -> homeDao.clearMealId(event.diaryEntryId.id)

            is FoodDiaryEntryAnonymizedEvent ->
                homeDao.updateEach(event.diaryEntryId.id) {
                    it.copy(snapshot = it.snapshot.anonymize())
                }

            is FoodDiaryEntryDeletedEvent -> homeDao.delete(event.diaryEntryId.id)
        }
    }
}

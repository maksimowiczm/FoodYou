package com.maksimowiczm.foodyou.features.home.integration

import com.maksimowiczm.foodyou.common.event.EventHandler
import com.maksimowiczm.foodyou.fooddiary.domain.FoodDiaryEntryAnonymizedEntryEvent
import com.maksimowiczm.foodyou.fooddiary.domain.FoodDiaryEntryCreatedEntryEvent
import com.maksimowiczm.foodyou.fooddiary.domain.FoodDiaryEntryDeletedEntryEvent
import com.maksimowiczm.foodyou.fooddiary.domain.FoodDiaryEntryEvent
import com.maksimowiczm.foodyou.fooddiary.domain.FoodDiaryEntryMealLinkedEntryEvent
import com.maksimowiczm.foodyou.fooddiary.domain.FoodDiaryEntryMealUnlinkedEntryEvent
import com.maksimowiczm.foodyou.fooddiary.domain.FoodDiaryEntryProfileIdsChangedEntryEvent
import com.maksimowiczm.foodyou.fooddiary.domain.FoodDiaryEntrySnapshotChangedEntryEvent
import com.maksimowiczm.foodyou.fooddiary.domain.FoodDiaryEntryTimestampChangedEntryEvent
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class HomeProjection(private val homeDao: HomeDao) : EventHandler<FoodDiaryEntryEvent> {
    override suspend fun handle(event: FoodDiaryEntryEvent) {
        when (event) {
            is FoodDiaryEntryCreatedEntryEvent -> {
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
                                payload = HomeEntryPayload.FoodDiaryEntry(event.snapshot),
                            )
                        },
                )
            }

            is FoodDiaryEntryProfileIdsChangedEntryEvent ->
                homeDao.replaceProfiles(
                    entryId = event.diaryEntryId.id,
                    profileIds = event.profileIds.map { it.value }.toSet(),
                )

            is FoodDiaryEntrySnapshotChangedEntryEvent ->
                homeDao.updateEach(event.diaryEntryId.id) { entity ->
                    val payload =
                        entity.payload as? HomeEntryPayload.FoodDiaryEntry
                            ?: return@updateEach entity
                    entity.copy(payload = payload.copy(snapshot = event.snapshot))
                }

            is FoodDiaryEntryTimestampChangedEntryEvent -> {
                val dt = event.entryTimestamp.toLocalDateTime(TimeZone.currentSystemDefault())
                homeDao.updateTimestamp(
                    entryId = event.diaryEntryId.id,
                    date = dt.date,
                    time = dt.time,
                )
            }

            is FoodDiaryEntryMealLinkedEntryEvent ->
                homeDao.updateMealId(
                    entryId = event.diaryEntryId.id,
                    mealId = event.mealId.value,
                )

            is FoodDiaryEntryMealUnlinkedEntryEvent -> homeDao.clearMealId(event.diaryEntryId.id)

            is FoodDiaryEntryAnonymizedEntryEvent ->
                homeDao.updateEach(event.diaryEntryId.id) { entity ->
                    val payload =
                        entity.payload as? HomeEntryPayload.FoodDiaryEntry
                            ?: return@updateEach entity
                    entity.copy(payload = payload.copy(snapshot = payload.snapshot.anonymize()))
                }

            is FoodDiaryEntryDeletedEntryEvent -> homeDao.delete(event.diaryEntryId.id)
        }
    }
}

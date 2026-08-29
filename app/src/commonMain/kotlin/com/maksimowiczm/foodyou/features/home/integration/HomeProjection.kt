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
                val dateTime = event.entryTimestamp.toLocalDateTime(TimeZone.currentSystemDefault())
                homeDao.insert(
                    event.profileIds.map { profileId ->
                        HomeEntryEntity(
                            entryId = event.identity.id,
                            profileId = profileId.value,
                            mealId = event.mealIdentity.id,
                            date = dateTime.date,
                            time = dateTime.time,
                            composition = event.composition,
                        )
                    }
                )
            }

            is FoodDiaryEntryUpdatedEvent ->
                homeDao.updateInTransaction(event.identity.id) {
                    val dateTime =
                        event.entryTimestamp.toLocalDateTime(TimeZone.currentSystemDefault())
                    it.copy(
                        date = dateTime.date,
                        time = dateTime.time,
                        composition = event.composition,
                    )
                }

            is FoodDiaryEntryDeletedEvent ->
                if (event.strategy == DeleteStrategy.Delete) {
                    homeDao.delete(event.identity.id)
                } else {
                    // Unlink strategy - entry becomes anonymous
                    homeDao.updateInTransaction(event.identity.id) {
                        it.copy(composition = it.composition.anonymize())
                    }
                }

            is FoodDiaryEntryUnlinkedFromMealEvent ->
                homeDao.updateInTransaction(event.identity.id) {
                    it.copy(mealId = null)
                }
        }
    }
}

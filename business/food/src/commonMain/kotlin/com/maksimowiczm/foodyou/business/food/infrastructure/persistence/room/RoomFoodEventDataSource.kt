package com.maksimowiczm.foodyou.business.food.infrastructure.persistence.room

import com.maksimowiczm.foodyou.business.food.domain.FoodEvent
import com.maksimowiczm.foodyou.business.food.domain.FoodId
import com.maksimowiczm.foodyou.business.food.infrastructure.persistence.LocalFoodEventDataSource
import com.maksimowiczm.foodyou.business.shared.infrastructure.persistence.room.food.FoodEventDao
import com.maksimowiczm.foodyou.business.shared.infrastructure.persistence.room.food.FoodEventEntity
import com.maksimowiczm.foodyou.business.shared.infrastructure.persistence.room.food.FoodEventType
import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapValues
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime

internal class RoomFoodEventDataSource(private val foodEventDao: FoodEventDao) :
    LocalFoodEventDataSource {
    override suspend fun insert(foodId: FoodId, event: FoodEvent) {
        foodEventDao.insert(event.toEntity(foodId))
    }

    override suspend fun observeFoodEvents(foodId: FoodId): Flow<List<FoodEvent>> =
        when (foodId) {
            is FoodId.Product -> foodEventDao.observeProductEvents(foodId.id)
            is FoodId.Recipe -> foodEventDao.observeRecipeEvents(foodId.id)
        }.mapValues { it.toModel() }
}

@OptIn(ExperimentalTime::class)
private fun FoodEventEntity.toModel(): FoodEvent {
    val date =
        Instant.fromEpochSeconds(epochSeconds).toLocalDateTime(TimeZone.currentSystemDefault())

    return when (type) {
        FoodEventType.Created -> FoodEvent.Created(date)
        FoodEventType.Downloaded -> FoodEvent.Downloaded(date, extra)
        FoodEventType.Imported -> FoodEvent.Imported(date)
        FoodEventType.Edited -> FoodEvent.Edited(date)
        FoodEventType.ImportedFromFoodYou2 -> FoodEvent.ImportedFromFoodYou2(date)
    }
}

@OptIn(ExperimentalTime::class)
private fun FoodEvent.toEntity(foodId: FoodId): FoodEventEntity {
    val epochSeconds = date.toInstant(TimeZone.currentSystemDefault()).epochSeconds

    val extra =
        when (this) {
            is FoodEvent.Created -> null
            is FoodEvent.Downloaded -> url
            is FoodEvent.Imported -> null
            is FoodEvent.Edited -> null
            is FoodEvent.ImportedFromFoodYou2 -> null
        }

    val type =
        when (this) {
            is FoodEvent.Created -> FoodEventType.Created
            is FoodEvent.Downloaded -> FoodEventType.Downloaded
            is FoodEvent.Imported -> FoodEventType.Imported
            is FoodEvent.Edited -> FoodEventType.Edited
            is FoodEvent.ImportedFromFoodYou2 -> FoodEventType.ImportedFromFoodYou2
        }

    return FoodEventEntity(
        type = type,
        epochSeconds = epochSeconds,
        extra = extra,
        productId = (foodId as? FoodId.Product)?.id,
        recipeId = (foodId as? FoodId.Recipe)?.id,
    )
}

package com.maksimowiczm.foodyou.app.infrastructure.room

import com.maksimowiczm.foodyou.app.infrastructure.room.food.FoodEventDao
import com.maksimowiczm.foodyou.app.infrastructure.room.food.FoodEventEntity
import com.maksimowiczm.foodyou.app.infrastructure.room.food.FoodEventType
import com.maksimowiczm.foodyou.food.domain.entity.FoodHistory
import com.maksimowiczm.foodyou.food.domain.entity.FoodId
import com.maksimowiczm.foodyou.food.domain.repository.FoodHistoryRepository
import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapValues
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime

internal class RoomFoodHistoryRepository(private val foodEventDao: FoodEventDao) :
    FoodHistoryRepository {
    override suspend fun insert(foodId: FoodId, history: FoodHistory) {
        foodEventDao.insert(history.toEntity(foodId))
    }

    override fun observeFoodHistory(foodId: FoodId): Flow<List<FoodHistory>> =
        when (foodId) {
            is FoodId.Product -> foodEventDao.observeProductEvents(foodId.id)
            is FoodId.Recipe -> foodEventDao.observeRecipeEvents(foodId.id)
        }.mapValues { it.toModel() }
}

@OptIn(ExperimentalTime::class)
private fun FoodEventEntity.toModel(): FoodHistory {
    val date =
        Instant.fromEpochSeconds(epochSeconds).toLocalDateTime(TimeZone.currentSystemDefault())

    return when (type) {
        FoodEventType.Created -> FoodHistory.Created(date)
        FoodEventType.Downloaded -> FoodHistory.Downloaded(date, extra)
        FoodEventType.Imported -> FoodHistory.Imported(date)
        FoodEventType.Edited -> FoodHistory.Edited(date)
        FoodEventType.ImportedFromFoodYou2 -> FoodHistory.ImportedFromFoodYou2(date)
    }
}

@OptIn(ExperimentalTime::class)
private fun FoodHistory.toEntity(foodId: FoodId): FoodEventEntity {
    val epochSeconds = date.toInstant(TimeZone.currentSystemDefault()).epochSeconds

    val extra =
        when (this) {
            is FoodHistory.Created -> null
            is FoodHistory.Downloaded -> url
            is FoodHistory.Imported -> null
            is FoodHistory.Edited -> null
            is FoodHistory.ImportedFromFoodYou2 -> null
        }

    val type =
        when (this) {
            is FoodHistory.Created -> FoodEventType.Created
            is FoodHistory.Downloaded -> FoodEventType.Downloaded
            is FoodHistory.Imported -> FoodEventType.Imported
            is FoodHistory.Edited -> FoodEventType.Edited
            is FoodHistory.ImportedFromFoodYou2 -> FoodEventType.ImportedFromFoodYou2
        }

    return FoodEventEntity(
        type = type,
        epochSeconds = epochSeconds,
        extra = extra,
        productId = (foodId as? FoodId.Product)?.id,
        recipeId = (foodId as? FoodId.Recipe)?.id,
    )
}

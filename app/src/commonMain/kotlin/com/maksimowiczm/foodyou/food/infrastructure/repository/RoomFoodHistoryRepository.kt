package com.maksimowiczm.foodyou.food.infrastructure.repository

import com.maksimowiczm.foodyou.food.domain.entity.FoodHistory
import com.maksimowiczm.foodyou.food.domain.entity.FoodId
import com.maksimowiczm.foodyou.food.domain.repository.FoodHistoryRepository
import com.maksimowiczm.foodyou.food.infrastructure.room.FoodEventDao
import com.maksimowiczm.foodyou.food.infrastructure.room.FoodEventEntity
import com.maksimowiczm.foodyou.food.infrastructure.room.FoodEventType
import kotlin.time.Instant
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal class RoomFoodHistoryRepository(private val foodEventDao: FoodEventDao) :
    FoodHistoryRepository {
    override suspend fun insert(foodId: FoodId, history: FoodHistory) {
        foodEventDao.insert(history.toEntity(foodId))
    }

    override fun observeFoodHistory(foodId: FoodId): Flow<List<FoodHistory>> =
        when (foodId) {
            is FoodId.Product -> foodEventDao.observeProductEvents(foodId.id)
            is FoodId.Recipe -> foodEventDao.observeRecipeEvents(foodId.id)
        }.map { list -> list.map { it.toModel() } }
}

private fun FoodEventEntity.toModel(): FoodHistory {
    val timestamp = Instant.fromEpochSeconds(epochSeconds)

    return when (type) {
        FoodEventType.Created -> FoodHistory.Created(timestamp)
        FoodEventType.Downloaded -> FoodHistory.Downloaded(timestamp, extra)
        FoodEventType.Imported -> FoodHistory.Imported(timestamp)
        FoodEventType.Edited -> FoodHistory.Edited(timestamp)
        FoodEventType.ImportedFromFoodYou2 -> FoodHistory.ImportedFromFoodYou2(timestamp)
    }
}

private fun FoodHistory.toEntity(foodId: FoodId): FoodEventEntity {
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
        epochSeconds = timestamp.epochSeconds,
        extra = extra,
        productId = (foodId as? FoodId.Product)?.id,
        recipeId = (foodId as? FoodId.Recipe)?.id,
    )
}

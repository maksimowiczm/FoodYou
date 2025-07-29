package com.maksimowiczm.foodyou.feature.food.domain

import com.maksimowiczm.foodyou.feature.food.data.database.food.FoodEvent as FoodEventEntity
import com.maksimowiczm.foodyou.feature.food.data.database.food.ProductEventType
import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.json.Json

interface FoodEventMapper {
    fun toModel(entity: FoodEventEntity): FoodEvent
    fun toEntity(model: FoodEvent, foodId: FoodId): FoodEventEntity
}

@OptIn(ExperimentalTime::class)
internal class FoodEventMapperImpl : FoodEventMapper {

    override fun toModel(entity: FoodEventEntity): FoodEvent = with(entity) {
        val date =
            Instant.fromEpochSeconds(epochSeconds).toLocalDateTime(TimeZone.currentSystemDefault())

        when (type) {
            ProductEventType.Created -> FoodEvent.Created(date)

            ProductEventType.Downloaded -> FoodEvent.Downloaded(
                date = date,
                url = extra
            )

            ProductEventType.Imported -> FoodEvent.Imported(date)

            ProductEventType.Edited if (extra != null) -> FoodEvent.Edited(
                oldFood = Json.decodeFromString(extra),
                date = date
            )

            else -> error("Unknown ProductEventType: $type with extra: $extra")
        }
    }

    override fun toEntity(model: FoodEvent, foodId: FoodId) = with(model) {
        val epochSeconds = date.toInstant(TimeZone.currentSystemDefault()).epochSeconds

        val extra = when (this) {
            is FoodEvent.Created -> null
            is FoodEvent.Downloaded -> url
            is FoodEvent.Imported -> null
            is FoodEvent.Edited -> Json.encodeToString(oldFood)
        }

        val type = when (this) {
            is FoodEvent.Created -> ProductEventType.Created
            is FoodEvent.Downloaded -> ProductEventType.Downloaded
            is FoodEvent.Imported -> ProductEventType.Imported
            is FoodEvent.Edited -> ProductEventType.Edited
        }

        FoodEventEntity(
            type = type,
            epochSeconds = epochSeconds,
            extra = extra,
            productId = (foodId as? FoodId.Product)?.id,
            recipeId = (foodId as? FoodId.Recipe)?.id
        )
    }
}

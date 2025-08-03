package com.maksimowiczm.foodyou.feature.food.domain

import com.maksimowiczm.foodyou.feature.food.data.database.food.FoodEvent as FoodEventEntity
import com.maksimowiczm.foodyou.feature.food.data.database.food.FoodEventType
import com.maksimowiczm.foodyou.feature.measurement.domain.Measurement
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
            FoodEventType.Created -> FoodEvent.Created(date)

            FoodEventType.Downloaded -> FoodEvent.Downloaded(
                date = date,
                url = extra
            )

            FoodEventType.Imported -> FoodEvent.Imported(date)

            FoodEventType.Edited -> {
                if (extra == null) {
                    error("Extra field cannot be null for Edited event")
                }

                FoodEvent.Edited(
                    oldFood = Json.decodeFromString(extra),
                    date = date
                )
            }

            FoodEventType.Measured -> {
                val measurement = if (extra != null) {
                    Json.decodeFromString<Measurement>(extra)
                } else {
                    Measurement.Gram(100f)
                }

                FoodEvent.Measured(
                    date = date,
                    measurement = measurement
                )
            }

            FoodEventType.ImportedFromFoodYou2 -> FoodEvent.ImportedFromFoodYou2(date)
        }
    }

    override fun toEntity(model: FoodEvent, foodId: FoodId) = with(model) {
        val epochSeconds = date.toInstant(TimeZone.currentSystemDefault()).epochSeconds

        val extra = when (this) {
            is FoodEvent.Created -> null
            is FoodEvent.Downloaded -> url
            is FoodEvent.Imported -> null
            is FoodEvent.Edited -> Json.encodeToString(oldFood)
            is FoodEvent.Measured -> Json.encodeToString(measurement)
            is FoodEvent.ImportedFromFoodYou2 -> null
        }

        val type = when (this) {
            is FoodEvent.Created -> FoodEventType.Created
            is FoodEvent.Downloaded -> FoodEventType.Downloaded
            is FoodEvent.Imported -> FoodEventType.Imported
            is FoodEvent.Edited -> FoodEventType.Edited
            is FoodEvent.Measured -> FoodEventType.Measured
            is FoodEvent.ImportedFromFoodYou2 -> FoodEventType.ImportedFromFoodYou2
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

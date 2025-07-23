package com.maksimowiczm.foodyou.feature.food.domain

import com.maksimowiczm.foodyou.feature.food.data.database.food.ProductEvent as ProductEventEntity
import com.maksimowiczm.foodyou.feature.food.data.database.food.ProductEventType
import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.json.Json

interface ProductEventMapper {
    fun toModel(entity: ProductEventEntity): ProductEvent
    fun toEntity(model: ProductEvent, productId: Long): ProductEventEntity
}

@OptIn(ExperimentalTime::class)
internal class ProductEventMapperImpl : ProductEventMapper {

    override fun toModel(entity: ProductEventEntity): ProductEvent = with(entity) {
        val date =
            Instant.fromEpochSeconds(epochSeconds).toLocalDateTime(TimeZone.currentSystemDefault())

        when (type) {
            ProductEventType.Created -> ProductEvent.Created(date)

            ProductEventType.Downloaded -> ProductEvent.Downloaded(
                date = date,
                url = extra
            )

            ProductEventType.Imported -> ProductEvent.Imported(date)

            ProductEventType.Edited if (extra != null) -> ProductEvent.Edited(
                oldProduct = Json.decodeFromString(extra),
                date = date
            )

            else -> error("Unknown ProductEventType: $type with extra: $extra")
        }
    }

    override fun toEntity(model: ProductEvent, productId: Long) = with(model) {
        val epochSeconds = date.toInstant(TimeZone.currentSystemDefault()).epochSeconds

        val extra = when (this) {
            is ProductEvent.Created -> null
            is ProductEvent.Downloaded -> url
            is ProductEvent.Imported -> null
            is ProductEvent.Edited -> Json.encodeToString(oldProduct)
        }

        val type = when (this) {
            is ProductEvent.Created -> ProductEventType.Created
            is ProductEvent.Downloaded -> ProductEventType.Downloaded
            is ProductEvent.Imported -> ProductEventType.Imported
            is ProductEvent.Edited -> ProductEventType.Edited
        }

        ProductEventEntity(
            type = type,
            epochSeconds = epochSeconds,
            extra = extra,
            productId = productId
        )
    }
}

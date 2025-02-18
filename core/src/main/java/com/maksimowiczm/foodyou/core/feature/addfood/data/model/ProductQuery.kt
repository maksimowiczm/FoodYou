package com.maksimowiczm.foodyou.core.feature.addfood.data.model

import com.maksimowiczm.foodyou.core.feature.addfood.database.ProductQueryEntity
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

data class ProductQuery(
    val query: String,
    val date: LocalDateTime
)

fun ProductQueryEntity.toDomain(): ProductQuery {
    return ProductQuery(
        query = query,
        date = Instant.fromEpochSeconds(date).toLocalDateTime(TimeZone.UTC)
    )
}

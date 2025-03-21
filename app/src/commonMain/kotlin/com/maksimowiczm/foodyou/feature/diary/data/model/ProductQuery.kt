package com.maksimowiczm.foodyou.feature.diary.data.model

import com.maksimowiczm.foodyou.feature.diary.database.entity.ProductQueryEntity
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

data class ProductQuery(val query: String, val date: LocalDateTime)

fun ProductQueryEntity.toDomain(): ProductQuery = ProductQuery(
    query = query,
    date = Instant.fromEpochSeconds(date).toLocalDateTime(TimeZone.UTC)
)

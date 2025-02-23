package com.maksimowiczm.foodyou.feature.addfood.data.model

import com.maksimowiczm.foodyou.feature.addfood.database.ProductQueryEntity
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

fun ProductQueryEntity.toDomain(): ProductQuery = ProductQuery(
    query = query,
    date = Instant.fromEpochSeconds(date).toLocalDateTime(TimeZone.UTC)
)

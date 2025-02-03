package com.maksimowiczm.foodyou.core.feature.addfood.data.model

import com.maksimowiczm.foodyou.core.feature.addfood.database.ProductQueryEntity
import java.time.LocalDateTime
import java.time.ZoneOffset

data class ProductQuery(
    val query: String,
    val date: LocalDateTime
)

fun ProductQueryEntity.toDomain(): ProductQuery {
    return ProductQuery(
        query = query,
        date = LocalDateTime.ofEpochSecond(date, 0, ZoneOffset.UTC)
    )
}

package com.maksimowiczm.foodyou.core.data.model.search

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class SearchQueryEntity(
    @PrimaryKey
    val query: String,
    val epochSeconds: Long
)

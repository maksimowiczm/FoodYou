package com.maksimowiczm.foodyou.core.database.search

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class SearchQueryEntity(
    @PrimaryKey
    val query: String,
    val epochSeconds: Long
)

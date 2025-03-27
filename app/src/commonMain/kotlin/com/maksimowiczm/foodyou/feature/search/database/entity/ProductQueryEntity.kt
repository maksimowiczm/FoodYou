package com.maksimowiczm.foodyou.feature.search.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ProductQueryEntity(
    @PrimaryKey
    val query: String,
    val date: Long
)

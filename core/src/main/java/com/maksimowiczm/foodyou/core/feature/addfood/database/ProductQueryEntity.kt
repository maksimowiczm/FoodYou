package com.maksimowiczm.foodyou.core.feature.addfood.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ProductQueryEntity(
    @PrimaryKey
    val query: String,
    val date: Long
)

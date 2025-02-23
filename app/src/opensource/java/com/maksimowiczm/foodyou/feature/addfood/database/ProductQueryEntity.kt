package com.maksimowiczm.foodyou.feature.addfood.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ProductQueryEntity(
    @PrimaryKey
    val query: String,
    val date: Long
)

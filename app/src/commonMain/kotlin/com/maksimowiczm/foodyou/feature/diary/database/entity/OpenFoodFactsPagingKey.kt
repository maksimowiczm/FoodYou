package com.maksimowiczm.foodyou.feature.diary.database.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    indices = [
        Index(
            value = ["queryString", "country"],
            unique = true
        )
    ]
)
data class OpenFoodFactsPagingKey(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val queryString: String,
    val country: String?,
    val fetchedCount: Int,
    val totalCount: Int
)

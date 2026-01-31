package com.maksimowiczm.foodyou.foodsearch.infrastructure.room

import androidx.room.Entity
import androidx.room.Index

@Entity(
    tableName = "SearchHistory",
    primaryKeys = ["query"],
    indices = [Index(value = ["profileId"])],
)
internal data class SearchHistoryEntity(
    val profileId: String,
    val query: String,
    val timestampMillis: Long,
)

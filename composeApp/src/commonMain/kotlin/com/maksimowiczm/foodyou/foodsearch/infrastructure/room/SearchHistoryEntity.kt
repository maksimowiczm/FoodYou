package com.maksimowiczm.foodyou.foodsearch.infrastructure.room

import androidx.room.Entity
import androidx.room.Index
import kotlin.uuid.Uuid

@Entity(
    tableName = "SearchHistory",
    primaryKeys = ["query"],
    indices = [Index(value = ["profileId"])],
)
internal data class SearchHistoryEntity(
    val profileId: Uuid,
    val query: String,
    val timestampMillis: Long,
)

package com.maksimowiczm.foodyou.search.infrastructure.history

import androidx.room.Entity
import androidx.room.Index
import kotlin.uuid.Uuid

@Entity(
    tableName = "SearchHistory",
    primaryKeys = ["query"],
    indices = [Index(value = ["profileId"])],
)
data class SearchHistoryEntity(val profileId: Uuid, val query: String, val timestampMillis: Long)

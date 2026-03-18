package com.maksimowiczm.foodyou.common.infrastructure.room.eventstore

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "EventStore")
data class RoomEventStoreEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val eventType: String,
    val eventData: String,
    val timestamp: Long,
)

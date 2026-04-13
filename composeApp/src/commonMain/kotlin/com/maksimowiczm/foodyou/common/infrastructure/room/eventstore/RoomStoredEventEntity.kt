package com.maksimowiczm.foodyou.common.infrastructure.room.eventstore

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "StoredEvent")
data class RoomStoredEventEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val eventType: String,
    val eventStream: String,
    val payloadJson: String,
    val occurredAtEpochMs: Long,
)

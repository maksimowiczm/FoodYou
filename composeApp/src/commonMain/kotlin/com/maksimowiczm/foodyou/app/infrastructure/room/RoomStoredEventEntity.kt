package com.maksimowiczm.foodyou.app.infrastructure.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "StoredEvent")
internal data class RoomStoredEventEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val eventStream: String,
    val payloadJson: String,
    val occurredAtEpochMs: Long,
)

package com.maksimowiczm.foodyou.common.infrastructure.room.eventstore

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "EventStore", indices = [Index(value = ["aggregateId"])])
data class RoomEventStoreEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val aggregateId: String,
    val eventType: String,
    val eventData: String,
    val timestamp: Long,
)

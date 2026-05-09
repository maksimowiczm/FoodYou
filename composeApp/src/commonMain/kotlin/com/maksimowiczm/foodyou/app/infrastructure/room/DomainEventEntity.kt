package com.maksimowiczm.foodyou.app.infrastructure.room

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "EventStore", indices = [Index(value = ["eventStream", "id"])])
internal data class DomainEventEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val eventStream: String,
    val payloadJson: String,
    val occurredAtEpochMs: Long,
)

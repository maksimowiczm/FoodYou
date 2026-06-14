package com.maksimowiczm.foodyou.app.infrastructure.room

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlin.uuid.Uuid

@Entity(tableName = "EventStore", indices = [Index(value = ["eventStream", "id"])])
internal data class DomainEventEntity(
    @PrimaryKey val id: Uuid,
    val eventStream: String,
    val payloadJson: String,
    val occurredAtEpochMs: Long,
)

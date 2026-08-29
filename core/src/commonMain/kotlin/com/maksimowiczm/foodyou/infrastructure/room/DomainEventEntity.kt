package com.maksimowiczm.foodyou.infrastructure.room

import androidx.room3.*
import kotlin.uuid.Uuid

@Entity(tableName = "EventStore", indices = [Index(value = ["eventStream", "id"])])
data class DomainEventEntity(
    @PrimaryKey val id: Uuid,
    val eventStream: String,
    val payloadJson: String,
    val occurredAtEpochMs: Long,
)

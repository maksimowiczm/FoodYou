package com.maksimowiczm.foodyou.app.infrastructure.room

import androidx.room3.*
import kotlin.uuid.Uuid

@Entity(
    tableName = "EventStore",
    indices = [Index(value = ["eventStream", "persistedAtEpochMs"])],
)
internal data class DomainEventEntity(
    @PrimaryKey val dbId: Uuid,
    val eventStream: String,
    val payloadJson: String,
    val persistedAtEpochMs: Long,
)

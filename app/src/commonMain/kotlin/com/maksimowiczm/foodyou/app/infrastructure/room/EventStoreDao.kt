package com.maksimowiczm.foodyou.app.infrastructure.room

import androidx.room3.*
import kotlinx.coroutines.flow.Flow

@Dao
internal interface EventStoreDao {

    @Query(
        """
        SELECT *
        FROM EventStore
        WHERE eventStream = :stream
        ORDER BY occurredAtEpochMs ASC, rowid ASC
        """
    )
    suspend fun getAllByStream(stream: String): List<DomainEventEntity>

    @Query(
        """
        SELECT *
        FROM EventStore
        WHERE eventStream = :stream
        ORDER BY occurredAtEpochMs ASC, rowid ASC
        """
    )
    fun observeAllByStream(stream: String): Flow<List<DomainEventEntity>>

    @Insert suspend fun insertAll(events: List<DomainEventEntity>)
}

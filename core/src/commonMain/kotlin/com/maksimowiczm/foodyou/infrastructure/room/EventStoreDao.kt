package com.maksimowiczm.foodyou.infrastructure.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface EventStoreDao {

    @Query(
        """
        SELECT *
        FROM EventStore
        WHERE eventStream = :stream
        ORDER BY occurredAtEpochMs ASC
        """
    )
    suspend fun getAllByStream(stream: String): List<DomainEventEntity>

    @Query(
        """
        SELECT *
        FROM EventStore
        WHERE eventStream = :stream
        ORDER BY occurredAtEpochMs ASC
        """
    )
    fun observeAllByStream(stream: String): Flow<List<DomainEventEntity>>

    @Insert suspend fun insertAll(events: List<DomainEventEntity>)
}

package com.maksimowiczm.foodyou.common.infrastructure.room.eventstore

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface EventStoreDao {
    @Query(
        """
        SELECT *
        FROM EventStore
        ORDER BY timestamp ASC
        """
    )
    suspend fun getAllByAggregateId(): List<RoomEventStoreEntity>

    @Insert suspend fun insert(event: RoomEventStoreEntity)

    @Insert suspend fun insertAll(events: List<RoomEventStoreEntity>)
}

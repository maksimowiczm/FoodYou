package com.maksimowiczm.foodyou.common.infrastructure.room.eventstore

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface EventStoreDao {
    @Query("SELECT * FROM StoredEvent WHERE eventStream = :stream")
    suspend fun getAllByStream(stream: String): List<RoomStoredEventEntity>

    @Query("SELECT * FROM StoredEvent WHERE eventStream = :stream")
    fun observeAllByStream(stream: String): Flow<List<RoomStoredEventEntity>>

    @Insert suspend fun insertAll(events: List<RoomStoredEventEntity>)
}

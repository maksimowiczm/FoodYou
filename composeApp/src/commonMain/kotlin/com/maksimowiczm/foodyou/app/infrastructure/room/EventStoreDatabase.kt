package com.maksimowiczm.foodyou.app.infrastructure.room

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor

@Database(
    entities = [RoomStoredEventEntity::class],
    version = EventStoreDatabase.VERSION,
    exportSchema = false,
)
@ConstructedBy(EventStoreDatabaseConstructor::class)
internal abstract class EventStoreDatabase : RoomDatabase() {
    abstract val eventStoreDao: EventStoreDao

    companion object {
        const val VERSION = 1

        fun Builder<EventStoreDatabase>.buildDatabase(): EventStoreDatabase = build()
    }
}

@Suppress("KotlinNoActualForExpect")
internal expect object EventStoreDatabaseConstructor : RoomDatabaseConstructor<EventStoreDatabase> {
    override fun initialize(): EventStoreDatabase
}

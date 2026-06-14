package com.maksimowiczm.foodyou.app.infrastructure.room

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.room.TypeConverters
import com.maksimowiczm.foodyou.common.infrastructure.room.UuidConverter

@Database(
    entities = [DomainEventEntity::class],
    version = EventStoreDatabase.VERSION,
    exportSchema = false,
)
@TypeConverters(UuidConverter::class)
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

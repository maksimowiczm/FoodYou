package com.maksimowiczm.foodyou.infrastructure.room

import androidx.room3.*
import com.maksimowiczm.foodyou.common.infrastructure.room.UuidConverter

@Database(
    entities = [DomainEventEntity::class],
    version = EventStoreDatabase.VERSION,
    exportSchema = false,
)
@ColumnTypeConverters(UuidConverter::class)
@ConstructedBy(EventStoreDatabaseConstructor::class)
abstract class EventStoreDatabase : RoomDatabase() {
    abstract val eventStoreDao: EventStoreDao

    companion object {
        const val VERSION = 1

        fun Builder<EventStoreDatabase>.buildDatabase(): EventStoreDatabase = build()
    }
}

@Suppress("KotlinNoActualForExpect")
expect object EventStoreDatabaseConstructor : RoomDatabaseConstructor<EventStoreDatabase> {
    override fun initialize(): EventStoreDatabase
}

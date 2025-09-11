package com.maksimowiczm.foodyou.app.infrastructure.room

import androidx.room.RoomDatabase
import androidx.room.execSQL
import androidx.room.useWriterConnection
import com.maksimowiczm.foodyou.app.business.opensource.domain.database.DatabaseDumpService
import kotlinx.coroutines.flow.Flow

internal class RoomDatabaseDumpService(private val database: RoomDatabase) : DatabaseDumpService {
    override suspend fun provideDatabaseDump(): Flow<ByteArray> =
        database.applyFullCheckpoint().databaseBytes()
}

private suspend fun RoomDatabase.applyFullCheckpoint(): RoomDatabase = apply {
    useWriterConnection { conn -> conn.execSQL("PRAGMA wal_checkpoint(FULL);") }
}

internal expect fun RoomDatabase.databaseBytes(): Flow<ByteArray>

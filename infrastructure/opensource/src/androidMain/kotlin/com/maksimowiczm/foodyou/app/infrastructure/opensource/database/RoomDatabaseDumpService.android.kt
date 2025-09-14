package com.maksimowiczm.foodyou.app.infrastructure.opensource.database

import androidx.room.RoomDatabase
import java.io.File
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

internal actual fun RoomDatabase.databaseBytes(): Flow<ByteArray> {
    val path = openHelper.writableDatabase.path

    requireNotNull(path) { "Database path can't be null" }

    return flow { File(path).inputStream().use { stream -> emit(stream.readBytes()) } }
}

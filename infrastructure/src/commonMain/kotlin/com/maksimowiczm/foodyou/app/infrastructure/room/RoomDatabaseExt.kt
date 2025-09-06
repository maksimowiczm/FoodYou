package com.maksimowiczm.foodyou.app.infrastructure.room

import androidx.room.RoomDatabase
import androidx.room.TransactionScope
import androidx.room.immediateTransaction
import androidx.room.useWriterConnection

suspend fun <T> RoomDatabase.immediateTransaction(block: suspend TransactionScope<T>.() -> T): T =
    useWriterConnection {
        it.immediateTransaction { block(this) }
    }

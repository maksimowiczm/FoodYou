package com.maksimowiczm.foodyou.app.business.opensource.domain.database

import kotlinx.coroutines.flow.Flow

fun interface DatabaseDumpService {
    suspend fun provideDatabaseDump(): Flow<ByteArray>
}

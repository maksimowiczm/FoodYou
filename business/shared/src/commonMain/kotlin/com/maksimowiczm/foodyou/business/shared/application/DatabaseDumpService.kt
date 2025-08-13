package com.maksimowiczm.foodyou.business.shared.application

import kotlinx.coroutines.flow.Flow

fun interface DatabaseDumpService {
    suspend fun provideDatabaseDump(): Flow<ByteArray>
}

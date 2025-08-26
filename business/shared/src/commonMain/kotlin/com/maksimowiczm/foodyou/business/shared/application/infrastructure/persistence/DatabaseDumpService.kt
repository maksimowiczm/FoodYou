package com.maksimowiczm.foodyou.business.shared.application.infrastructure.persistence

import kotlinx.coroutines.flow.Flow

fun interface DatabaseDumpService {
    suspend fun provideDatabaseDump(): Flow<ByteArray>
}

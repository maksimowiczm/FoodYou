package com.maksimowiczm.foodyou.importexport.domain.service

import kotlinx.coroutines.flow.Flow

fun interface DatabaseDumpService {
    suspend fun provideDatabaseDump(): Flow<ByteArray>
}

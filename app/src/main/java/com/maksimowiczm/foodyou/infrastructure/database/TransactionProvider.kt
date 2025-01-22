package com.maksimowiczm.foodyou.infrastructure.database

interface TransactionProvider {
    suspend fun <T> withTransaction(block: suspend () -> T): T
}

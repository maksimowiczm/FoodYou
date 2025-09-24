package com.maksimowiczm.foodyou.common.domain.database

interface TransactionScope<T> {

    suspend fun rollback(result: T)
}

package com.maksimowiczm.foodyou.shared.domain.database

interface TransactionScope<T> {

    suspend fun rollback(result: T)
}

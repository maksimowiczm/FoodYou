package com.maksimowiczm.foodyou.business.shared.application.database

interface TransactionScope<T> {

    suspend fun rollback(result: T)
}

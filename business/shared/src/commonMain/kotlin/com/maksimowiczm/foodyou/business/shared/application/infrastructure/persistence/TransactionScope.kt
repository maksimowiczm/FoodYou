package com.maksimowiczm.foodyou.business.shared.application.infrastructure.persistence

interface TransactionScope<T> {

    suspend fun rollback(result: T)
}

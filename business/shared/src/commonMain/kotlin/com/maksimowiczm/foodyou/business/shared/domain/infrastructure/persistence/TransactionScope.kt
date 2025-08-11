package com.maksimowiczm.foodyou.business.shared.domain.infrastructure.persistence

interface TransactionScope<T> {

    suspend fun rollback(result: T)
}

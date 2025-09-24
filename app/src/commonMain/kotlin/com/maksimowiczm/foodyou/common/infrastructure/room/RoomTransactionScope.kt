package com.maksimowiczm.foodyou.common.infrastructure.room

import androidx.room.TransactionScope

class RoomTransactionScope<T>(private val scope: TransactionScope<T>) :
    com.maksimowiczm.foodyou.common.domain.database.TransactionScope<T> {
    override suspend fun rollback(result: T) = scope.rollback(result)
}

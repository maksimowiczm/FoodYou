package com.maksimowiczm.foodyou.app.infrastructure.opensource.shared.room

internal class RoomTransactionScope<T>(private val scope: androidx.room.TransactionScope<T>) :
    com.maksimowiczm.foodyou.shared.domain.database.TransactionScope<T> {
    override suspend fun rollback(result: T) = scope.rollback(result)
}

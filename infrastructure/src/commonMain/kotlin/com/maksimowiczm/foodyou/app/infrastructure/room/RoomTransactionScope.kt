package com.maksimowiczm.foodyou.app.infrastructure.room

import androidx.room.TransactionScope
import com.maksimowiczm.foodyou.shared.domain.database.TransactionScope as DomainTransactionScope

internal class RoomTransactionScope<T>(private val scope: TransactionScope<T>) :
    DomainTransactionScope<T> {
    override suspend fun rollback(result: T) = scope.rollback(result)
}

package com.maksimowiczm.foodyou.business.shared.infrastructure.room

import androidx.room.TransactionScope
import com.maksimowiczm.foodyou.business.shared.application.database.TransactionScope as DomainTransactionScope

internal class RoomTransactionScope<T>(private val scope: TransactionScope<T>) :
    DomainTransactionScope<T> {
    override suspend fun rollback(result: T) = scope.rollback(result)
}

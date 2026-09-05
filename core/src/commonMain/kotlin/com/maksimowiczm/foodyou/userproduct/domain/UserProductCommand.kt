package com.maksimowiczm.foodyou.userproduct.domain

import com.maksimowiczm.foodyou.common.domain.DeleteStrategy
import kotlin.time.Instant

sealed interface UserProductCommand {
    data class Create(val product: UserProduct, val timestamp: Instant) : UserProductCommand

    data class Update(val timestamp: Instant, val transform: (UserProduct) -> UserProduct) :
        UserProductCommand

    data class Remove(val strategy: DeleteStrategy, val timestamp: Instant) : UserProductCommand
}

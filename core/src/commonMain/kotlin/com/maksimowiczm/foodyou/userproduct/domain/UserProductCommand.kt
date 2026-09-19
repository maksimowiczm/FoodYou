package com.maksimowiczm.foodyou.userproduct.domain

import com.maksimowiczm.foodyou.common.domain.DeleteStrategy

sealed interface UserProductCommand {
    data class Create(val product: UserProduct) : UserProductCommand

    data class Update(val transform: (UserProduct) -> UserProduct) : UserProductCommand

    data class Remove(val strategy: DeleteStrategy) : UserProductCommand
}

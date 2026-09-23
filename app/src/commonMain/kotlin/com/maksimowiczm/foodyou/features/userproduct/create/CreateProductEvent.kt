package com.maksimowiczm.foodyou.features.userproduct.create

import com.maksimowiczm.foodyou.userproduct.domain.UserProductId

internal sealed interface CreateProductEvent {
    data class Created(val id: UserProductId) : CreateProductEvent
}

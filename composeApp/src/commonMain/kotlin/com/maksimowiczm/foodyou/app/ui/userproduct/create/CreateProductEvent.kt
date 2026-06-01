package com.maksimowiczm.foodyou.app.ui.userproduct.create

import com.maksimowiczm.foodyou.userproduct.domain.UserProductIdentity

internal sealed interface CreateProductEvent {
    data class Created(val id: UserProductIdentity) : CreateProductEvent
}

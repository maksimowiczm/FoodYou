package com.maksimowiczm.foodyou.app.ui.userfood.create

import com.maksimowiczm.foodyou.userfood.domain.product.UserProductIdentity

sealed interface CreateProductEvent {
    data class Created(val id: UserProductIdentity) : CreateProductEvent
}

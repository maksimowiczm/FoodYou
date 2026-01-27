package com.maksimowiczm.foodyou.app.ui.userfood.create

import com.maksimowiczm.foodyou.userfood.domain.UserFoodProductIdentity

sealed interface CreateProductEvent {
    data class Created(val id: UserFoodProductIdentity) : CreateProductEvent
}

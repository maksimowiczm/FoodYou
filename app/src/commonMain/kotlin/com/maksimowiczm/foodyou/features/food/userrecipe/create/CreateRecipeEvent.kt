package com.maksimowiczm.foodyou.features.food.userrecipe.create

import com.maksimowiczm.foodyou.userrecipe.domain.UserRecipeIdentity

sealed interface CreateRecipeEvent {
    data class Created(val id: UserRecipeIdentity) : CreateRecipeEvent
}

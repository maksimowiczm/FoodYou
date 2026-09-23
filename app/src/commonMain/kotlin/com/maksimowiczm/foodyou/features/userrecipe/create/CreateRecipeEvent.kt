package com.maksimowiczm.foodyou.features.userrecipe.create

import com.maksimowiczm.foodyou.userrecipe.domain.UserRecipeId

sealed interface CreateRecipeEvent {
    data class Created(val id: UserRecipeId) : CreateRecipeEvent
}

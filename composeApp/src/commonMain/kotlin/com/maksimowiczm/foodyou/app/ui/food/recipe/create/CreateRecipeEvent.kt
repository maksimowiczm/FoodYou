package com.maksimowiczm.foodyou.app.ui.food.recipe.create

import com.maksimowiczm.foodyou.userrecipe.domain.UserRecipeIdentity

sealed interface CreateRecipeEvent {
    data class Created(val id: UserRecipeIdentity) : CreateRecipeEvent
}

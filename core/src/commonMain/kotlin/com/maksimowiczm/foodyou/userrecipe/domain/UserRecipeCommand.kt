package com.maksimowiczm.foodyou.userrecipe.domain

import com.maksimowiczm.foodyou.common.domain.DeleteStrategy
import kotlin.time.Instant

sealed interface UserRecipeCommand {
    data class Create(val recipe: UserRecipe, val timestamp: Instant) : UserRecipeCommand

    data class Update(val timestamp: Instant, val transform: (UserRecipe) -> UserRecipe) :
        UserRecipeCommand

    data class Remove(val strategy: DeleteStrategy, val timestamp: Instant) : UserRecipeCommand
}

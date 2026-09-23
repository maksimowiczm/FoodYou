package com.maksimowiczm.foodyou.userrecipe.domain

import com.maksimowiczm.foodyou.common.domain.DeleteStrategy

sealed interface UserRecipeCommand {
    data class Create(val recipe: UserRecipe) : UserRecipeCommand

    data class Update(val transform: (UserRecipe) -> UserRecipe) : UserRecipeCommand

    data class Remove(val strategy: DeleteStrategy) : UserRecipeCommand
}

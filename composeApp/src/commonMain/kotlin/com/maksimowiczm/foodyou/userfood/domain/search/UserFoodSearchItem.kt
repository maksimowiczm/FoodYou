package com.maksimowiczm.foodyou.userfood.domain.search

import com.maksimowiczm.foodyou.userfood.domain.product.UserProduct
import com.maksimowiczm.foodyou.userfood.domain.recipe.UserRecipeIdentity
import com.maksimowiczm.foodyou.userfood.domain.recipe.UserRecipeName

sealed interface UserFoodSearchItem {
    data class Product(val product: UserProduct) : UserFoodSearchItem

    data class Recipe(val recipeIdentity: UserRecipeIdentity, val recipeName: UserRecipeName) :
        UserFoodSearchItem
}

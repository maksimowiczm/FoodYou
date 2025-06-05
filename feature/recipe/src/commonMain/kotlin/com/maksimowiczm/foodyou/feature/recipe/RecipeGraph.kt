package com.maksimowiczm.foodyou.feature.recipe

import androidx.navigation.NavGraphBuilder
import androidx.navigation.toRoute
import com.maksimowiczm.foodyou.core.model.FoodId
import com.maksimowiczm.foodyou.core.navigation.forwardBackwardComposable
import com.maksimowiczm.foodyou.feature.recipe.ui.create.CreateRecipeScreen
import com.maksimowiczm.foodyou.feature.recipe.ui.update.UpdateRecipeScreen
import kotlinx.serialization.Serializable

@Serializable
data object CreateRecipe

@Serializable
data class UpdateRecipe(val recipeId: Long) {
    val id: FoodId.Recipe
        get() = FoodId.Recipe(recipeId)
}

fun NavGraphBuilder.recipeGraph(
    createOnBack: () -> Unit,
    onCreate: (FoodId.Recipe) -> Unit,
    updateOnBack: () -> Unit,
    onUpdate: () -> Unit,
    onEditFood: (FoodId) -> Unit
) {
    forwardBackwardComposable<CreateRecipe> {
        CreateRecipeScreen(
            onBack = createOnBack,
            onCreate = onCreate,
            onEditFood = onEditFood
        )
    }
    forwardBackwardComposable<UpdateRecipe> {
        val route = it.toRoute<UpdateRecipe>()

        UpdateRecipeScreen(
            recipeId = route.id,
            onBack = updateOnBack,
            onUpdate = onUpdate,
            onEditFood = onEditFood
        )
    }
}

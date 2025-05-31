package com.maksimowiczm.foodyou.feature.recipe

import androidx.navigation.NavGraphBuilder
import com.maksimowiczm.foodyou.core.domain.model.FoodId
import com.maksimowiczm.foodyou.core.navigation.forwardBackwardComposable
import com.maksimowiczm.foodyou.feature.recipe.ui.create.CreateRecipeScreen
import kotlinx.serialization.Serializable

@Serializable
data object CreateRecipe

@Serializable
data class UpdateRecipe(val recipeId: Long)

fun NavGraphBuilder.recipeGraph(createOnBack: () -> Unit, onCreate: (FoodId.Recipe) -> Unit) {
    forwardBackwardComposable<CreateRecipe> {
        CreateRecipeScreen(
            onBack = createOnBack,
            onCreate = onCreate
        )
    }
}

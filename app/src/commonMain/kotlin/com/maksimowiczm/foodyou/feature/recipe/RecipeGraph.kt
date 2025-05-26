package com.maksimowiczm.foodyou.feature.recipe

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraphBuilder
import androidx.navigation.toRoute
import com.maksimowiczm.foodyou.core.navigation.forwardBackwardComposable
import com.maksimowiczm.foodyou.feature.recipe.ui.RecipeApp
import kotlinx.serialization.Serializable

@Serializable
data object CreateRecipe

@Serializable
data class UpdateRecipe(val recipeId: Long)

fun NavGraphBuilder.recipeGraph(
    onCreateClose: () -> Unit,
    onCreate: (recipeId: Long) -> Unit,
    onUpdateClose: () -> Unit,
    onUpdate: (recipeId: Long) -> Unit
) {
    forwardBackwardComposable<CreateRecipe> {
        Surface(
            shadowElevation = 6.dp,
            shape = MaterialTheme.shapes.medium
        ) {
            RecipeApp(
                onBack = onCreateClose,
                onCreate = onCreate
            )
        }
    }
    forwardBackwardComposable<UpdateRecipe> {
        val (recipeId) = it.toRoute<UpdateRecipe>()

        Surface(
            shadowElevation = 6.dp,
            shape = MaterialTheme.shapes.medium
        ) {
            RecipeApp(
                onBack = onUpdateClose,
                onCreate = onUpdate,
                recipeId = recipeId
            )
        }
    }
}

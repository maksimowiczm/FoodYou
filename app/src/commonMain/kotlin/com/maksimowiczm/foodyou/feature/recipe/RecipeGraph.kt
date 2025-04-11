package com.maksimowiczm.foodyou.feature.recipe

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraphBuilder
import com.maksimowiczm.foodyou.core.navigation.fullScreenDialogComposable
import com.maksimowiczm.foodyou.feature.recipe.ui.CreateRecipeScreen
import kotlinx.serialization.Serializable

@Serializable
data object CreateRecipe

fun NavGraphBuilder.recipeGraph() {
    fullScreenDialogComposable<CreateRecipe> {
        Surface(
            shadowElevation = 6.dp,
            shape = MaterialTheme.shapes.medium
        ) {
            CreateRecipeScreen()
        }
    }
}

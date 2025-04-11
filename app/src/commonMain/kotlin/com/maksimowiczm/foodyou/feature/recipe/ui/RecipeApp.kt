package com.maksimowiczm.foodyou.feature.recipe.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.maksimowiczm.foodyou.core.navigation.crossfadeComposable
import kotlinx.serialization.Serializable

@Composable
internal fun RecipeApp(onBack: () -> Unit, modifier: Modifier = Modifier) {
    RecipeNavHost(
        onBack = onBack,
        modifier = modifier
    )
}

@Composable
private fun RecipeNavHost(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = CreateRecipe
    ) {
        crossfadeComposable<CreateRecipe> {
            CreateRecipeScreen(
                onClose = {
                    if (navController.currentBackStack.value.size == 2) {
                        onBack()
                    } else {
                        navController.popBackStack<CreateRecipe>(inclusive = true)
                    }
                },
                modifier = modifier
            )
        }
    }
}

@Serializable
private data object CreateRecipe

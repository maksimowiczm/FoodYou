package com.maksimowiczm.foodyou.feature.recipe.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.maksimowiczm.foodyou.core.navigation.crossfadeComposable
import kotlinx.serialization.Serializable
import org.koin.compose.viewmodel.koinViewModel

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
    val viewModel = koinViewModel<RecipeViewModel>()
    val state by viewModel.state.collectAsStateWithLifecycle()

    NavHost(
        navController = navController,
        startDestination = CreateRecipe
    ) {
        crossfadeComposable<CreateRecipe> {
            RecipeFormScreen(
                state = state,
                onNameChange = remember(viewModel) { viewModel::onNameChange },
                onServingsChange = remember(viewModel) { viewModel::onServingsChange },
                onAddIngredient = {
                    navController.navigate(AddIngredient) {
                        launchSingleTop = true
                    }
                },
                onClose = {
                    if (navController.currentBackStack.value.size == 2) {
                        onBack()
                    } else {
                        navController.popBackStack<CreateRecipe>(inclusive = true)
                    }
                },
                onCreate = remember(viewModel) { viewModel::onCreate },
                modifier = modifier
            )
        }
        crossfadeComposable<AddIngredient> {
        }
    }
}

@Serializable
private data object CreateRecipe

@Serializable
private data object AddIngredient

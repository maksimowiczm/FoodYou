package com.maksimowiczm.foodyou.feature.diary.ui.recipe

import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import com.maksimowiczm.foodyou.feature.diary.ui.search.SearchHome
import com.maksimowiczm.foodyou.navigation.crossfadeComposable
import kotlinx.serialization.Serializable
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun CreateRecipeApp(
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    val searchViewModel = koinViewModel<RecipeSearchViewModel>()
    val lazyListState = rememberLazyListState()

    NavHost(
        navController = navController,
        startDestination = CreateRecipeDialog,
        modifier = modifier
    ) {
        crossfadeComposable<CreateRecipeDialog> {
            CreateRecipeDialog(
                onClose = onClose,
                onAddIngredient = {
                    navController.navigate(
                        route = CreateRecipeSearch,
                        navOptions = navOptions {
                            launchSingleTop = true
                        }
                    )
                },
                onEditIngredient = {
                    // TODO
                }
            )
        }
        crossfadeComposable<CreateRecipeSearch> {
            SearchHome(
                onProductClick = {},
                onBack = { navController.popBackStack<CreateRecipeSearch>(inclusive = true) },
                onBarcodeScanner = {},
                viewModel = searchViewModel,
                lazyListState = lazyListState,
                searchHint = null
            )
        }
    }
}

@Serializable
data object CreateRecipeDialog

@Serializable
data object CreateRecipeSearch

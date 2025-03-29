package com.maksimowiczm.foodyou.feature.diary.ui.createrecipe.compose

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.maksimowiczm.foodyou.feature.diary.ui.createrecipe.CreateRecipeViewModel
import com.maksimowiczm.foodyou.navigation.crossfadeComposable
import org.koin.androidx.compose.koinViewModel

@Composable
fun CreateRecipeApp(
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    val viewModel = koinViewModel<CreateRecipeViewModel>()

    NavHost(
        navController = navController,
        startDestination = "start",
        modifier = modifier
    ) {
        crossfadeComposable("start") {
            CreateRecipeDialog(
                onClose = onClose,
                onAddIngredient = {
                },
                onProductClick = {
                    // TODO
                },
                onProductEdit = { id ->
                },
                viewModel = viewModel
            )
        }
        crossfadeComposable("search") {
            IngredientSearch()
        }
    }
}

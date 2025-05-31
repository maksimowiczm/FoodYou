package com.maksimowiczm.foodyou.feature.recipe.ui.create

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import com.maksimowiczm.foodyou.core.domain.model.FoodId
import com.maksimowiczm.foodyou.core.ui.ext.LaunchedCollectWithLifecycle
import com.maksimowiczm.foodyou.feature.recipe.ui.RecipeApp
import foodyou.app.generated.resources.*
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun CreateRecipeScreen(
    onBack: () -> Unit,
    onCreate: (FoodId.Recipe) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CreateRecipeViewModel = koinViewModel()
) {
    val latestOnCreate by rememberUpdatedState(onCreate)
    LaunchedCollectWithLifecycle(viewModel.eventBus) {
        when (it) {
            is CreateRecipeEvent.RecipeCreated -> latestOnCreate(it.id)
        }
    }

    RecipeApp(
        recipeId = null,
        titleRes = Res.string.headline_create_recipe,
        ingredients = emptyList(),
        onSave = viewModel::onSave,
        onBack = onBack,
        observedIngredients = viewModel::observeIngredients,
        modifier = modifier
    )
}

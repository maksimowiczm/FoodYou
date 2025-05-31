package com.maksimowiczm.foodyou.feature.recipe.ui.update

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.maksimowiczm.foodyou.core.domain.model.FoodId
import com.maksimowiczm.foodyou.core.ui.ext.LaunchedCollectWithLifecycle
import com.maksimowiczm.foodyou.feature.recipe.ui.RecipeApp
import com.maksimowiczm.foodyou.feature.recipe.ui.rememberRecipeFormState
import com.maksimowiczm.foodyou.feature.recipe.ui.toMinimalIngredient
import foodyou.app.generated.resources.*
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
internal fun UpdateRecipeScreen(
    recipeId: FoodId.Recipe,
    onBack: () -> Unit,
    onUpdate: () -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel = koinViewModel<UpdateRecipeViewModel>(
        parameters = { parametersOf(recipeId) }
    )

    val latestOnUpdate by rememberUpdatedState(onUpdate)
    LaunchedCollectWithLifecycle(viewModel.eventBus) {
        when (it) {
            is UpdateRecipeEvent.RecipeUpdated -> latestOnUpdate()
        }
    }

    val recipe = viewModel.recipe.collectAsStateWithLifecycle().value

    if (recipe == null) {
        // TODO loading state
        Surface(modifier) { Spacer(Modifier.fillMaxSize()) }
        return
    }

    val formState = rememberRecipeFormState(
        initialName = recipe.name,
        initialServings = recipe.servings
    )

    RecipeApp(
        recipeId = recipeId,
        titleRes = Res.string.headline_update_recipe,
        ingredients = recipe.ingredients.map { it.toMinimalIngredient() },
        formState = formState,
        onSave = viewModel::onSave,
        onBack = onBack,
        observedIngredients = viewModel::observeIngredients,
        modifier = modifier
    )
}

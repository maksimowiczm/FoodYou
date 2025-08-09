package com.maksimowiczm.foodyou.feature.food.recipe.ui

import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.maksimowiczm.foodyou.feature.food.recipe.presentation.UpdateRecipeEvent
import com.maksimowiczm.foodyou.feature.food.recipe.presentation.UpdateRecipeViewModel
import com.maksimowiczm.foodyou.feature.food.recipe.presentation.toMinimalIngredient
import com.maksimowiczm.foodyou.shared.common.domain.food.FoodId
import com.maksimowiczm.foodyou.shared.ui.BackHandler
import com.maksimowiczm.foodyou.shared.ui.DiscardDialog
import com.maksimowiczm.foodyou.shared.ui.ext.LaunchedCollectWithLifecycle
import foodyou.app.generated.resources.*
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun UpdateRecipeScreen(
    onBack: () -> Unit,
    onEditFood: (FoodId) -> Unit,
    onUpdate: () -> Unit,
    onUpdateUsdaApiKey: () -> Unit,
    recipeId: FoodId.Recipe,
    modifier: Modifier = Modifier,
) {
    val viewModel = koinViewModel<UpdateRecipeViewModel> { parametersOf(recipeId) }
    val latestOnUpdate by rememberUpdatedState(onUpdate)
    LaunchedCollectWithLifecycle(viewModel.events) {
        when (it) {
            UpdateRecipeEvent.Updated -> latestOnUpdate()
        }
    }

    val recipe = viewModel.recipe.collectAsStateWithLifecycle().value

    if (recipe == null) {
        // TODO loading state
        return
    }

    val formState =
        rememberRecipeFormState(
            initialName = recipe.name,
            initialServings = recipe.servings,
            initialNote = recipe.note,
            initialIsLiquid = recipe.isLiquid,
            initialIngredients = recipe.ingredients.map { it.toMinimalIngredient() },
        )
    val asRecipe =
        remember(formState.ingredients) { viewModel.intoRecipe(formState) }
            .collectAsStateWithLifecycle(null)
            .value

    var showDiscardDialog by rememberSaveable { mutableStateOf(false) }
    BackHandler(enabled = formState.isModified, onBack = { showDiscardDialog = true })
    if (showDiscardDialog) {
        DiscardDialog(
            onDismissRequest = { showDiscardDialog = false },
            onDiscard = {
                showDiscardDialog = false
                onBack()
            },
        ) {
            Text(stringResource(Res.string.question_discard_changes))
        }
    }

    RecipeApp(
        onBack = {
            if (formState.isModified) {
                showDiscardDialog = true
            } else {
                onBack()
            }
        },
        onSave = viewModel::update,
        onEditFood = onEditFood,
        onUpdateUsdaApiKey = onUpdateUsdaApiKey,
        state = formState,
        topBarTitle = stringResource(Res.string.headline_edit_recipe),
        mainRecipeId = recipeId,
        recipe = asRecipe,
        modifier = modifier,
    )
}

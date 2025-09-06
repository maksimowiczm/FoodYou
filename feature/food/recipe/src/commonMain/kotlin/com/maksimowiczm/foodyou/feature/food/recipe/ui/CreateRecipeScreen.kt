package com.maksimowiczm.foodyou.feature.food.recipe.ui

import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.maksimowiczm.foodyou.core.food.domain.entity.FoodId
import com.maksimowiczm.foodyou.feature.food.recipe.presentation.CreateRecipeEvent
import com.maksimowiczm.foodyou.feature.food.recipe.presentation.CreateRecipeViewModel
import com.maksimowiczm.foodyou.shared.ui.BackHandler
import com.maksimowiczm.foodyou.shared.ui.DiscardDialog
import com.maksimowiczm.foodyou.shared.ui.ext.LaunchedCollectWithLifecycle
import foodyou.app.generated.resources.*
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun CreateRecipeScreen(
    onBack: () -> Unit,
    onCreate: (FoodId.Recipe) -> Unit,
    onEditFood: (FoodId) -> Unit,
    onUpdateUsdaApiKey: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val viewModel = koinViewModel<CreateRecipeViewModel>()
    val latestOnCreate by rememberUpdatedState(onCreate)
    LaunchedCollectWithLifecycle(viewModel.events) {
        when (it) {
            is CreateRecipeEvent.Created -> latestOnCreate(it.recipeId)
        }
    }

    val formState =
        rememberRecipeFormState(
            initialName = "",
            initialServings = 1,
            initialNote = null,
            initialIsLiquid = false,
            initialIngredients = emptyList(),
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
            Text(stringResource(Res.string.question_discard_recipe))
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
        onSave = viewModel::create,
        onEditFood = onEditFood,
        onUpdateUsdaApiKey = onUpdateUsdaApiKey,
        state = formState,
        topBarTitle = stringResource(Res.string.headline_create_recipe),
        mainRecipeId = null,
        recipe = asRecipe,
        modifier = modifier,
    )
}

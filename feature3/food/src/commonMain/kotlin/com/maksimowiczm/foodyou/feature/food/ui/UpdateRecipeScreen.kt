package com.maksimowiczm.foodyou.feature.food.ui

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.maksimowiczm.foodyou.core.ui.BackHandler
import com.maksimowiczm.foodyou.core.ui.DiscardDialog
import com.maksimowiczm.foodyou.core.ui.ext.LaunchedCollectWithLifecycle
import com.maksimowiczm.foodyou.feature.food.domain.FoodId
import com.maksimowiczm.foodyou.feature.food.ui.recipe.RecipeApp
import com.maksimowiczm.foodyou.feature.food.ui.recipe.rememberRecipeFormState
import com.maksimowiczm.foodyou.feature.food.ui.recipe.toMinimalIngredient
import com.maksimowiczm.foodyou.feature.food.ui.recipe.update.UpdateRecipeEvent
import com.maksimowiczm.foodyou.feature.food.ui.recipe.update.UpdateRecipeViewModel
import foodyou.app.generated.resources.*
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun UpdateRecipeScreen(
    id: FoodId.Recipe,
    onBack: () -> Unit,
    onEditFood: (FoodId) -> Unit,
    onUpdate: () -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel = koinViewModel<UpdateRecipeViewModel> {
        parametersOf(id)
    }
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

    val formState = rememberRecipeFormState(
        initialName = recipe.name,
        initialServings = recipe.servings,
        initialNote = recipe.note,
        initialIsLiquid = recipe.isLiquid,
        initialIngredients = recipe.ingredients.map { it.toMinimalIngredient() }
    )
    val asRecipe = remember(formState.ingredients) {
        viewModel.intoRecipe(formState)
    }.collectAsStateWithLifecycle(null).value

    var showDiscardDialog by rememberSaveable { mutableStateOf(false) }
    BackHandler(
        enabled = formState.isModified,
        onBack = { showDiscardDialog = true }
    )
    if (showDiscardDialog) {
        DiscardDialog(
            onDismissRequest = {
                showDiscardDialog = false
            },
            onDiscard = {
                showDiscardDialog = false
                onBack()
            }
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
        state = formState,
        topBarTitle = stringResource(Res.string.headline_edit_recipe),
        mainRecipeId = id,
        recipe = asRecipe,
        modifier = modifier
    )
}

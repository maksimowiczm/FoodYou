package com.maksimowiczm.foodyou.app.ui.food.recipe.edit

import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigationevent.NavigationEventInfo
import androidx.navigationevent.compose.NavigationBackHandler
import androidx.navigationevent.compose.rememberNavigationEventState
import com.maksimowiczm.foodyou.app.ui.common.component.DiscardChangesDialog
import com.maksimowiczm.foodyou.app.ui.common.extension.LaunchedCollectWithLifecycle
import com.maksimowiczm.foodyou.app.ui.food.recipe.RecipeApp
import com.maksimowiczm.foodyou.app.ui.food.recipe.RecipeFormViewModel
import com.maksimowiczm.foodyou.app.ui.food.recipe.rememberRecipeFormState
import com.maksimowiczm.foodyou.common.domain.food.toQuantity
import com.maksimowiczm.foodyou.userproduct.domain.UserProductIdentity
import com.maksimowiczm.foodyou.userrecipe.domain.UserRecipeIdentity
import foodyou.app.generated.resources.*
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun EditRecipeScreen(
    identity: UserRecipeIdentity,
    onBack: () -> Unit,
    onEditUserProduct: (UserProductIdentity) -> Unit,
    onEditUserRecipe: (UserRecipeIdentity) -> Unit,
    modifier: Modifier = Modifier,
) {
    val viewModel: EditRecipeViewModel = koinViewModel { parametersOf(identity) }

    LaunchedCollectWithLifecycle(viewModel.uiEvents) {
        when (it) {
            is EditRecipeEvent.Updated -> onBack()
        }
    }

    val isLocked by viewModel.isLocked.collectAsStateWithLifecycle()
    val recipe = viewModel.recipe.collectAsStateWithLifecycle().value ?: return

    val initialIngredients =
        remember(recipe.identity) {
            recipe.composition.components.map { it.identity to it.quantity.toQuantity() }
        }
    val recipeFormViewModel: RecipeFormViewModel = koinViewModel {
        parametersOf(initialIngredients)
    }

    val uiState by recipeFormViewModel.state.collectAsStateWithLifecycle()

    val recipeFormState = rememberRecipeFormState(recipe)

    val isModified by
        remember(recipeFormState.isModified, uiState.ingredients, recipe.composition) {
            derivedStateOf {
                recipeFormState.isModified ||
                    uiState.ingredients.size != recipe.composition.components.size ||
                    uiState.ingredients.zip(recipe.composition.components).any { (current, original)
                        ->
                        current.entry.identity != original.identity ||
                            current.entry.quantity != original.quantity.toQuantity()
                    }
            }
        }

    var showDiscardDialog by rememberSaveable { mutableStateOf(false) }
    if (showDiscardDialog) {
        DiscardChangesDialog(onDismissRequest = { showDiscardDialog = false }, onDiscard = onBack) {
            Text(stringResource(Res.string.question_discard_recipe))
        }
    }

    NavigationBackHandler(
        state = rememberNavigationEventState(NavigationEventInfo.None),
        isBackEnabled = isModified && !isLocked,
        onBackCompleted = { showDiscardDialog = true },
    )

    RecipeApp(
        identity = recipe.identity,
        onBack = { if (isModified) showDiscardDialog = true else onBack() },
        onSave = { viewModel.save(form = recipeFormState, state = uiState) },
        onEditUserProduct = onEditUserProduct,
        onEditUserRecipe = onEditUserRecipe,
        recipeForm = recipeFormState,
        title = { Text(stringResource(Res.string.headline_edit_recipe)) },
        isLocked = isLocked,
        modifier = modifier,
    )
}

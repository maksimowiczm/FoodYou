package com.maksimowiczm.foodyou.features.userrecipe.edit

import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigationevent.NavigationEventInfo
import androidx.navigationevent.compose.NavigationBackHandler
import androidx.navigationevent.compose.rememberNavigationEventState
import com.maksimowiczm.foodyou.common.domain.food.FoodSnapshotId
import com.maksimowiczm.foodyou.common.domain.food.toQuantity
import com.maksimowiczm.foodyou.features.userrecipe.RecipeApp
import com.maksimowiczm.foodyou.features.userrecipe.RecipeFormViewModel
import com.maksimowiczm.foodyou.features.userrecipe.rememberRecipeFormState
import com.maksimowiczm.foodyou.shared.ui.component.DiscardChangesDialog
import com.maksimowiczm.foodyou.shared.ui.extension.LaunchedCollectWithLifecycle
import com.maksimowiczm.foodyou.userproduct.domain.UserProductId
import com.maksimowiczm.foodyou.userrecipe.domain.UserRecipeId
import foodyou.app.generated.resources.*
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun EditRecipeScreen(
    id: UserRecipeId,
    onBack: () -> Unit,
    onEditUserProduct: (UserProductId) -> Unit,
    onEditUserRecipe: (UserRecipeId) -> Unit,
    modifier: Modifier = Modifier,
) {
    val viewModel: EditRecipeViewModel = koinViewModel { parametersOf(id) }

    LaunchedCollectWithLifecycle(viewModel.uiEvents) {
        when (it) {
            is EditRecipeEvent.Updated -> onBack()
        }
    }

    val isLocked by viewModel.isLocked.collectAsStateWithLifecycle()
    val recipe = viewModel.recipe.collectAsStateWithLifecycle().value ?: return

    val initialIngredients =
        remember(recipe.components) {
            recipe.components
                .map {
                    when (it.id) {
                        is FoodSnapshotId.Anonymous -> TODO()
                        is FoodSnapshotId.Tracked -> it
                    }
                }
                .map { it.id to it.quantity.toQuantity() }
        }
    val recipeFormViewModel: RecipeFormViewModel = koinViewModel {
        parametersOf(initialIngredients)
    }

    val uiState by recipeFormViewModel.state.collectAsStateWithLifecycle()

    val recipeFormState = rememberRecipeFormState(recipe)

    val isModified by
        remember(recipeFormState.isModified, uiState.ingredients, recipe.components) {
            derivedStateOf {
                recipeFormState.isModified ||
                    uiState.ingredients.size != recipe.components.size ||
                    uiState.ingredients.zip(recipe.components).any { (current, original) ->
                        current.entry.id != original.id ||
                            current.entry.quantity != original.quantity.toQuantity()
                    }
            }
        }

    var showDiscardDialog by rememberSaveable { mutableStateOf(false) }
    if (showDiscardDialog) {
        DiscardChangesDialog(onDismissRequest = { showDiscardDialog = false }, onDiscard = onBack)
    }

    NavigationBackHandler(
        state = rememberNavigationEventState(NavigationEventInfo.None),
        isBackEnabled = isModified && !isLocked,
        onBackCompleted = { showDiscardDialog = true },
    )

    RecipeApp(
        id = recipe.id,
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

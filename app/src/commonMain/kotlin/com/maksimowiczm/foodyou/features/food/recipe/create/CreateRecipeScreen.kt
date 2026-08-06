package com.maksimowiczm.foodyou.features.food.recipe.create

import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigationevent.NavigationEventInfo
import androidx.navigationevent.compose.NavigationBackHandler
import androidx.navigationevent.compose.rememberNavigationEventState
import com.maksimowiczm.foodyou.features.food.recipe.RecipeApp
import com.maksimowiczm.foodyou.features.food.recipe.RecipeFormViewModel
import com.maksimowiczm.foodyou.features.food.recipe.rememberRecipeFormState
import com.maksimowiczm.foodyou.shared.ui.component.DiscardChangesDialog
import com.maksimowiczm.foodyou.shared.ui.extension.LaunchedCollectWithLifecycle
import com.maksimowiczm.foodyou.userproduct.domain.UserProductIdentity
import com.maksimowiczm.foodyou.userrecipe.domain.UserRecipeIdentity
import foodyou.app.generated.resources.*
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun CreateRecipeScreen(
    onBack: () -> Unit,
    onCreate: (UserRecipeIdentity) -> Unit,
    onEditUserProduct: (UserProductIdentity) -> Unit,
    onEditUserRecipe: (UserRecipeIdentity) -> Unit,
    modifier: Modifier = Modifier.Companion,
) {
    val viewModel: CreateRecipeViewModel = koinViewModel()
    val recipeFormViewModel: RecipeFormViewModel = koinViewModel()

    LaunchedCollectWithLifecycle(viewModel.uiEvents) {
        when (it) {
            is CreateRecipeEvent.Created -> onCreate(it.id)
        }
    }

    val isLocked = viewModel.isLocked.collectAsStateWithLifecycle().value
    val uiState = recipeFormViewModel.state.collectAsStateWithLifecycle().value

    val recipeFormState = rememberRecipeFormState(null)

    val isModified by
        remember(recipeFormState.isModified, uiState.ingredients) {
            derivedStateOf { recipeFormState.isModified || uiState.ingredients.isNotEmpty() }
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
        identity = null,
        onBack = { if (isModified) showDiscardDialog = true else onBack() },
        onSave = { viewModel.create(form = recipeFormState, state = uiState) },
        onEditUserProduct = onEditUserProduct,
        onEditUserRecipe = onEditUserRecipe,
        recipeForm = recipeFormState,
        title = { Text(stringResource(Res.string.headline_create_recipe)) },
        isLocked = isLocked,
        modifier = modifier,
    )
}

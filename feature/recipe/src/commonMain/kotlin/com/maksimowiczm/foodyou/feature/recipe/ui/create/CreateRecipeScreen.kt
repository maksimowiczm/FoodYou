package com.maksimowiczm.foodyou.feature.recipe.ui.create

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.maksimowiczm.foodyou.core.model.FoodId
import com.maksimowiczm.foodyou.core.ui.component.BackHandler
import com.maksimowiczm.foodyou.core.ui.ext.LaunchedCollectWithLifecycle
import com.maksimowiczm.foodyou.feature.recipe.ui.DiscardDialog
import com.maksimowiczm.foodyou.feature.recipe.ui.RecipeApp
import com.maksimowiczm.foodyou.feature.recipe.ui.rememberRecipeFormState
import foodyou.app.generated.resources.*
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun CreateRecipeScreen(
    onBack: () -> Unit,
    onCreate: (FoodId.Recipe) -> Unit,
    onEditFood: (FoodId) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CreateRecipeViewModel = koinViewModel()
) {
    val latestOnCreate by rememberUpdatedState(onCreate)
    LaunchedCollectWithLifecycle(viewModel.eventBus) {
        when (it) {
            is CreateRecipeEvent.RecipeCreated -> latestOnCreate(it.id)
        }
    }

    val formState = rememberRecipeFormState(
        initialName = "",
        initialServings = 1,
        initialNote = "",
        initialIsLiquid = false,
        initialIngredients = emptyList()
    )

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
            Text(stringResource(Res.string.question_discard_recipe))
        }
    }

    RecipeApp(
        recipeId = null,
        titleRes = Res.string.headline_create_recipe,
        observedIngredients = viewModel::observeIngredients,
        onSave = viewModel::onSave,
        onBack = {
            if (formState.isModified) {
                showDiscardDialog = true
            } else {
                onBack()
            }
        },
        onEditFood = onEditFood,
        modifier = modifier,
        formState = formState
    )
}

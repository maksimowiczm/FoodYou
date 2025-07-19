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
import com.maksimowiczm.foodyou.feature.food.ui.recipe.create.CreateRecipeEvent
import com.maksimowiczm.foodyou.feature.food.ui.recipe.create.CreateRecipeViewModel
import com.maksimowiczm.foodyou.feature.food.ui.recipe.rememberRecipeFormState
import foodyou.app.generated.resources.*
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun CreateRecipeScreen(
    onBack: () -> Unit,
    onCreate: (FoodId.Recipe) -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel = koinViewModel<CreateRecipeViewModel>()
    val latestOnCreate by rememberUpdatedState(onCreate)
    LaunchedCollectWithLifecycle(viewModel.events) {
        when (it) {
            is CreateRecipeEvent.Created -> latestOnCreate(it.recipeId)
        }
    }

    val formState = rememberRecipeFormState(
        initialName = "",
        initialServings = 1,
        initialNote = null,
        initialIngredients = emptyList()
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
        state = formState,
        topBarTitle = stringResource(Res.string.headline_create_recipe),
        mainRecipeId = null,
        recipe = asRecipe,
        modifier = modifier
    )
}

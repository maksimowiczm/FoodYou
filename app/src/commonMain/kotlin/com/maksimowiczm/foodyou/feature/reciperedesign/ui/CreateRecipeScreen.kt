package com.maksimowiczm.foodyou.feature.reciperedesign.ui

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.maksimowiczm.foodyou.core.navigation.forwardBackwardComposable
import com.maksimowiczm.foodyou.core.ui.component.ArrowBackIconButton
import foodyou.app.generated.resources.Res
import foodyou.app.generated.resources.action_save
import foodyou.app.generated.resources.headline_create_recipe
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
internal fun CreateRecipeScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CreateRecipeViewModel = koinInject()
) {
    val navController = rememberNavController()

    val formState = rememberRecipeFormState()
    val ingredients = viewModel.ingredients.collectAsStateWithLifecycle().value

    NavHost(
        navController = navController,
        startDestination = "form",
        modifier = modifier
    ) {
        forwardBackwardComposable("form") {
            FormContent(
                ingredients = ingredients,
                formState = formState,
                onBack = onBack,
                onAddIngredient = {
                    navController.navigate("search") {
                        launchSingleTop = true
                    }
                },
                onSave = {
                    // TODO
                }
            )
        }
        forwardBackwardComposable("search") {
            IngredientsSearchScreen(
                onBack = {
                    navController.popBackStack("search", inclusive = true)
                },
                onIngredient = {
                    // TODO
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FormContent(
    ingredients: List<Ingredient>,
    formState: RecipeFormState,
    onBack: () -> Unit,
    onAddIngredient: () -> Unit,
    onSave: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Text(stringResource(Res.string.headline_create_recipe))
                },
                navigationIcon = {
                    ArrowBackIconButton(onBack)
                },
                actions = {
                    FilledIconButton(
                        onClick = onSave,
                        enabled = formState.isValid && ingredients.isNotEmpty()
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Save,
                            contentDescription = stringResource(Res.string.action_save)
                        )
                    }
                },
                scrollBehavior = scrollBehavior
            )
        }
    ) { contentPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .nestedScroll(scrollBehavior.nestedScrollConnection),
            contentPadding = contentPadding
        ) {
            item {
                RecipeForm(
                    ingredients = ingredients,
                    onAddIngredient = onAddIngredient,
                    formState = formState,
                    contentPadding = PaddingValues(horizontal = 16.dp)
                )
            }
        }
    }
}

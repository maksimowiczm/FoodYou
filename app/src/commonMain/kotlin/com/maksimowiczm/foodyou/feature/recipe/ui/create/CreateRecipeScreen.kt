package com.maksimowiczm.foodyou.feature.recipe.ui.create

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.maksimowiczm.foodyou.core.domain.model.FoodId
import com.maksimowiczm.foodyou.core.navigation.forwardBackwardComposable
import com.maksimowiczm.foodyou.core.ui.component.ArrowBackIconButton
import com.maksimowiczm.foodyou.core.ui.ext.LaunchedCollectWithLifecycle
import com.maksimowiczm.foodyou.feature.recipe.domain.Ingredient
import com.maksimowiczm.foodyou.feature.recipe.ui.IngredientListItem
import com.maksimowiczm.foodyou.feature.recipe.ui.MinimalIngredient
import com.maksimowiczm.foodyou.feature.recipe.ui.RecipeForm
import com.maksimowiczm.foodyou.feature.recipe.ui.RecipeFormState
import com.maksimowiczm.foodyou.feature.recipe.ui.measure.MeasureIngredientScreen
import com.maksimowiczm.foodyou.feature.recipe.ui.rememberRecipeFormState
import com.maksimowiczm.foodyou.feature.recipe.ui.search.IngredientsSearchScreen
import foodyou.app.generated.resources.*
import kotlin.collections.plus
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
internal fun CreateRecipeScreen(
    onBack: () -> Unit,
    onCreate: (FoodId.Recipe) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CreateRecipeViewModel = koinInject()
) {
    val latestOnCreate by rememberUpdatedState(onCreate)
    LaunchedCollectWithLifecycle(viewModel.eventBus) {
        when (it) {
            is CreateRecipeEvent.RecipeCreated -> latestOnCreate(it.id)
        }
    }

    val navController = rememberNavController()

    var ingredientsState = rememberSaveable(
        stateSaver = MinimalIngredient.Companion.ListSaver
    ) {
        mutableStateOf<List<MinimalIngredient>>(emptyList())
    }

    val formState = rememberRecipeFormState()
    val ingredients = viewModel
        .observeIngredients(ingredientsState.value)
        .collectAsStateWithLifecycle(emptyList()).value

    NavHost(
        navController = navController,
        startDestination = Form,
        modifier = modifier
    ) {
        forwardBackwardComposable<Form> {
            FormContent(
                ingredients = ingredients,
                formState = formState,
                onBack = onBack,
                onAddIngredient = {
                    navController.navigate(Search) {
                        launchSingleTop = true
                    }
                },
                onEditIngredient = { ingredient ->
                    // TODO
                },
                onRemoveIngredient = { ingredient ->
                    ingredientsState.value = ingredientsState.value.toMutableList().apply {
                        removeAt(ingredient)
                    }
                },
                onSave = {
                    viewModel.onSave(
                        name = formState.nameState.value,
                        servings = formState.servingsState.value,
                        ingredients = ingredients.toList()
                    )
                }
            )
        }
        forwardBackwardComposable<Search> {
            IngredientsSearchScreen(
                onBack = {
                    navController.popBackStack(Search, inclusive = true)
                },
                onIngredient = {
                    val route = Measure(it.food.id)

                    navController.navigate(route) {
                        launchSingleTop = true

                        popUpTo<Search> {
                            saveState = true
                        }
                    }
                }
            )
        }
        forwardBackwardComposable<Measure> {
            val route = it.toRoute<Measure>()
            val foodId = route.foodId

            MeasureIngredientScreen(
                foodId = route.foodId,
                selected = null,
                onBack = {
                    navController.popBackStack<Measure>(inclusive = true)
                },
                onMeasurement = { measurement ->
                    ingredientsState.value = ingredientsState.value + MinimalIngredient(
                        foodId = foodId,
                        measurement = measurement
                    )
                    navController.popBackStack<Search>(inclusive = true)
                }
            )
        }
    }
}

@Serializable
private data object Form

@Serializable
private data object Search

@Serializable
data class Measure(val productId: Long?, val recipeId: Long?) {
    constructor(foodId: FoodId) : this(
        productId = (foodId as? FoodId.Product)?.id,
        recipeId = (foodId as? FoodId.Recipe)?.id
    )

    init {
        require(productId != null || recipeId != null) {
            "Either productId or recipeId must be provided"
        }
    }

    val foodId
        get() = when {
            productId != null -> FoodId.Product(productId)
            recipeId != null -> FoodId.Recipe(recipeId)
            else -> error("Either productId or recipeId must be provided")
        }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FormContent(
    ingredients: List<Ingredient>,
    formState: RecipeFormState,
    onBack: () -> Unit,
    onAddIngredient: () -> Unit,
    onEditIngredient: (Ingredient) -> Unit,
    onRemoveIngredient: (index: Int) -> Unit,
    onSave: () -> Unit,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    var selectedIngredientIndex = rememberSaveable {
        mutableStateOf<Int?>(null)
    }
    val selectedIngredient = remember(selectedIngredientIndex.value, ingredients) {
        selectedIngredientIndex.value?.let { index ->
            ingredients.getOrNull(index)
        }
    }

    if (selectedIngredient != null) {
        val sheetState = rememberModalBottomSheetState()

        ModalBottomSheet(
            onDismissRequest = { selectedIngredientIndex.value = null },
            sheetState = sheetState
        ) {
            IngredientListItem(
                ingredient = selectedIngredient,
                onClick = null
            )
            HorizontalDivider(Modifier.padding(horizontal = 16.dp))
            ListItem(
                headlineContent = {
                    Text(stringResource(Res.string.action_edit_ingredient_measurement))
                },
                modifier = Modifier.clickable {
                    coroutineScope.launch {
                        onEditIngredient(selectedIngredient)
                        sheetState.hide()
                        selectedIngredientIndex.value = null
                    }
                },
                leadingContent = {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = null
                    )
                },
                colors = ListItemDefaults.colors(
                    containerColor = Color.Transparent
                )
            )
            ListItem(
                headlineContent = {
                    Text(stringResource(Res.string.action_delete_ingredient))
                },
                modifier = Modifier.clickable {
                    coroutineScope.launch {
                        sheetState.hide()
                        selectedIngredientIndex.value?.let { onRemoveIngredient(it) }
                        selectedIngredientIndex.value = null
                    }
                },
                leadingContent = {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = null
                    )
                },
                colors = ListItemDefaults.colors(
                    containerColor = Color.Transparent
                )
            )
        }
    }

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
                    onIngredientClick = { index ->
                        selectedIngredientIndex.value = index
                    },
                    formState = formState,
                    contentPadding = PaddingValues(horizontal = 16.dp)
                )
            }
        }
    }
}

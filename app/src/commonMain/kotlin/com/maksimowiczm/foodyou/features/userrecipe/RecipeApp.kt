package com.maksimowiczm.foodyou.features.userrecipe

import androidx.compose.animation.ContentTransform
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import androidx.savedstate.serialization.SavedStateConfiguration
import com.maksimowiczm.foodyou.app.navigation.ForwardBackwardTransition
import com.maksimowiczm.foodyou.app.navigation.rememberPredictiveBackRoundedCornersDecorator
import com.maksimowiczm.foodyou.common.domain.food.FoodSnapshotId
import com.maksimowiczm.foodyou.common.domain.food.Quantity
import com.maksimowiczm.foodyou.common.extension.removeLastIf
import com.maksimowiczm.foodyou.common.extension.removeWhile
import com.maksimowiczm.foodyou.features.userrecipe.ingredient.AddFoodDataCentralIngredientScreen
import com.maksimowiczm.foodyou.features.userrecipe.ingredient.AddOpenFoodFactsIngredientScreen
import com.maksimowiczm.foodyou.features.userrecipe.ingredient.AddUserProductIngredientScreen
import com.maksimowiczm.foodyou.features.userrecipe.ingredient.AddUserRecipeIngredientScreen
import com.maksimowiczm.foodyou.fooddatacentral.domain.FoodDataCentralProductIdentity
import com.maksimowiczm.foodyou.openfoodfacts.domain.OpenFoodFactsProductIdentity
import com.maksimowiczm.foodyou.shared.ui.component.ArrowBackIconButton
import com.maksimowiczm.foodyou.shared.ui.extension.add
import com.maksimowiczm.foodyou.userproduct.domain.UserProductIdentity
import com.maksimowiczm.foodyou.userrecipe.domain.UserRecipeIdentity
import foodyou.app.generated.resources.*
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun RecipeApp(
    identity: UserRecipeIdentity?,
    onBack: () -> Unit,
    onSave: () -> Unit,
    onEditUserProduct: (UserProductIdentity) -> Unit,
    onEditUserRecipe: (UserRecipeIdentity) -> Unit,
    recipeForm: RecipeFormState,
    title: @Composable () -> Unit,
    isLocked: Boolean,
    modifier: Modifier = Modifier,
) {
    val recipeFormViewModel: RecipeFormViewModel = koinViewModel()
    val backStack = rememberNavBackStack(config, RecipeForm)

    NavDisplay(
        modifier = modifier,
        backStack = backStack,
        entryDecorators =
            listOf(
                rememberSaveableStateHolderNavEntryDecorator(),
                rememberViewModelStoreNavEntryDecorator(),
                rememberPredictiveBackRoundedCornersDecorator(),
            ),
        transitionSpec = {
            ContentTransform(
                ForwardBackwardTransition.enterTransition(),
                ForwardBackwardTransition.exitTransition(),
            )
        },
        popTransitionSpec = {
            ContentTransform(
                ForwardBackwardTransition.popEnterTransition(),
                ForwardBackwardTransition.popExitTransition(),
            )
        },
        predictivePopTransitionSpec = {
            ContentTransform(
                ForwardBackwardTransition.popEnterTransition(),
                ForwardBackwardTransition.popExitTransition(),
            )
        },
        entryProvider =
            entryProvider {
                entry<RecipeForm> {
                    RecipeFormScreen(
                        onBack = onBack,
                        onSave = onSave,
                        onAddIngredient = { backStack.add(Search) },
                        onIngredientClick = { index ->
                            val entry = recipeFormViewModel.state.value.ingredients[index].entry

                            val backStackEntry =
                                when (val id = entry.identity) {
                                    is FoodSnapshotId.FoodDataCentral ->
                                        EditFoodDataCentral(
                                            index,
                                            FoodDataCentralProductIdentity(id.fdcId),
                                            entry.quantity,
                                        )

                                    is FoodSnapshotId.OpenFoodFacts ->
                                        EditOpenFoodFacts(
                                            index,
                                            OpenFoodFactsProductIdentity(id.barcode),
                                            entry.quantity,
                                        )

                                    is FoodSnapshotId.UserProduct ->
                                        EditUserProduct(
                                            index,
                                            UserProductIdentity(id.id),
                                            entry.quantity,
                                        )

                                    is FoodSnapshotId.UserRecipe ->
                                        EditUserRecipe(
                                            index,
                                            UserRecipeIdentity(id.id),
                                            entry.quantity,
                                        )
                                }

                            backStack.add(backStackEntry)
                        },
                        recipeForm = recipeForm,
                        recipeFormViewModel = recipeFormViewModel,
                        title = title,
                        isLocked = isLocked,
                    )
                }
                entry<Search> {
                    IngredientSearchScreen(
                        recipeIdentity = identity,
                        onBack = { backStack.removeLastIf<Search>() },
                        onFoodDataCentralProduct = { id, quantity ->
                            backStack.add(FoodDataCentral(id, quantity))
                        },
                        onOpenFoodFactsProduct = { id, quantity ->
                            backStack.add(OpenFoodFacts(id, quantity))
                        },
                        onUserProduct = { id, quantity ->
                            backStack.add(UserProduct(id, quantity))
                        },
                        onUserRecipe = { id, quantity -> backStack.add(UserRecipe(id, quantity)) },
                    )
                }
                entry<OpenFoodFacts> {
                    AddOpenFoodFactsIngredientScreen(
                        onBack = { backStack.removeLastIf<OpenFoodFacts>() },
                        onAdd = { quantity ->
                            recipeFormViewModel.addIngredient(
                                FoodSnapshotId.OpenFoodFacts(it.identity.barcode),
                                quantity,
                            )
                            backStack.removeWhile { r -> r !is RecipeForm }
                        },
                        identity = it.identity,
                        initialQuantity = it.quantity,
                    )
                }
                entry<FoodDataCentral> {
                    AddFoodDataCentralIngredientScreen(
                        onBack = { backStack.removeLastIf<FoodDataCentral>() },
                        onAdd = { quantity ->
                            recipeFormViewModel.addIngredient(
                                FoodSnapshotId.FoodDataCentral(it.identity.fdcId),
                                quantity,
                            )
                            backStack.removeWhile { r -> r !is RecipeForm }
                        },
                        identity = it.identity,
                        initialQuantity = it.quantity,
                    )
                }
                entry<UserProduct> {
                    AddUserProductIngredientScreen(
                        onBack = { backStack.removeLastIf<UserProduct>() },
                        onAdd = { quantity ->
                            recipeFormViewModel.addIngredient(
                                FoodSnapshotId.UserProduct(it.identity.id),
                                quantity,
                            )
                            backStack.removeWhile { r -> r !is RecipeForm }
                        },
                        onEdit = { onEditUserProduct(it.identity) },
                        onDelete = { backStack.removeLastIf<UserProduct>() },
                        identity = it.identity,
                        initialQuantity = it.quantity,
                    )
                }
                entry<UserRecipe> {
                    AddUserRecipeIngredientScreen(
                        onBack = { backStack.removeLastIf<UserRecipe>() },
                        onAdd = { quantity ->
                            recipeFormViewModel.addIngredient(
                                FoodSnapshotId.UserRecipe(it.identity.id),
                                quantity,
                            )
                            backStack.removeWhile { r -> r !is RecipeForm }
                        },
                        onEdit = { onEditUserRecipe(it.identity) },
                        onDelete = { backStack.removeLastIf<UserRecipe>() },
                        identity = it.identity,
                        initialQuantity = it.quantity,
                        onNavigateToIngredient = { identity, quantity ->
                            val route =
                                when (identity) {
                                    is FoodSnapshotId.UserProduct ->
                                        UserProduct(UserProductIdentity(identity.id), quantity)

                                    is FoodSnapshotId.OpenFoodFacts ->
                                        OpenFoodFacts(
                                            OpenFoodFactsProductIdentity(identity.barcode),
                                            quantity,
                                        )

                                    is FoodSnapshotId.FoodDataCentral ->
                                        FoodDataCentral(
                                            FoodDataCentralProductIdentity(identity.fdcId),
                                            quantity,
                                        )

                                    is FoodSnapshotId.UserRecipe ->
                                        UserRecipe(UserRecipeIdentity(identity.id), quantity)

                                    is FoodSnapshotId.Anonymous -> TODO()
                                }
                            backStack.add(route)
                        },
                    )
                }
                entry<EditOpenFoodFacts> {
                    AddOpenFoodFactsIngredientScreen(
                        onBack = { backStack.removeLastIf<EditOpenFoodFacts>() },
                        onAdd = { quantity ->
                            recipeFormViewModel.updateIngredient(it.index, quantity)
                            backStack.removeWhile { r -> r !is RecipeForm }
                        },
                        identity = it.identity,
                        initialQuantity = it.quantity,
                    )
                }
                entry<EditFoodDataCentral> {
                    AddFoodDataCentralIngredientScreen(
                        onBack = { backStack.removeLastIf<EditFoodDataCentral>() },
                        onAdd = { quantity ->
                            recipeFormViewModel.updateIngredient(it.index, quantity)
                            backStack.removeWhile { r -> r !is RecipeForm }
                        },
                        identity = it.identity,
                        initialQuantity = it.quantity,
                    )
                }
                entry<EditUserProduct> {
                    AddUserProductIngredientScreen(
                        onBack = { backStack.removeLastIf<EditUserProduct>() },
                        onAdd = { quantity ->
                            recipeFormViewModel.updateIngredient(it.index, quantity)
                            backStack.removeWhile { r -> r !is RecipeForm }
                        },
                        onEdit = { onEditUserProduct(it.identity) },
                        onDelete = {
                            recipeFormViewModel.removeIngredient(it.index)
                            backStack.removeWhile { r -> r !is RecipeForm }
                        },
                        identity = it.identity,
                        initialQuantity = it.quantity,
                    )
                }
                entry<EditUserRecipe> {
                    AddUserRecipeIngredientScreen(
                        onBack = { backStack.removeLastIf<EditUserRecipe>() },
                        onAdd = { quantity ->
                            recipeFormViewModel.updateIngredient(it.index, quantity)
                            backStack.removeWhile { r -> r !is RecipeForm }
                        },
                        onEdit = { onEditUserRecipe(it.identity) },
                        onDelete = {
                            recipeFormViewModel.removeIngredient(it.index)
                            backStack.removeWhile { r -> r !is RecipeForm }
                        },
                        identity = it.identity,
                        initialQuantity = it.quantity,
                        onNavigateToIngredient = { identity, quantity ->
                            val route =
                                when (identity) {
                                    is FoodSnapshotId.UserProduct ->
                                        UserProduct(UserProductIdentity(identity.id), quantity)

                                    is FoodSnapshotId.OpenFoodFacts ->
                                        OpenFoodFacts(
                                            OpenFoodFactsProductIdentity(identity.barcode),
                                            quantity,
                                        )

                                    is FoodSnapshotId.FoodDataCentral ->
                                        FoodDataCentral(
                                            FoodDataCentralProductIdentity(identity.fdcId),
                                            quantity,
                                        )

                                    is FoodSnapshotId.UserRecipe ->
                                        UserRecipe(UserRecipeIdentity(identity.id), quantity)

                                    is FoodSnapshotId.Anonymous -> TODO()
                                }
                            backStack.add(route)
                        },
                    )
                }
            },
    )
}

@Composable
private fun RecipeFormScreen(
    onBack: () -> Unit,
    onSave: () -> Unit,
    onAddIngredient: () -> Unit,
    onIngredientClick: (Int) -> Unit,
    recipeForm: RecipeFormState,
    recipeFormViewModel: RecipeFormViewModel,
    title: @Composable () -> Unit,
    isLocked: Boolean,
    modifier: Modifier = Modifier,
) {
    val uiState by recipeFormViewModel.state.collectAsStateWithLifecycle()

    val isValid by
        remember(recipeForm.isValid, uiState.ingredients) {
            derivedStateOf { recipeForm.isValid && uiState.ingredients.isNotEmpty() }
        }

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = title,
                navigationIcon = { ArrowBackIconButton(onClick = onBack, enabled = !isLocked) },
                actions = {
                    FilledIconButton(
                        onClick = onSave,
                        shapes = IconButtonDefaults.shapes(),
                        modifier =
                            Modifier.size(
                                IconButtonDefaults.smallContainerSize(
                                    IconButtonDefaults.IconButtonWidthOption.Wide
                                )
                            ),
                        enabled = isValid && !isLocked,
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Check,
                            contentDescription = stringResource(Res.string.action_save),
                            modifier = Modifier.size(IconButtonDefaults.smallIconSize),
                        )
                    }
                },
                colors =
                    TopAppBarDefaults.topAppBarColors(
                        scrolledContainerColor = MaterialTheme.colorScheme.surfaceContainerHighest
                    ),
                scrollBehavior = scrollBehavior,
            )
        },
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().imePadding(),
            contentPadding = paddingValues.add(vertical = 8.dp),
        ) {
            item {
                RecipeForm(
                    state = recipeForm,
                    isLocked = isLocked,
                    contentPadding = PaddingValues(horizontal = 8.dp),
                    onAddIngredient = onAddIngredient,
                    onIngredientClick = onIngredientClick,
                    viewModel = recipeFormViewModel,
                )
            }
        }
    }
}

@OptIn(ExperimentalSerializationApi::class)
private val config = SavedStateConfiguration {
    serializersModule = SerializersModule {
        polymorphic(NavKey::class) { subclassesOfSealed<RecipeNavKey>() }
    }
}

@Serializable private sealed interface RecipeNavKey : NavKey

@Serializable private data object RecipeForm : RecipeNavKey

@Serializable private data object Search : RecipeNavKey

@Serializable
private data class OpenFoodFacts(
    val identity: OpenFoodFactsProductIdentity,
    val quantity: Quantity,
) : RecipeNavKey

@Serializable
private data class FoodDataCentral(
    val identity: FoodDataCentralProductIdentity,
    val quantity: Quantity,
) : RecipeNavKey

@Serializable
private data class UserProduct(val identity: UserProductIdentity, val quantity: Quantity) :
    RecipeNavKey

@Serializable
private data class UserRecipe(val identity: UserRecipeIdentity, val quantity: Quantity) :
    RecipeNavKey

@Serializable
private data class EditOpenFoodFacts(
    val index: Int,
    val identity: OpenFoodFactsProductIdentity,
    val quantity: Quantity,
) : RecipeNavKey

@Serializable
private data class EditFoodDataCentral(
    val index: Int,
    val identity: FoodDataCentralProductIdentity,
    val quantity: Quantity,
) : RecipeNavKey

@Serializable
private data class EditUserProduct(
    val index: Int,
    val identity: UserProductIdentity,
    val quantity: Quantity,
) : RecipeNavKey

@Serializable
private data class EditUserRecipe(
    val index: Int,
    val identity: UserRecipeIdentity,
    val quantity: Quantity,
) : RecipeNavKey

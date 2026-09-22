package com.maksimowiczm.foodyou.features.diary

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.maksimowiczm.foodyou.common.domain.food.FoodSnapshotId
import com.maksimowiczm.foodyou.common.domain.food.Quantity
import com.maksimowiczm.foodyou.fooddatacentral.domain.toFoodDataCentralProductId
import com.maksimowiczm.foodyou.fooddiary.domain.FoodDiaryEntryId
import com.maksimowiczm.foodyou.mealplan.domain.MealId
import com.maksimowiczm.foodyou.openfoodfacts.domain.toOpenFoodFactsProductId
import com.maksimowiczm.foodyou.shared.ui.component.LoadingScreen
import com.maksimowiczm.foodyou.shared.ui.extension.LaunchedCollectWithLifecycle
import com.maksimowiczm.foodyou.userproduct.domain.UserProductId
import com.maksimowiczm.foodyou.userproduct.domain.toUserProductId
import com.maksimowiczm.foodyou.userrecipe.domain.UserRecipeId
import com.maksimowiczm.foodyou.userrecipe.domain.toUserRecipeId
import kotlin.time.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
context(animatedContentScope: AnimatedContentScope)
fun UpdateFoodDiaryEntryScreenDispatcher(
    onBack: () -> Unit,
    onUpdate: () -> Unit,
    onEditUserProduct: (UserProductId) -> Unit,
    onDeleteUserProduct: () -> Unit,
    onEditUserRecipe: (UserRecipeId) -> Unit,
    onDeleteUserRecipe: () -> Unit,
    onNavigateToIngredient: (FoodSnapshotId, Quantity, MealId?, Instant) -> Unit,
    entryIdentity: FoodDiaryEntryId,
    modifier: Modifier = Modifier,
) {
    val viewModel: UpdateFoodDiaryEntryViewModel = koinViewModel { parametersOf(entryIdentity) }

    LaunchedCollectWithLifecycle(viewModel.updatedEvent) { onUpdate() }

    when (val uiState = viewModel.uiState.collectAsStateWithLifecycle().value) {
        UpdateFoodDiaryEntryUiState.Loading -> LoadingScreen(onBack, modifier)

        is UpdateFoodDiaryEntryUiState.Ready ->
            when (val id = uiState.entry.snapshot.id) {
                is FoodSnapshotId.UserRecipe ->
                    UpdateUserRecipeDiaryEntryScreen(
                        onBack = onBack,
                        onSave = viewModel::update,
                        onEdit = { onEditUserRecipe(id.toUserRecipeId()) },
                        onDelete = onDeleteUserRecipe,
                        onNavigateToIngredient = { snapshotId, quantity ->
                            onNavigateToIngredient(
                                snapshotId,
                                quantity,
                                uiState.selectedMeal.id,
                                uiState.selectedDateTime.toInstant(TimeZone.currentSystemDefault()),
                            )
                        },
                        foodId = id.toUserRecipeId(),
                        entry = uiState.entry,
                        profiles = uiState.profiles,
                        meals = uiState.meals,
                        selectedMeal = uiState.selectedMeal,
                        selectedDateTime = uiState.selectedDateTime,
                        selectedProfileIds = uiState.selectedProfileIds,
                        onSelectMeal = { viewModel.selectMeal(it.id) },
                        onSelectDate = viewModel::selectDate,
                        onSelectTime = viewModel::selectTime,
                        onSelectProfiles = {
                            viewModel.selectProfiles(it.map { profile -> profile.id })
                        },
                        modifier = modifier,
                    )

                is FoodSnapshotId.Anonymous ->
                    UpdateAnonymousFoodDiaryEntryScreen(
                        onBack = onBack,
                        onRelink = viewModel::relink,
                        onSave = { quantity -> viewModel.update(quantity, isTracked = false) },
                        entry = uiState.entry,
                        profiles = uiState.profiles,
                        meals = uiState.meals,
                        selectedMeal = uiState.selectedMeal,
                        selectedDateTime = uiState.selectedDateTime,
                        selectedProfileIds = uiState.selectedProfileIds,
                        onSelectMeal = { viewModel.selectMeal(it.id) },
                        onSelectDate = viewModel::selectDate,
                        onSelectTime = viewModel::selectTime,
                        onSelectProfiles = {
                            viewModel.selectProfiles(it.map { profile -> profile.id })
                        },
                        modifier = modifier,
                    )

                is FoodSnapshotId.FoodDataCentral ->
                    UpdateFoodDataCentralDiaryEntryScreen(
                        onBack = onBack,
                        onSave = viewModel::update,
                        foodId = id.toFoodDataCentralProductId(),
                        entry = uiState.entry,
                        profiles = uiState.profiles,
                        meals = uiState.meals,
                        selectedMeal = uiState.selectedMeal,
                        selectedDateTime = uiState.selectedDateTime,
                        selectedProfileIds = uiState.selectedProfileIds,
                        onSelectMeal = { viewModel.selectMeal(it.id) },
                        onSelectDate = viewModel::selectDate,
                        onSelectTime = viewModel::selectTime,
                        onSelectProfiles = {
                            viewModel.selectProfiles(it.map { profile -> profile.id })
                        },
                        modifier = modifier,
                    )

                is FoodSnapshotId.OpenFoodFacts ->
                    UpdateOpenFoodFactsDiaryEntryScreen(
                        onBack = onBack,
                        onSave = viewModel::update,
                        foodId = id.toOpenFoodFactsProductId(),
                        entry = uiState.entry,
                        profiles = uiState.profiles,
                        meals = uiState.meals,
                        selectedMeal = uiState.selectedMeal,
                        selectedDateTime = uiState.selectedDateTime,
                        selectedProfileIds = uiState.selectedProfileIds,
                        onSelectMeal = { viewModel.selectMeal(it.id) },
                        onSelectDate = viewModel::selectDate,
                        onSelectTime = viewModel::selectTime,
                        onSelectProfiles = {
                            viewModel.selectProfiles(it.map { profile -> profile.id })
                        },
                        modifier = modifier,
                    )

                is FoodSnapshotId.UserProduct ->
                    UpdateUserProductDiaryEntryScreen(
                        onBack = onBack,
                        onSave = viewModel::update,
                        onEdit = { onEditUserProduct(id.toUserProductId()) },
                        onDelete = onDeleteUserProduct,
                        foodId = id.toUserProductId(),
                        entry = uiState.entry,
                        profiles = uiState.profiles,
                        meals = uiState.meals,
                        selectedMeal = uiState.selectedMeal,
                        selectedDateTime = uiState.selectedDateTime,
                        selectedProfileIds = uiState.selectedProfileIds,
                        onSelectMeal = { viewModel.selectMeal(it.id) },
                        onSelectDate = viewModel::selectDate,
                        onSelectTime = viewModel::selectTime,
                        onSelectProfiles = {
                            viewModel.selectProfiles(it.map { profile -> profile.id })
                        },
                        modifier = modifier,
                    )
            }
    }
}

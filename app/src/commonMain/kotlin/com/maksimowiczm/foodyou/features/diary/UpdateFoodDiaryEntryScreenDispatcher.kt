package com.maksimowiczm.foodyou.features.diary

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
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
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
    val entry = viewModel.entry.collectAsStateWithLifecycle().value
    val profiles = viewModel.profiles.collectAsStateWithLifecycle().value

    LaunchedCollectWithLifecycle(viewModel.updatedEvent) { onUpdate() }

    when {
        profiles == null || entry == null -> LoadingScreen(onBack, modifier)
        else ->
            when (val id = entry.snapshot.id) {
                is FoodSnapshotId.UserRecipe ->
                    UpdateUserRecipeDiaryEntryScreen(
                        onBack = onBack,
                        onSave = viewModel::update,
                        onEdit = { onEditUserRecipe(id.toUserRecipeId()) },
                        onDelete = onDeleteUserRecipe,
                        onNavigateToIngredient = { id, quantity ->
                            onNavigateToIngredient(id, quantity, entry.mealId, entry.timestamp)
                        },
                        foodId = id.toUserRecipeId(),
                        entry = entry,
                        profiles = profiles,
                        modifier = modifier,
                    )

                is FoodSnapshotId.Anonymous ->
                    UpdateAnonymousFoodDiaryEntryScreen(
                        onBack = onBack,
                        onRelink = { profiles, timestamp, relinkedSnapshot ->
                            viewModel.relink(
                                profiles = profiles,
                                timestamp = timestamp,
                                relinkedSnapshot = relinkedSnapshot,
                            )
                        },
                        onSave = { quantity, profiles, timestamp ->
                            viewModel.update(
                                quantity = quantity,
                                profiles = profiles,
                                timestamp = timestamp,
                                isTracked = false,
                            )
                        },
                        entry = entry,
                        profiles = profiles,
                        modifier = modifier,
                    )

                is FoodSnapshotId.FoodDataCentral ->
                    UpdateFoodDataCentralDiaryEntryScreen(
                        onBack = onBack,
                        onSave = viewModel::update,
                        foodId = id.toFoodDataCentralProductId(),
                        entry = entry,
                        profiles = profiles,
                        modifier = modifier,
                    )

                is FoodSnapshotId.OpenFoodFacts ->
                    UpdateOpenFoodFactsDiaryEntryScreen(
                        onBack = onBack,
                        onSave = viewModel::update,
                        foodId = id.toOpenFoodFactsProductId(),
                        entry = entry,
                        profiles = profiles,
                        modifier = modifier,
                    )

                is FoodSnapshotId.UserProduct ->
                    UpdateUserProductDiaryEntryScreen(
                        onBack = onBack,
                        onSave = viewModel::update,
                        onEdit = { onEditUserProduct(id.toUserProductId()) },
                        onDelete = onDeleteUserProduct,
                        foodId = id.toUserProductId(),
                        entry = entry,
                        profiles = profiles,
                        modifier = modifier,
                    )
            }
    }
}

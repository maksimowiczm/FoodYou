package com.maksimowiczm.foodyou.features.fooddetails

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.maksimowiczm.foodyou.capabilities.fooddetails.FoodDetailsNutrientsWithSuggestions
import com.maksimowiczm.foodyou.capabilities.fooddetails.FoodHeadline
import com.maksimowiczm.foodyou.capabilities.fooddetails.FoodImage
import com.maksimowiczm.foodyou.capabilities.fooddetails.FoodNote
import com.maksimowiczm.foodyou.capabilities.fooddetails.FoodScreenTopBar
import com.maksimowiczm.foodyou.capabilities.fooddetails.UserFoodMenu
import com.maksimowiczm.foodyou.capabilities.fooddetails.rememberNutrientExpanded
import com.maksimowiczm.foodyou.capabilities.fooddetails.userproduct.UserProductDetailsUiEvent
import com.maksimowiczm.foodyou.capabilities.fooddetails.userproduct.UserProductDetailsViewModel
import com.maksimowiczm.foodyou.common.domain.FileUri
import com.maksimowiczm.foodyou.common.domain.food.AbsoluteQuantity
import com.maksimowiczm.foodyou.common.domain.food.Nutrient
import com.maksimowiczm.foodyou.common.domain.food.NutritionFacts
import com.maksimowiczm.foodyou.common.domain.food.Quantity
import com.maksimowiczm.foodyou.shared.ui.component.FavoriteIconButton
import com.maksimowiczm.foodyou.shared.ui.extension.LaunchedCollectWithLifecycle
import com.maksimowiczm.foodyou.shared.ui.extension.add
import com.maksimowiczm.foodyou.shared.ui.utility.LocalFoodNameSelector
import com.maksimowiczm.foodyou.shared.ui.utility.headline
import com.maksimowiczm.foodyou.shared.ui.utility.resolveBlob
import com.maksimowiczm.foodyou.userproduct.domain.UserProductIdentity
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun UserProductDetailsScreen(
    identity: UserProductIdentity,
    initialQuantity: Quantity?,
    onBack: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val viewModel: UserProductDetailsViewModel =
        koinViewModel(parameters = { parametersOf(identity, initialQuantity) })

    LaunchedCollectWithLifecycle(viewModel.uiEvents) {
        when (it) {
            UserProductDetailsUiEvent.Deleted -> onDelete()
        }
    }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val nameSelector = LocalFoodNameSelector.current
    val image = uiState.product?.image?.let { resolveBlob(it) }

    val scope =
        remember(uiState, nameSelector, image) {
            val product = uiState.product ?: return@remember null
            UserProductScope(
                headline = product.headline(nameSelector),
                isFavorite = uiState.isFavorite,
                image = image,
                note = product.note,
                suggestions = uiState.suggestions,
                selectedQuantity = uiState.selectedQuantity,
                scaledNutritionFacts = uiState.scaledNutritionFacts,
                packageQuantity = product.packageQuantity,
                servingQuantity = product.servingQuantity,
            )
        }

    scope?.let {
        UserProductDetailsScreenContent(
            scope = it,
            onBack = onBack,
            onEdit = onEdit,
            onDelete = viewModel::delete,
            onSetFavorite = viewModel::setFavorite,
            onSelectQuantity = viewModel::selectQuantity,
            modifier = modifier,
        )
    }
}

@Immutable
private data class UserProductScope(
    val headline: String?,
    val isFavorite: Boolean,
    val image: FileUri?,
    val note: String?,
    val suggestions: List<Quantity>,
    val selectedQuantity: Quantity?,
    val scaledNutritionFacts: NutritionFacts?,
    val packageQuantity: AbsoluteQuantity?,
    val servingQuantity: AbsoluteQuantity?,
)

@Composable
private fun UserProductDetailsScreenContent(
    scope: UserProductScope,
    onBack: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onSetFavorite: (Boolean) -> Unit,
    onSelectQuantity: (Quantity) -> Unit,
    modifier: Modifier = Modifier,
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    var expanded by rememberNutrientExpanded()
    val expandingEnabled =
        remember(scope.scaledNutritionFacts) {
            val scaledNutritionFacts = scope.scaledNutritionFacts ?: return@remember false
            (Nutrient.all - Nutrient.basic).any { scaledNutritionFacts[it].value != null }
        }

    Scaffold(
        modifier = modifier,
        topBar = {
            FoodScreenTopBar(
                onBack = onBack,
                title = scope.headline,
                actions = {
                    FavoriteIconButton(
                        isFavorite = scope.isFavorite,
                        onChange = onSetFavorite,
                    )
                    UserFoodMenu(onEdit = onEdit, onDelete = onDelete)
                },
                scrollBehavior = scrollBehavior,
            )
        },
    ) { contentPadding ->
        LazyColumn(
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            contentPadding = contentPadding.add(bottom = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item {
                FoodHeadline(scope.headline, Modifier.padding(horizontal = 8.dp))
            }
            if (scope.image != null)
                item {
                    FoodImage(scope.image, Modifier.fillMaxWidth().padding(horizontal = 8.dp))
                }
            if (scope.scaledNutritionFacts != null)
                item {
                    FoodDetailsNutrientsWithSuggestions(
                        scaledNutritionFacts = scope.scaledNutritionFacts,
                        suggestions = scope.suggestions,
                        selectedQuantity = scope.selectedQuantity,
                        packageQuantity = scope.packageQuantity,
                        servingQuantity = scope.servingQuantity,
                        onSelectQuantity = onSelectQuantity,
                        expanded = if (!expandingEnabled) false else expanded,
                        onExpandedChange = { expanded = it },
                        expandingEnabled = expandingEnabled,
                        contentPadding = PaddingValues(horizontal = 8.dp),
                    )
                }
            if (scope.note != null)
                item {
                    FoodNote(scope.note, Modifier.padding(horizontal = 8.dp))
                }
        }
    }
}

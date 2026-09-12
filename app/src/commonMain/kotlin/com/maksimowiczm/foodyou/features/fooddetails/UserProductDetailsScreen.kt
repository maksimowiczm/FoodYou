package com.maksimowiczm.foodyou.features.fooddetails

import androidx.compose.animation.Crossfade
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.updateTransition
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
import com.maksimowiczm.foodyou.shared.ui.component.LoadingScreen
import com.maksimowiczm.foodyou.shared.ui.extension.LaunchedCollectWithLifecycle
import com.maksimowiczm.foodyou.shared.ui.extension.add
import com.maksimowiczm.foodyou.shared.ui.utility.LocalFoodNameSelector
import com.maksimowiczm.foodyou.shared.ui.utility.headline
import com.maksimowiczm.foodyou.shared.ui.utility.resolveBlob
import com.maksimowiczm.foodyou.userproduct.domain.UserProductId
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun UserProductDetailsScreen(
    id: UserProductId,
    initialQuantity: Quantity?,
    onBack: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val viewModel: UserProductDetailsViewModel =
        koinViewModel(parameters = { parametersOf(id, initialQuantity) })

    LaunchedCollectWithLifecycle(viewModel.uiEvents) {
        when (it) {
            UserProductDetailsUiEvent.Deleted -> onDelete()
        }
    }

    val uiState = viewModel.uiState.collectAsStateWithLifecycle().value

    updateTransition(uiState).Crossfade(contentKey = { it != null }) { uiState ->
        if (uiState == null) LoadingScreen(onBack)
        else
            UserProductDetailsScreenContent(
                headline = uiState.product.headline(LocalFoodNameSelector.current),
                isFavorite = uiState.isFavorite,
                image = uiState.product.image?.let { resolveBlob(it) },
                note = uiState.product.note,
                suggestions = uiState.suggestions,
                selectedQuantity = uiState.selectedQuantity,
                scaledNutritionFacts = uiState.scaledNutritionFacts,
                packageQuantity = uiState.product.packageQuantity,
                servingQuantity = uiState.product.servingQuantity,
                onBack = onBack,
                onEdit = onEdit,
                onDelete = viewModel::delete,
                onSetFavorite = viewModel::setFavorite,
                onSelectQuantity = viewModel::selectQuantity,
                modifier = modifier,
            )
    }
}

@Composable
private fun UserProductDetailsScreenContent(
    headline: String?,
    isFavorite: Boolean,
    image: FileUri?,
    note: String?,
    suggestions: List<Quantity>,
    selectedQuantity: Quantity?,
    scaledNutritionFacts: NutritionFacts?,
    packageQuantity: AbsoluteQuantity?,
    servingQuantity: AbsoluteQuantity?,
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
        remember(scaledNutritionFacts) {
            val scaledNutritionFacts = scaledNutritionFacts ?: return@remember false
            (Nutrient.all - Nutrient.basic).any { scaledNutritionFacts[it].value != null }
        }

    Scaffold(
        modifier = modifier,
        topBar = {
            FoodScreenTopBar(
                onBack = onBack,
                title = headline,
                actions = {
                    FavoriteIconButton(
                        isFavorite = isFavorite,
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
                FoodHeadline(headline, Modifier.padding(horizontal = 8.dp))
            }
            if (image != null)
                item {
                    FoodImage(image, Modifier.fillMaxWidth().padding(horizontal = 8.dp))
                }
            if (scaledNutritionFacts != null)
                item {
                    FoodDetailsNutrientsWithSuggestions(
                        scaledNutritionFacts = scaledNutritionFacts,
                        suggestions = suggestions,
                        selectedQuantity = selectedQuantity,
                        packageQuantity = packageQuantity,
                        servingQuantity = servingQuantity,
                        onSelectQuantity = onSelectQuantity,
                        expanded = if (!expandingEnabled) false else expanded,
                        onExpandedChange = { expanded = it },
                        expandingEnabled = expandingEnabled,
                        contentPadding = PaddingValues(horizontal = 8.dp),
                    )
                }
            if (note != null)
                item {
                    FoodNote(note, Modifier.padding(horizontal = 8.dp))
                }
        }
    }
}

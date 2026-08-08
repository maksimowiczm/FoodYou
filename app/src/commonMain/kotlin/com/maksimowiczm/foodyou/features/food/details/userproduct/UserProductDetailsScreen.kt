package com.maksimowiczm.foodyou.features.food.details.userproduct

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
import com.maksimowiczm.foodyou.capabilities.food.FoodDetailsNutrientsWithSuggestions
import com.maksimowiczm.foodyou.capabilities.food.FoodScreenTopBar
import com.maksimowiczm.foodyou.capabilities.food.FoodUiScope
import com.maksimowiczm.foodyou.capabilities.food.FoodUiScopeWithImage
import com.maksimowiczm.foodyou.capabilities.food.FoodUiScopeWithNote
import com.maksimowiczm.foodyou.capabilities.food.FoodUiScopeWithOptionalNutrients
import com.maksimowiczm.foodyou.capabilities.food.Headline
import com.maksimowiczm.foodyou.capabilities.food.Image
import com.maksimowiczm.foodyou.capabilities.food.Note
import com.maksimowiczm.foodyou.capabilities.food.UserFoodMenu
import com.maksimowiczm.foodyou.capabilities.food.rememberNutrientExpanded
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

    scope?.Screen(
        onBack = onBack,
        onEdit = onEdit,
        onDelete = viewModel::delete,
        onSetFavorite = viewModel::setFavorite,
        onSelectQuantity = viewModel::selectQuantity,
        modifier = modifier,
    )
}

@Immutable
private data class UserProductScope(
    override val headline: String?,
    override val isFavorite: Boolean,
    override val image: FileUri?,
    override val note: String?,
    override val suggestions: List<Quantity>,
    override val selectedQuantity: Quantity?,
    override val scaledNutritionFacts: NutritionFacts?,
    override val packageQuantity: AbsoluteQuantity?,
    override val servingQuantity: AbsoluteQuantity?,
) : FoodUiScope, FoodUiScopeWithImage, FoodUiScopeWithOptionalNutrients, FoodUiScopeWithNote

@Composable
private fun UserProductScope.Screen(
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
                        favorite = isFavorite,
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
                Headline(Modifier.padding(horizontal = 8.dp))
            }
            if (image != null)
                item {
                    Image(Modifier.fillMaxWidth().padding(horizontal = 8.dp))
                }
            if (scaledNutritionFacts != null)
                item {
                    FoodDetailsNutrientsWithSuggestions(
                        onSelectQuantity = onSelectQuantity,
                        expanded = if (!expandingEnabled) false else expanded,
                        onExpandedChange = { expanded = it },
                        expandingEnabled = expandingEnabled,
                        contentPadding = PaddingValues(horizontal = 8.dp),
                    )
                }
            if (note != null)
                item {
                    Note(Modifier.padding(horizontal = 8.dp))
                }
        }
    }
}

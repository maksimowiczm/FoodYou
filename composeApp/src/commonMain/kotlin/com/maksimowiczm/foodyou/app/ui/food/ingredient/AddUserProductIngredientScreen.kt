package com.maksimowiczm.foodyou.app.ui.food.ingredient

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeExtendedFloatingActionButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.animateFloatingActionButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.maksimowiczm.foodyou.app.ui.common.component.QuantityInput
import com.maksimowiczm.foodyou.app.ui.common.extension.LaunchedCollectWithLifecycle
import com.maksimowiczm.foodyou.app.ui.common.extension.add
import com.maksimowiczm.foodyou.app.ui.common.utility.LocalFoodNameSelector
import com.maksimowiczm.foodyou.app.ui.common.utility.headline
import com.maksimowiczm.foodyou.app.ui.common.utility.resolveBlob
import com.maksimowiczm.foodyou.app.ui.food.details.FavoriteIconButton
import com.maksimowiczm.foodyou.app.ui.food.details.FoodDetailsHeadline
import com.maksimowiczm.foodyou.app.ui.food.details.FoodDetailsImage
import com.maksimowiczm.foodyou.app.ui.food.details.FoodDetailsTopBar
import com.maksimowiczm.foodyou.app.ui.food.details.rememberNutrientExpanded
import com.maksimowiczm.foodyou.app.ui.food.details.userproduct.UserFoodMenu
import com.maksimowiczm.foodyou.app.ui.food.details.userproduct.UserFoodNote
import com.maksimowiczm.foodyou.app.ui.food.details.userproduct.UserProductDetailsUiEvent
import com.maksimowiczm.foodyou.app.ui.food.details.userproduct.UserProductDetailsViewModel
import com.maksimowiczm.foodyou.common.domain.food.Nutrient
import com.maksimowiczm.foodyou.common.domain.food.Quantity
import com.maksimowiczm.foodyou.common.domain.food.scale
import com.maksimowiczm.foodyou.common.getOrNull
import com.maksimowiczm.foodyou.userproduct.domain.UserProduct
import com.maksimowiczm.foodyou.userproduct.domain.UserProductIdentity
import foodyou.app.generated.resources.*
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun AddUserProductIngredientScreen(
    onBack: () -> Unit,
    onAdd: (Quantity) -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    identity: UserProductIdentity,
    initialQuantity: Quantity,
    modifier: Modifier = Modifier,
) {
    val viewModel: UserProductDetailsViewModel =
        koinViewModel(parameters = { parametersOf(identity) })

    LaunchedCollectWithLifecycle(viewModel.uiEvents) {
        when (it) {
            UserProductDetailsUiEvent.Deleted -> onDelete()
        }
    }

    val product by viewModel.userFood.collectAsStateWithLifecycle()
    val isFavorite by viewModel.isFavorite.collectAsStateWithLifecycle()

    val quantityState =
        rememberAddIngredientQuantityState(
            initialQuantity = initialQuantity,
            servingQuantity = product?.servingQuantity,
            packageQuantity = product?.packageQuantity,
            isLiquid = product?.isLiquid ?: false,
        )

    AddUserProductIngredientScreen(
        product = product,
        isFavorite = isFavorite,
        onSetFavorite = viewModel::setFavorite,
        onBack = onBack,
        onAdd = { onAdd(quantityState.quantity) },
        onEdit = onEdit,
        onDelete = viewModel::delete,
        modifier = modifier,
        quantityState = quantityState,
    )
}

@Composable
private fun AddUserProductIngredientScreen(
    product: UserProduct?,
    isFavorite: Boolean?,
    onSetFavorite: (Boolean) -> Unit,
    onBack: () -> Unit,
    onAdd: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    quantityState: AddIngredientQuantityState,
    modifier: Modifier = Modifier,
) {
    val nameSelector = LocalFoodNameSelector.current

    val expanded = rememberNutrientExpanded()
    val expandingEnabled =
        remember(product?.nutritionFacts) {
            val nutritionFacts = product?.nutritionFacts ?: return@remember false
            (Nutrient.all - Nutrient.basic).any { nutritionFacts[it].value != null }
        }

    val headline =
        remember(product?.name, product?.brand, nameSelector) { product?.headline(nameSelector) }

    val scaledNutritionFacts =
        remember(
            product?.nutritionFacts,
            product?.packageQuantity,
            product?.servingQuantity,
            quantityState.quantity,
        ) {
            product
                ?.nutritionFacts
                ?.scale(product.packageQuantity, product.servingQuantity, quantityState.quantity)
                ?.getOrNull()
        }

    val lazyListState = rememberLazyListState()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(lazyListState)

    Scaffold(
        modifier = modifier,
        topBar = {
            FoodDetailsTopBar(
                onBack = onBack,
                title = headline,
                scrollBehavior = scrollBehavior,
                actions = {
                    FavoriteIconButton(favorite = isFavorite ?: false, onChange = onSetFavorite)
                    UserFoodMenu(onEdit = onEdit, onDelete = onDelete)
                },
            )
        },
        floatingActionButton = {
            LargeExtendedFloatingActionButton(
                onClick = onAdd,
                modifier =
                    Modifier.animateFloatingActionButton(
                        visible = quantityState.formField.error == null,
                        alignment = Alignment.BottomEnd,
                    ),
            ) {
                Icon(
                    imageVector = Icons.Outlined.Add,
                    contentDescription = null,
                    modifier = Modifier.size(FloatingActionButtonDefaults.LargeIconSize),
                )
                Spacer(Modifier.width(16.dp))
                Text(stringResource(Res.string.action_add))
            }
        },
    ) { contentPadding ->
        LazyColumn(
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            state = lazyListState,
            contentPadding = contentPadding.add(bottom = 128.dp),
        ) {
            item { FoodDetailsHeadline(headline = headline) }
            item { Spacer(Modifier.height(16.dp)) }
            item {
                FoodDetailsImage(
                    image = product?.image?.let { resolveBlob(it) },
                    showPlaceholder = product == null,
                )
            }
            item { Spacer(Modifier.height(16.dp)) }
            item {
                QuantityInput(
                    entries = quantityState.quantityTypes,
                    selectedQuantity = quantityState.selectedQuantityType,
                    onQuantity = quantityState::onSelectedQuantityTypeChange,
                    formField = quantityState.formField,
                    modifier = Modifier.padding(horizontal = 16.dp),
                )
            }
            item { Spacer(Modifier.height(8.dp)) }
            if (scaledNutritionFacts != null) {
                item {
                    AddIngredientNutrients(
                        nutritionFacts = scaledNutritionFacts,
                        quantities = quantityState.quantitySuggestions,
                        servingQuantity = product?.servingQuantity,
                        packageQuantity = product?.packageQuantity,
                        onSelectQuantity = quantityState::onSelectQuantity,
                        expanded = expanded.value,
                        onExpandedChange = { expanded.value = it },
                        expandingEnabled = expandingEnabled,
                    )
                }
            }
            if (product?.note != null) {
                item {
                    Spacer(Modifier.height(8.dp))
                    UserFoodNote(note = product.note)
                }
            }
        }
    }
}

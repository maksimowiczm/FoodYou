package com.maksimowiczm.foodyou.app.ui.food.details.userproduct

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSerializable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.maksimowiczm.foodyou.app.ui.common.extension.LaunchedCollectWithLifecycle
import com.maksimowiczm.foodyou.app.ui.common.extension.add
import com.maksimowiczm.foodyou.app.ui.common.utility.LocalFoodNameSelector
import com.maksimowiczm.foodyou.app.ui.common.utility.headline
import com.maksimowiczm.foodyou.app.ui.common.utility.resolveBlob
import com.maksimowiczm.foodyou.app.ui.food.details.FavoriteIconButton
import com.maksimowiczm.foodyou.app.ui.food.details.FoodDetailsHeadline
import com.maksimowiczm.foodyou.app.ui.food.details.FoodDetailsImage
import com.maksimowiczm.foodyou.app.ui.food.details.FoodDetailsNutrients
import com.maksimowiczm.foodyou.app.ui.food.details.FoodDetailsTopBar
import com.maksimowiczm.foodyou.app.ui.food.details.UserFoodMenu
import com.maksimowiczm.foodyou.app.ui.food.details.UserFoodNote
import com.maksimowiczm.foodyou.app.ui.food.details.rememberNutrientExpanded
import com.maksimowiczm.foodyou.common.domain.food.AbsoluteQuantity
import com.maksimowiczm.foodyou.common.domain.food.Nutrient
import com.maksimowiczm.foodyou.common.domain.food.PackageQuantity
import com.maksimowiczm.foodyou.common.domain.food.Quantity
import com.maksimowiczm.foodyou.common.domain.food.ServingQuantity
import com.maksimowiczm.foodyou.common.domain.food.scale
import com.maksimowiczm.foodyou.common.domain.grams
import com.maksimowiczm.foodyou.common.domain.milliliters
import com.maksimowiczm.foodyou.common.getOrNull
import com.maksimowiczm.foodyou.userproduct.domain.UserProduct
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
        koinViewModel(parameters = { parametersOf(identity) })

    LaunchedCollectWithLifecycle(viewModel.uiEvents) {
        when (it) {
            UserProductDetailsUiEvent.Deleted -> onDelete()
        }
    }

    val isFavorite by viewModel.isFavorite.collectAsStateWithLifecycle()
    val userFood by viewModel.userFood.collectAsStateWithLifecycle()

    UserProductDetailsScreen(
        isFavorite = isFavorite,
        product = userFood,
        initialQuantity = initialQuantity,
        onBack = onBack,
        onEdit = onEdit,
        onDelete = viewModel::delete,
        onSetFavorite = viewModel::setFavorite,
        modifier = modifier,
    )
}

@Composable
private fun UserProductDetailsScreen(
    isFavorite: Boolean?,
    product: UserProduct?,
    initialQuantity: Quantity?,
    onBack: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onSetFavorite: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val nameSelector = LocalFoodNameSelector.current

    var expanded by rememberNutrientExpanded()
    val expandingEnabled =
        remember(product?.nutritionFacts) {
            if (product?.nutritionFacts == null) return@remember false
            (Nutrient.all - Nutrient.basic).any { product.nutritionFacts[it].value != null }
        }

    var quantity by
        rememberSerializable(
            initialQuantity,
            product?.isLiquid,
            product?.servingQuantity,
            product?.packageQuantity,
        ) {
            val default =
                initialQuantity
                    ?: if (product?.servingQuantity != null) ServingQuantity(1.0)
                    else if (product?.packageQuantity != null) PackageQuantity(1.0)
                    else if (product?.isLiquid == true) AbsoluteQuantity.Volume(100.milliliters)
                    else AbsoluteQuantity.Weight(100.grams)

            mutableStateOf(default)
        }
    val quantitySuggestions =
        remember(product?.isLiquid, product?.servingQuantity, product?.packageQuantity) {
            if (product == null) return@remember emptyList()
            else
                buildList {
                    if (product.isLiquid) add(AbsoluteQuantity.Volume(100.milliliters))
                    else add(AbsoluteQuantity.Weight(100.grams))
                    if (initialQuantity != null) add(initialQuantity)
                    if (product.servingQuantity != null) add(ServingQuantity(1.0))
                    if (product.packageQuantity != null) add(PackageQuantity(1.0))
                }
                    .distinct()
        }

    val headline =
        remember(product?.name, product?.brand, nameSelector) { product?.headline(nameSelector) }

    val scaledNutritionFacts =
        remember(
            product?.nutritionFacts,
            product?.packageQuantity,
            product?.servingQuantity,
            quantity,
        ) {
            product
                ?.nutritionFacts
                ?.scale(product.packageQuantity, product.servingQuantity, quantity)
                ?.getOrNull()
        }

    val lazyListState = rememberLazyListState()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    Scaffold(
        modifier = modifier,
        topBar = {
            FoodDetailsTopBar(
                onBack = onBack,
                title = headline,
                actions = {
                    FavoriteIconButton(
                        favorite = isFavorite ?: false,
                        onChange = onSetFavorite,
                        enabled = product != null,
                    )
                    UserFoodMenu(onEdit = onEdit, onDelete = onDelete, enabled = product != null)
                },
                scrollBehavior = scrollBehavior,
            )
        },
    ) { contentPadding ->
        LazyColumn(
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            state = lazyListState,
            contentPadding = contentPadding.add(bottom = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            item {
                FoodDetailsHeadline(
                    headline = headline,
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                )
            }
            if (product?.image != null) {
                item {
                    FoodDetailsImage(
                        image = resolveBlob(product.image),
                        showPlaceholder = false,
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                    )
                }
            }
            if (scaledNutritionFacts != null) {
                item {
                    FoodDetailsNutrients(
                        nutritionFacts = scaledNutritionFacts,
                        quantities = quantitySuggestions,
                        selectedQuantity = quantity,
                        servingQuantity = product?.servingQuantity,
                        packageQuantity = product?.packageQuantity,
                        onSelectQuantity = { quantity = it },
                        expanded = expanded,
                        onExpandedChange = { expanded = it },
                        expandingEnabled = expandingEnabled,
                    )
                }
            }
            if (product?.note != null) {
                item {
                    UserFoodNote(
                        note = product.note,
                        modifier = Modifier.padding(horizontal = 8.dp),
                    )
                }
            }
        }
    }
}

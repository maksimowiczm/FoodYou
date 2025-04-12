package com.maksimowiczm.foodyou.feature.recipe.ui

import androidx.compose.animation.Crossfade
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.clearText
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSearchBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.maksimowiczm.foodyou.core.model.Measurement
import com.maksimowiczm.foodyou.core.ui.component.FoodListItemSkeleton
import com.maksimowiczm.foodyou.core.ui.component.MeasurementSummary
import com.maksimowiczm.foodyou.core.ui.component.NutrientsRow
import com.maksimowiczm.foodyou.core.ui.ext.throwable
import com.maksimowiczm.foodyou.core.ui.res.formatClipZeros
import com.maksimowiczm.foodyou.feature.addfood.ui.component.SearchScreen
import com.maksimowiczm.foodyou.feature.openfoodfacts.OpenFoodFactsErrorCard
import com.maksimowiczm.foodyou.feature.recipe.model.Ingredient
import com.valentinilk.shimmer.ShimmerBounds
import com.valentinilk.shimmer.rememberShimmer
import foodyou.app.generated.resources.*
import kotlin.math.roundToInt
import kotlinx.coroutines.flow.collectLatest
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun AddIngredientScreen(
    viewModel: RecipeViewModel,
    listState: LazyListState,
    onBarcodeScanner: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val pages = viewModel.pages.collectAsLazyPagingItems(
        viewModel.viewModelScope.coroutineContext
    )

    val textFieldState = rememberTextFieldState()

    LaunchedEffect(viewModel) {
        viewModel.searchQuery.collectLatest {
            when (it) {
                null -> textFieldState.clearText()
                else -> textFieldState.setTextAndPlaceCursorAtEnd(it)
            }
        }
    }

    AddIngredientScreen(
        pages = pages,
        onBarcodeScanner = onBarcodeScanner,
        modifier = modifier,
        listState = listState,
        textFieldState = textFieldState,
        onSearch = remember(viewModel) { viewModel::onSearch },
        onClear = remember(viewModel) { { viewModel.onSearch(null) } },
        onBack = onBack
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
private fun AddIngredientScreen(
    pages: LazyPagingItems<Ingredient>,
    onBarcodeScanner: () -> Unit,
    listState: LazyListState,
    textFieldState: TextFieldState,
    onSearch: (String) -> Unit,
    onClear: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val searchBarState = rememberSearchBarState()
    val shimmer = rememberShimmer(ShimmerBounds.View)

    SearchScreen(
        pages = pages,
        onSearch = onSearch,
        onClear = onClear,
        onBack = onBack,
        onBarcodeScanner = onBarcodeScanner,
        textFieldState = textFieldState,
        searchBarState = searchBarState,
        topBar = null,
        floatingActionButton = {},
        fullScreenSearchBarContent = {},
        errorCard = {
            val error by remember(pages.loadState) {
                derivedStateOf { pages.throwable }
            }

            error?.let {
                OpenFoodFactsErrorCard(
                    throwable = it,
                    onRetry = remember(pages) { pages::refresh },
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
            }
        },
        hintCard = {},
        modifier = modifier
    ) { paddingValues ->
        LazyColumn(
            contentPadding = paddingValues,
            state = listState
        ) {
            items(
                count = pages.itemCount,
                key = pages.itemKey { it.product.id.id }
            ) {
                val item = pages[it]
                val transition = updateTransition(item)

                transition.Crossfade(
                    contentKey = { it != null },
                    modifier = Modifier.animateItem(
                        fadeInSpec = null,
                        fadeOutSpec = null
                    )
                ) { item ->
                    when (item) {
                        null -> FoodListItemSkeleton(shimmer)
                        else -> item.ListItem(
                            modifier = Modifier.clickable {
                                // TODO
                            }
                        )
                    }
                }
            }

            if (pages.loadState.append is androidx.paging.LoadState.Loading) {
                items(3) {
                    FoodListItemSkeleton(shimmer)
                }
            }
        }
    }
}

@Composable
private fun Ingredient.ListItem(modifier: Modifier = Modifier) {
    val weight = weight
    val measurementString = measurementString
    val caloriesString = caloriesString
    if (weight == null || measurementString == null || caloriesString == null) {
        // TODO handle broken weight
        return
    }

    val proteins = product.nutrients.proteins.value
    val carbohydrates = product.nutrients.carbohydrates.value
    val fats = product.nutrients.fats.value

    ListItem(
        headlineContent = { Text(product.name) },
        modifier = modifier,
        overlineContent = product.brand?.let { { Text(it) } },
        supportingContent = {
            Column {
                val proteins = (proteins * weight / 100f).roundToInt()
                val carbohydrates = (carbohydrates * weight / 100f).roundToInt()
                val fats = (fats * weight / 100f).roundToInt()

                NutrientsRow(
                    proteins = proteins,
                    carbohydrates = carbohydrates,
                    fats = fats,
                    modifier = Modifier.fillMaxWidth()
                )
                MeasurementSummary(
                    measurementString = measurementString,
                    measurementStringShort = measurementStringShort,
                    caloriesString = caloriesString,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    )
}

private val Ingredient.measurementStringShort: String
    @Composable get() = with(measurement) {
        when (this) {
            is Measurement.Package -> stringResource(
                Res.string.x_times_y,
                quantity.formatClipZeros(),
                stringResource(Res.string.product_package)
            )

            is Measurement.Serving -> stringResource(
                Res.string.x_times_y,
                quantity.formatClipZeros(),
                stringResource(Res.string.product_serving)
            )

            is Measurement.Gram -> "${value.formatClipZeros()} " +
                stringResource(Res.string.unit_gram_short)
        }
    }

private val Ingredient.measurementString: String?
    @Composable get() {
        val short = measurementStringShort
        val weight = weight?.formatClipZeros() ?: return null

        return when (measurement) {
            is Measurement.Gram -> short
            is Measurement.Package,
            is Measurement.Serving ->
                "$short ($weight ${
                    stringResource(
                        Res.string.unit_gram_short
                    )
                })"
        }
    }

private val Ingredient.caloriesString: String?
    @Composable get() = weight?.let {
        val value = (it * product.nutrients.calories.value / 100).roundToInt()
        "$value " + stringResource(Res.string.unit_kcal)
    }

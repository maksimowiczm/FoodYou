package com.maksimowiczm.foodyou.app.ui.food.details.openfoodfacts

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LargeFlexibleTopAppBar
import androidx.compose.material3.LinearWavyProgressIndicator
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.maksimowiczm.foodyou.app.ui.common.component.ArrowBackIconButton
import com.maksimowiczm.foodyou.app.ui.common.component.Image
import com.maksimowiczm.foodyou.app.ui.common.extension.add
import com.maksimowiczm.foodyou.app.ui.common.extension.toDp
import com.maksimowiczm.foodyou.app.ui.food.LocalFoodNameSelector
import com.maksimowiczm.foodyou.app.ui.food.details.FavoriteIcon
import com.maksimowiczm.foodyou.app.ui.food.details.FoodDetailsUiState
import com.maksimowiczm.foodyou.app.ui.food.details.NutrientList
import com.maksimowiczm.foodyou.app.ui.food.details.NutrientsHeader
import com.maksimowiczm.foodyou.app.ui.food.details.RefreshMenu
import com.maksimowiczm.foodyou.app.ui.food.details.rememberNutrientExpanded
import com.maksimowiczm.foodyou.common.domain.Image
import com.maksimowiczm.foodyou.common.domain.food.NutritionFacts
import com.maksimowiczm.foodyou.openfoodfacts.domain.OpenFoodFactsProduct
import com.maksimowiczm.foodyou.openfoodfacts.domain.OpenFoodFactsProductIdentity
import com.valentinilk.shimmer.ShimmerBounds
import com.valentinilk.shimmer.rememberShimmer
import com.valentinilk.shimmer.shimmer
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun OpenFoodFactsDetailsScreen(
    identity: OpenFoodFactsProductIdentity,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val viewModel: OpenFoodFactsDetailsViewModel =
        koinViewModel(parameters = { parametersOf(identity) })

    val uiState = viewModel.uiState.collectAsStateWithLifecycle().value

    val nameSelector = LocalFoodNameSelector.current
    val headline =
        remember(uiState, nameSelector) {
            when (uiState) {
                is FoodDetailsUiState.Error -> null
                FoodDetailsUiState.NotFound -> null
                is FoodDetailsUiState.Details<OpenFoodFactsProduct> -> {
                    val name = uiState.food?.name
                    val brand = uiState.food?.brand
                    if (name == null) null
                    else
                        buildString {
                            append(nameSelector.select(name))
                            append(brand?.let { " ($it)" } ?: "")
                        }
                }
            }
        }

    when (uiState) {
        is FoodDetailsUiState.Details<OpenFoodFactsProduct> ->
            OpenFoodFactsDetailsScreen(
                isLoading = uiState.isLoading,
                isFavorite = uiState.isFavorite,
                headline = headline,
                image = uiState.food?.image,
                nutritionFacts = uiState.food?.nutritionFacts,
                url = uiState.food?.source,
                onBack = onBack,
                onRefresh = viewModel::refresh,
                onSetFavorite = viewModel::setFavorite,
                modifier = modifier,
            )

        is FoodDetailsUiState.Error,
        FoodDetailsUiState.NotFound -> error("Not possible for now")
    }
}

@Composable
private fun OpenFoodFactsDetailsScreen(
    isLoading: Boolean,
    isFavorite: Boolean,
    headline: String?,
    image: Image?,
    nutritionFacts: NutritionFacts?,
    url: String?,
    onBack: () -> Unit,
    onRefresh: () -> Unit,
    onSetFavorite: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    var expanded by rememberNutrientExpanded()

    Scaffold(
        modifier = modifier,
        topBar = {
            LargeFlexibleTopAppBar(
                title = {
                    if (headline != null) {
                        Text(headline)
                    } else {
                        Spacer(
                            Modifier.shimmer()
                                .fillMaxWidth(.75f)
                                .height(LocalTextStyle.current.toDp())
                                .clip(MaterialTheme.shapes.medium)
                                .background(MaterialTheme.colorScheme.secondaryContainer)
                        )
                    }
                },
                navigationIcon = { ArrowBackIconButton(onBack) },
                actions = {
                    FavoriteIcon(favorite = isFavorite, onChange = onSetFavorite)
                    RefreshMenu(onRefresh)
                },
                colors =
                    TopAppBarDefaults.topAppBarColors(
                        scrolledContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                    ),
                scrollBehavior = scrollBehavior,
            )
        },
    ) { contentPadding ->
        Box(Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)) {
            Column(Modifier.padding(top = contentPadding.calculateTopPadding()).zIndex(100f)) {
                if (isLoading) {
                    Spacer(Modifier.height(8.dp))
                    LinearWavyProgressIndicator(Modifier.fillMaxWidth())
                    Spacer(Modifier.height(8.dp))
                } else {
                    Spacer(Modifier.height(26.dp))
                }
            }

            LazyColumn(contentPadding = contentPadding.add(top = 26.dp, bottom = 8.dp)) {
                item {
                    when (image) {
                        is Image ->
                            image.Image(
                                shimmer = rememberShimmer(ShimmerBounds.View),
                                modifier =
                                    Modifier.fillMaxWidth()
                                        .aspectRatio(16f / 9f)
                                        .padding(horizontal = 32.dp),
                            )

                        null if (isLoading) ->
                            Spacer(
                                Modifier.shimmer()
                                    .fillMaxWidth()
                                    .aspectRatio(16f / 9f)
                                    .padding(horizontal = 32.dp)
                                    .clip(MaterialTheme.shapes.medium)
                                    .background(MaterialTheme.colorScheme.secondaryContainer)
                            )

                        null -> Unit
                    }
                }
                item { Spacer(Modifier.height(8.dp)) }
                if (nutritionFacts != null) {
                    item {
                        NutrientsHeader(
                            proteins = nutritionFacts.proteins.value?.toFloat(),
                            carbohydrates = nutritionFacts.carbohydrates.value?.toFloat(),
                            fats = nutritionFacts.fats.value?.toFloat(),
                            expanded = expanded,
                            onExpandedChange = { expanded = it },
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                        )
                        Spacer(Modifier.height(8.dp))
                        NutrientList(
                            facts = nutritionFacts,
                            expanded = expanded,
                            modifier =
                                Modifier.fillMaxWidth()
                                    .padding(horizontal = 8.dp)
                                    .clickable(
                                        interactionSource = null,
                                        indication = null,
                                        onClick = { expanded = !expanded },
                                    ),
                        )
                    }
                }
                if (url != null) {
                    item {
                        Spacer(Modifier.height(16.dp))
                        HorizontalDivider(Modifier.fillMaxWidth().padding(horizontal = 16.dp))
                        Spacer(Modifier.height(16.dp))
                        OpenFoodFactsSource(
                            url = url,
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                        )
                    }
                }
            }
        }
    }
}

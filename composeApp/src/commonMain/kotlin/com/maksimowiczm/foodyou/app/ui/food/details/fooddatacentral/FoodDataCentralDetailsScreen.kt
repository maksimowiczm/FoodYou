package com.maksimowiczm.foodyou.app.ui.food.details.fooddatacentral

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LinearWavyProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.maksimowiczm.foodyou.app.ui.common.component.ArrowBackIconButton
import com.maksimowiczm.foodyou.app.ui.common.component.StatusBarProtection
import com.maksimowiczm.foodyou.app.ui.common.extension.add
import com.maksimowiczm.foodyou.app.ui.common.extension.toDp
import com.maksimowiczm.foodyou.app.ui.food.LocalFoodNameSelector
import com.maksimowiczm.foodyou.app.ui.food.details.FavoriteIconButton
import com.maksimowiczm.foodyou.app.ui.food.details.FoodDetailsUiState
import com.maksimowiczm.foodyou.app.ui.food.details.NutrientList
import com.maksimowiczm.foodyou.app.ui.food.details.NutrientsHeader
import com.maksimowiczm.foodyou.app.ui.food.details.RefreshIconButton
import com.maksimowiczm.foodyou.app.ui.food.details.rememberNutrientExpanded
import com.maksimowiczm.foodyou.common.domain.food.Nutrient
import com.maksimowiczm.foodyou.common.domain.food.NutritionFacts
import com.maksimowiczm.foodyou.fooddatacentral.domain.FoodDataCentralProduct
import com.maksimowiczm.foodyou.fooddatacentral.domain.FoodDataCentralProductIdentity
import com.valentinilk.shimmer.shimmer
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun FoodDataCentralDetailsScreen(
    identity: FoodDataCentralProductIdentity,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val viewModel: FoodDataCentralDetailsViewModel =
        koinViewModel(parameters = { parametersOf(identity) })

    val uiState = viewModel.uiState.collectAsStateWithLifecycle().value

    val nameSelector = LocalFoodNameSelector.current
    val headline =
        remember(uiState, nameSelector) {
            when (uiState) {
                is FoodDetailsUiState.Error -> null
                FoodDetailsUiState.NotFound -> null
                is FoodDetailsUiState.Details<FoodDataCentralProduct> -> {
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
        is FoodDetailsUiState.Details<FoodDataCentralProduct> ->
            FoodDataCentralDetailsScreen(
                isLoading = uiState.isLoading,
                isFavorite = uiState.isFavorite,
                headline = headline,
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
private fun FoodDataCentralDetailsScreen(
    isLoading: Boolean,
    isFavorite: Boolean,
    headline: String?,
    nutritionFacts: NutritionFacts?,
    url: String?,
    onBack: () -> Unit,
    onRefresh: () -> Unit,
    onSetFavorite: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanded by rememberNutrientExpanded()
    val expandingEnabled =
        remember(nutritionFacts) {
            if (nutritionFacts == null) return@remember false
            (Nutrient.all - Nutrient.basic).any { nutritionFacts[it].value != null }
        }

    val lazyListState = rememberLazyListState()
    val animatedIsScrolled =
        animateFloatAsState(
            targetValue = if (lazyListState.canScrollBackward) 1f else 0f,
            animationSpec = MaterialTheme.motionScheme.fastEffectsSpec(),
        )
    val animatedIconButtonColor =
        animateColorAsState(
            targetValue =
                if (lazyListState.canScrollBackward) MaterialTheme.colorScheme.surfaceContainerHigh
                else MaterialTheme.colorScheme.surface
        )

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    ArrowBackIconButton(
                        onClick = onBack,
                        colors =
                            IconButtonDefaults.iconButtonColors(
                                containerColor = animatedIconButtonColor.value
                            ),
                    )
                },
                actions = {
                    FavoriteIconButton(
                        favorite = isFavorite,
                        onChange = onSetFavorite,
                        colors =
                            IconButtonDefaults.iconButtonColors(
                                containerColor = animatedIconButtonColor.value
                            ),
                    )
                    RefreshIconButton(
                        onRefresh = onRefresh,
                        colors =
                            IconButtonDefaults.iconButtonColors(
                                containerColor = animatedIconButtonColor.value
                            ),
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
            )
        },
    ) { contentPadding ->
        Box {
            Column(Modifier.padding(top = contentPadding.calculateTopPadding()).zIndex(100f)) {
                if (isLoading) {
                    Spacer(Modifier.height(8.dp))
                    LinearWavyProgressIndicator(Modifier.fillMaxWidth())
                    Spacer(Modifier.height(8.dp))
                } else {
                    Spacer(Modifier.height(26.dp))
                }
            }
            LazyColumn(
                state = lazyListState,
                contentPadding = contentPadding.add(top = 26.dp, bottom = 8.dp),
            ) {
                item {
                    Box(Modifier.fillMaxWidth().padding(16.dp)) {
                        if (headline != null) {
                            Text(text = headline, style = MaterialTheme.typography.displaySmall)
                        } else {
                            Spacer(
                                Modifier.shimmer()
                                    .fillMaxWidth(.75f)
                                    .height(MaterialTheme.typography.displaySmall.toDp())
                                    .clip(MaterialTheme.shapes.medium)
                                    .background(MaterialTheme.colorScheme.secondaryContainer)
                            )
                        }
                    }
                }
                if (nutritionFacts != null) {
                    item {
                        NutrientsHeader(
                            proteins = nutritionFacts.proteins.value?.toFloat(),
                            carbohydrates = nutritionFacts.carbohydrates.value?.toFloat(),
                            fats = nutritionFacts.fats.value?.toFloat(),
                            expanded = expanded,
                            onExpandedChange = { expanded = it },
                            enabled = expandingEnabled,
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
                        FoodDataCentralSource(
                            url = url,
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                        )
                    }
                }
            }
        }
    }
    StatusBarProtection(MaterialTheme.colorScheme.surfaceContainerHigh) { animatedIsScrolled.value }
}

package com.maksimowiczm.foodyou.feature.addfood.ui.portion

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.displayCutoutPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.maksimowiczm.foodyou.R
import com.maksimowiczm.foodyou.feature.addfood.data.model.QuantitySuggestion
import com.maksimowiczm.foodyou.feature.addfood.data.model.WeightMeasurementEnum
import com.maksimowiczm.foodyou.feature.product.data.model.Product
import com.maksimowiczm.foodyou.feature.product.ui.previewparameter.ProductPreviewParameterProvider
import com.maksimowiczm.foodyou.ui.theme.FoodYouTheme
import kotlin.math.roundToInt

@Composable
fun PortionScreen(
    uiState: PortionUiState,
    onSuccess: () -> Unit,
    onSave: (WeightMeasurementEnum, quantity: Float) -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    LaunchedEffect(uiState) {
        if (uiState is PortionUiState.Success) {
            onSuccess()
        }
    }

    when (uiState) {
        PortionUiState.LoadingProduct,
        PortionUiState.ProductNotFound,
        PortionUiState.WaitingForProduct -> Unit

        is PortionUiState.StateWithProduct -> {
            PortionScreen(
                suggestion = uiState.suggestion,
                onNavigateBack = onNavigateBack,
                onSave = onSave,
                highlight = uiState.highlight,
                modifier = modifier
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PortionScreen(
    suggestion: QuantitySuggestion,
    onNavigateBack: () -> Unit,
    onSave: (WeightMeasurementEnum, quantity: Float) -> Unit,
    modifier: Modifier = Modifier,
    highlight: WeightMeasurementEnum? = null,
    highlightColor: Color = MaterialTheme.colorScheme.surfaceContainerHighest
) {
    val product = suggestion.product

    Scaffold(
        modifier = modifier.displayCutoutPadding(),
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.action_go_back)
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .consumeWindowInsets(innerPadding)
        ) {
            item {
                Text(
                    text = product.name,
                    style = MaterialTheme.typography.headlineLarge,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            vertical = 8.dp,
                            horizontal = 16.dp
                        )
                )
            }

            item {
                HorizontalDivider()
            }

            portionForm(
                suggestion = suggestion,
                product = product,
                onSave = onSave,
                highlight = highlight,
                highlightColor = highlightColor
            )

            item {
                HorizontalDivider()
            }
        }
    }
}

private fun LazyListScope.portionForm(
    suggestion: QuantitySuggestion,
    product: Product,
    onSave: (WeightMeasurementEnum, quantity: Float) -> Unit,
    highlight: WeightMeasurementEnum?,
    highlightColor: Color
) {
    if (product.packageWeight != null) {
        item {
            val quantity = suggestion.quantitySuggestions[WeightMeasurementEnum.Package] ?: 1f

            val packagePicker = rememberMutableDefinedPortion(
                initialQuantity = quantity,
                weightUnit = product.weightUnit,
                calculateCalories = {
                    product.calories((it * product.packageWeight)).roundToInt()
                },
                calculateWeight = { (it * product.packageWeight).roundToInt() },
                portionType = PortionType.Package
            )

            MutableDefinedPortionInput(
                portion = packagePicker,
                onConfirm = {
                    onSave(WeightMeasurementEnum.Package, it)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .then(
                        if (highlight == WeightMeasurementEnum.Package) {
                            Modifier.background(highlightColor)
                        } else {
                            Modifier
                        }
                    )
                    .padding(8.dp)
            )

            HorizontalDivider()
        }
    }

    if (product.servingWeight != null) {
        item {
            val quantity = suggestion.quantitySuggestions[WeightMeasurementEnum.Serving] ?: 1f

            val servingPicker = rememberMutableDefinedPortion(
                initialQuantity = quantity,
                weightUnit = product.weightUnit,
                calculateCalories = {
                    product.calories((it * product.servingWeight)).roundToInt()
                },
                calculateWeight = { (it * product.servingWeight).roundToInt() },
                portionType = PortionType.Serving
            )

            MutableDefinedPortionInput(
                portion = servingPicker,
                onConfirm = {
                    onSave(WeightMeasurementEnum.Serving, it)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .then(
                        if (highlight == WeightMeasurementEnum.Serving) {
                            Modifier.background(highlightColor)
                        } else {
                            Modifier
                        }
                    )
                    .padding(8.dp)
            )

            HorizontalDivider()
        }
    }

    item {
        val quantity = suggestion.quantitySuggestions[WeightMeasurementEnum.WeightUnit] ?: 100f

        val weightUnitPicker = rememberMutableWeightUnitPortion(
            initialWeight = quantity.roundToInt(),
            weightUnit = product.weightUnit,
            calculateCalories = { product.calories(it.toFloat()).roundToInt() }
        )

        MutableWeightUnitPortionInput(
            portion = weightUnitPicker,
            modifier = Modifier
                .fillMaxWidth()
                .then(
                    if (highlight == WeightMeasurementEnum.WeightUnit) {
                        Modifier.background(highlightColor)
                    } else {
                        Modifier
                    }
                )
                .padding(8.dp),
            onConfirm = {
                onSave(WeightMeasurementEnum.WeightUnit, it.toFloat())
            }
        )
    }

    item {
        HorizontalDivider()
    }
}

@PreviewLightDark
@Composable
private fun PortionScreenPreview() {
    val product = ProductPreviewParameterProvider().values.first {
        it.servingWeight != null && it.packageWeight != null
    }

    FoodYouTheme {
        PortionScreen(
            suggestion = QuantitySuggestion(
                product = product,
                quantitySuggestions = mapOf(
                    WeightMeasurementEnum.WeightUnit to 123f,
                    WeightMeasurementEnum.Package to 2f,
                    WeightMeasurementEnum.Serving to 3f
                )
            ),
            onNavigateBack = {},
            onSave = { _, _ -> },
            highlight = WeightMeasurementEnum.Serving
        )
    }
}

package com.maksimowiczm.foodyou.core.feature.addfood.ui.portion

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.add
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.maksimowiczm.foodyou.core.R
import com.maksimowiczm.foodyou.core.feature.addfood.data.model.QuantitySuggestion
import com.maksimowiczm.foodyou.core.feature.addfood.data.model.WeightMeasurementEnum
import com.maksimowiczm.foodyou.core.feature.product.ui.previewparameter.ProductPreviewParameterProvider
import com.maksimowiczm.foodyou.core.ui.form.FormFieldWithTextFieldValue
import com.maksimowiczm.foodyou.core.ui.theme.FoodYouTheme
import kotlinx.coroutines.flow.collectLatest
import kotlin.math.max
import kotlin.math.roundToInt

@Composable
fun PortionScreen(
    onBack: () -> Unit,
    onSuccess: () -> Unit,
    viewModel: PortionViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    PortionScreen(
        uiState = uiState,
        onBack = onBack,
        onSuccess = onSuccess,
        onConfirm = viewModel::onAddPortion,
        modifier = modifier
    )
}

@Composable
private fun PortionScreen(
    uiState: PortionUiState,
    onBack: () -> Unit,
    onSuccess: () -> Unit,
    onConfirm: (WeightMeasurementEnum, quantity: Float) -> Unit,
    modifier: Modifier = Modifier
) {
    val hapticFeedback = LocalHapticFeedback.current

    LaunchedEffect(uiState) {
        if (uiState is PortionUiState.Success) {
            onSuccess()
        }
    }

    when (uiState) {
        PortionUiState.Empty,
        PortionUiState.Error,
        PortionUiState.Loading -> Unit

        is PortionUiState.WithMeasurement -> PortionScreen(
            onBack = onBack,
            state = uiState,
            onConfirm = { _, _ -> },
            modifier = modifier,
            highlight = uiState.measurement
        )

        is PortionUiState.WithProduct -> PortionScreen(
            state = uiState,
            onBack = onBack,
            onConfirm = { weightMeasurementEnum, quantity ->
                hapticFeedback.performHapticFeedback(HapticFeedbackType.Confirm)

                onConfirm(weightMeasurementEnum, quantity)
            },
            modifier = modifier
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PortionScreen(
    state: PortionUiState.WithProduct,
    onBack: () -> Unit,
    onConfirm: (WeightMeasurementEnum, quantity: Float) -> Unit,
    modifier: Modifier = Modifier,
    highlight: WeightMeasurementEnum? = null
) {
    val formState = rememberPortionFormState(
        product = state.product,
        suggestion = state.suggestion
    )

    // Fade in top app bar title when scrolling down
    val lazyListState = rememberLazyListState()
    var headlineHeight by remember { mutableIntStateOf(0) }
    var headlineAlpha by remember { mutableFloatStateOf(0f) }
    LaunchedEffect(lazyListState) {
        snapshotFlow { lazyListState.firstVisibleItemScrollOffset }.collectLatest {
            headlineAlpha = if (lazyListState.firstVisibleItemIndex != 0) {
                1f
            } else {
                lerp(0f, 1f, it / headlineHeight.toFloat())
            }
        }
    }
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = state.product.name,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.graphicsLayer { alpha = headlineAlpha }
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.action_go_back)
                        )
                    }
                },
                scrollBehavior = scrollBehavior
            )
        },
        contentWindowInsets = ScaffoldDefaults.contentWindowInsets.add(WindowInsets.ime)
    ) { paddingValues ->
        LazyColumn(
            contentPadding = paddingValues,
            state = lazyListState,
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
        ) {
            item {
                Text(
                    text = state.product.name,
                    style = MaterialTheme.typography.headlineLarge,
                    modifier = Modifier
                        .padding(16.dp)
                        .onSizeChanged { headlineHeight = it.height }
                        .graphicsLayer { alpha = 1 - headlineAlpha }
                )
            }

            item {
                HorizontalDivider()
            }

            item {
                val packageWeight = state.product.packageWeight

                if (packageWeight != null) {
                    Column {
                        formState.packageInput.NoNameInput(
                            asCalories = {
                                val weight = it * packageWeight
                                state.product.nutrients.calories(weight).roundToInt()
                            },
                            asGrams = { (it * packageWeight).roundToInt() },
                            onConfirm = {
                                onConfirm(
                                    WeightMeasurementEnum.Package,
                                    formState.packageInput.value
                                )
                            },
                            label = { Text(stringResource(R.string.product_package)) },
                            modifier = Modifier
                                .then(
                                    if (highlight == WeightMeasurementEnum.Package) {
                                        Modifier.background(MaterialTheme.colorScheme.surfaceVariant)
                                    } else {
                                        Modifier
                                    }
                                )
                                .padding(8.dp)
                        )
                        HorizontalDivider()
                    }
                }
            }

            item {
                val servingWeight = state.product.servingWeight

                if (servingWeight != null) {
                    Column {
                        formState.servingInput.NoNameInput(
                            asCalories = {
                                val weight = it * servingWeight
                                state.product.nutrients.calories(weight).roundToInt()
                            },
                            asGrams = { (it * servingWeight).roundToInt() },
                            onConfirm = {
                                onConfirm(
                                    WeightMeasurementEnum.Serving,
                                    formState.servingInput.value
                                )
                            },
                            label = { Text(stringResource(R.string.product_serving)) },
                            modifier = Modifier
                                .then(
                                    if (highlight == WeightMeasurementEnum.Serving) {
                                        Modifier.background(MaterialTheme.colorScheme.surfaceVariant)
                                    } else {
                                        Modifier
                                    }
                                )
                                .padding(8.dp)
                        )
                        HorizontalDivider()
                    }
                }
            }

            item {
                formState.weightUnitInput.WeightUnitInput(
                    asCalories = { state.product.nutrients.calories(it).roundToInt() },
                    onConfirm = {
                        onConfirm(WeightMeasurementEnum.WeightUnit, formState.weightUnitInput.value)
                    },
                    suffix = { Text(stringResource(R.string.unit_gram_short)) },
                    modifier = Modifier
                        .then(
                            if (highlight == WeightMeasurementEnum.WeightUnit) {
                                Modifier.background(MaterialTheme.colorScheme.surfaceVariant)
                            } else {
                                Modifier
                            }
                        )
                        .padding(8.dp)
                )
            }

            item {
                HorizontalDivider()
            }

            item {
                Spacer(Modifier.height(16.dp))
            }

            item {
                Column(
                    modifier = Modifier.padding(horizontal = 16.dp)
                ) {
                    Text(
                        text = stringResource(R.string.headline_macronutrients),
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.labelLarge
                    )

                    MacroGraph(
                        product = state.product,
                        measurement = formState.latestMeasurement
                    )
                }
            }

            item {
                Spacer(Modifier.height(16.dp))
            }

            item {
                HorizontalDivider()
            }

            item {
                Spacer(Modifier.height(16.dp))
            }

            item {
                Column {
                    Text(
                        text = stringResource(R.string.headline_nutrients),
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.labelLarge,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )

                    NutrientsList(
                        state = rememberNutrientsListState(
                            product = state.product,
                            extraFilters = listOf(formState.latestMeasurement)
                        ),
                        paddingValues = PaddingValues(horizontal = 16.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun FormFieldWithTextFieldValue<Float, MyError>.NoNameInput(
    asCalories: (Float) -> Int,
    asGrams: (Float) -> Int,
    onConfirm: () -> Unit,
    label: @Composable () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = Modifier
            .clickable { onConfirm() }
            .then(modifier),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = textFieldValue,
            onValueChange = { onValueChange(it) },
            modifier = Modifier.weight(2f),
            shape = MaterialTheme.shapes.medium,
            isError = error != null,
            label = label,
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Done,
                keyboardType = KeyboardType.Number
            )
        )

        val weight: @Composable () -> Unit = {
            Text(
                text = "${asGrams(value)} " + stringResource(R.string.unit_gram_short),
                overflow = TextOverflow.Visible,
                textAlign = TextAlign.Center
            )
        }
        val calories: @Composable () -> Unit = {
            Text(
                text = "${asCalories(value)} " + stringResource(R.string.unit_kcal),
                overflow = TextOverflow.Visible,
                textAlign = TextAlign.Center
            )
        }

        Layout(
            content = {
                weight()
                calories()
            },
            modifier = Modifier.weight(2f)
        ) { (weightMeasurable, caloriesMeasurable), constraints ->
            val weightWidth = weightMeasurable.minIntrinsicWidth(constraints.maxHeight)
            val caloriesWidth = caloriesMeasurable.minIntrinsicWidth(constraints.maxHeight)

            val halfWidth = constraints.maxWidth / 2

            if (weightWidth < halfWidth && caloriesWidth < halfWidth) {
                val weightPlaceable = weightMeasurable.measure(
                    constraints.copy(
                        minWidth = halfWidth,
                        maxWidth = halfWidth
                    )
                )
                val caloriesPlaceable = caloriesMeasurable.measure(
                    constraints.copy(
                        minWidth = halfWidth,
                        maxWidth = halfWidth
                    )
                )
                val height = max(weightPlaceable.height, caloriesPlaceable.height)

                layout(constraints.maxWidth, height) {
                    weightPlaceable.placeRelative(0, (height - weightPlaceable.height) / 2)
                    caloriesPlaceable.placeRelative(
                        halfWidth,
                        (height - caloriesPlaceable.height) / 2
                    )
                }
            } else {
                val caloriesPlaceable = caloriesMeasurable.measure(constraints)

                layout(constraints.maxWidth, caloriesPlaceable.height) {
                    caloriesPlaceable.placeRelative(0, 0)
                }
            }
        }

        FilledIconButton(
            onClick = onConfirm,
            enabled = error == null
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = stringResource(R.string.action_add)
            )
        }
    }
}

@Composable
private fun FormFieldWithTextFieldValue<Float, MyError>.WeightUnitInput(
    asCalories: (Float) -> Int,
    onConfirm: () -> Unit,
    suffix: @Composable () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = Modifier
            .clickable { onConfirm() }
            .then(modifier),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = textFieldValue,
            onValueChange = { onValueChange(it) },
            modifier = Modifier.weight(2f),
            shape = MaterialTheme.shapes.medium,
            isError = error != null,
            label = { Text(stringResource(R.string.weight)) },
            suffix = suffix,
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Done,
                keyboardType = KeyboardType.Number
            )
        )

        Text(
            text = "${asCalories(value)} " + stringResource(R.string.unit_kcal),
            modifier = Modifier.weight(2f),
            textAlign = TextAlign.Center
        )

        FilledIconButton(
            onClick = onConfirm,
            enabled = error == null
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = stringResource(R.string.action_add)
            )
        }
    }
}

@Preview
@Composable
private fun PortionScreenPreview() {
    val product = ProductPreviewParameterProvider().values.first {
        it.packageWeight != null && it.servingWeight != null
    }

    FoodYouTheme {
        PortionScreen(
            state = PortionUiState.Ready(
                product = product,
                suggestion = QuantitySuggestion(
                    product = product,
                    quantitySuggestions = QuantitySuggestion.defaultSuggestion()
                )
            ),
            onBack = {},
            onConfirm = { _, _ -> },
            highlight = WeightMeasurementEnum.Serving
        )
    }
}

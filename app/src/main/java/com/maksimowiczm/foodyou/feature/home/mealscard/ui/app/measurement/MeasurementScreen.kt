package com.maksimowiczm.foodyou.feature.home.mealscard.ui.app.measurement

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.add
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
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
import com.maksimowiczm.foodyou.R
import com.maksimowiczm.foodyou.data.model.QuantitySuggestion
import com.maksimowiczm.foodyou.data.model.WeightMeasurementEnum
import com.maksimowiczm.foodyou.data.model.WeightUnit
import com.maksimowiczm.foodyou.ui.form.FormFieldWithTextFieldValue
import com.maksimowiczm.foodyou.ui.preview.ProductPreviewParameterProvider
import com.maksimowiczm.foodyou.ui.res.stringResourceShort
import com.maksimowiczm.foodyou.ui.theme.FoodYouTheme
import kotlin.math.max
import kotlin.math.roundToInt
import kotlinx.coroutines.flow.collectLatest

@Composable
fun MeasurementScreen(
    onBack: () -> Unit,
    onSuccess: () -> Unit,
    onEditClick: (productId: Long) -> Unit,
    onDelete: (productId: Long) -> Unit,
    viewModel: MeasurementViewModel,
    modifier: Modifier = Modifier
) {
    val event by viewModel.uiEvent.collectAsStateWithLifecycle()

    val state = rememberMeasurementState()
    val latestOnSuccess by rememberUpdatedState(onSuccess)

    LaunchedEffect(event) {
        @Suppress("NAME_SHADOWING")
        when (val event = event) {
            MeasurementEvent.Processing,
            MeasurementEvent.Empty,
            MeasurementEvent.Error,
            MeasurementEvent.Loading -> Unit

            is MeasurementEvent.Ready -> {
                state.suggestion = event.suggestion
                state.weightMeasurementEnum = event.highlight
            }

            MeasurementEvent.Success -> latestOnSuccess()
        }
    }

    MeasurementScreen(
        state = state,
        onBack = onBack,
        onConfirm = viewModel::onAddMeasurement,
        onEditClick = onEditClick,
        onDeleteClick = {
            viewModel.onProductDelete(it)
            onDelete(it)
        },
        modifier = modifier
    )
}

@Composable
private fun MeasurementScreen(
    state: MeasurementState,
    onBack: () -> Unit,
    onConfirm: (WeightMeasurementEnum, quantity: Float) -> Unit,
    onEditClick: (productId: Long) -> Unit,
    onDeleteClick: (productId: Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val hapticFeedback = LocalHapticFeedback.current

    fun internalOnConfirm(weightMeasurementEnum: WeightMeasurementEnum, quantity: Float) {
        hapticFeedback.performHapticFeedback(HapticFeedbackType.Confirm)
        state.weightMeasurementEnum = weightMeasurementEnum

        onConfirm(weightMeasurementEnum, quantity)
    }

    val suggestion = state.suggestion
    if (suggestion == null) {
        // TODO
        //  Might consider doing skeleton. It loads fast but what if it isn't fast enough?
        Surface { Spacer(Modifier.fillMaxSize()) }
    } else {
        MeasurementScreen(
            suggestion = suggestion,
            onBack = onBack,
            onConfirm = { weightMeasurementEnum, quantity ->
                internalOnConfirm(weightMeasurementEnum, quantity)
            },
            onEditClick = { onEditClick(state.suggestion!!.product.id) },
            onDeleteClick = { onDeleteClick(state.suggestion!!.product.id) },
            modifier = modifier,
            highlight = state.weightMeasurementEnum
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MeasurementScreen(
    suggestion: QuantitySuggestion,
    onBack: () -> Unit,
    onConfirm: (WeightMeasurementEnum, quantity: Float) -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    highlight: WeightMeasurementEnum?,
    modifier: Modifier = Modifier
) {
    var showDeleteDialog by rememberSaveable { mutableStateOf(false) }

    if (showDeleteDialog) {
        DeleteDialog(
            onDismissRequest = { showDeleteDialog = false },
            onDelete = onDeleteClick
        )
    }

    val formState = rememberMeasurementFormState(
        suggestion = suggestion
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
                        text = formState.product.name,
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
                    text = formState.product.name,
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
                val packageWeight = formState.product.packageWeight

                if (packageWeight != null) {
                    Column {
                        formState.packageInput.WeightUnitInput(
                            asCalories = {
                                val weight = it * packageWeight
                                formState.product.nutrients.calories(weight).roundToInt()
                            },
                            asGrams = { (it * packageWeight).roundToInt() },
                            onConfirm = {
                                onConfirm(
                                    WeightMeasurementEnum.Package,
                                    formState.packageInput.value
                                )
                            },
                            label = { Text(stringResource(R.string.product_package)) },
                            weightUnit = formState.product.weightUnit,
                            modifier = Modifier
                                .then(
                                    if (highlight == WeightMeasurementEnum.Package) {
                                        Modifier.background(
                                            MaterialTheme.colorScheme.surfaceVariant
                                        )
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
                val servingWeight = formState.product.servingWeight

                if (servingWeight != null) {
                    Column {
                        formState.servingInput.WeightUnitInput(
                            asCalories = {
                                val weight = it * servingWeight
                                formState.product.nutrients.calories(weight).roundToInt()
                            },
                            asGrams = { (it * servingWeight).roundToInt() },
                            onConfirm = {
                                onConfirm(
                                    WeightMeasurementEnum.Serving,
                                    formState.servingInput.value
                                )
                            },
                            label = { Text(stringResource(R.string.product_serving)) },
                            weightUnit = formState.product.weightUnit,
                            modifier = Modifier
                                .then(
                                    if (highlight == WeightMeasurementEnum.Serving) {
                                        Modifier.background(
                                            MaterialTheme.colorScheme.surfaceVariant
                                        )
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
                    asCalories = { formState.product.nutrients.calories(it).roundToInt() },
                    onConfirm = {
                        onConfirm(
                            WeightMeasurementEnum.WeightUnit,
                            formState.weightUnitInput.value
                        )
                    },
                    suffix = { Text(formState.product.weightUnit.stringResourceShort()) },
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
                        product = formState.product,
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
                            product = formState.product,
                            extraFilters = listOf(formState.latestMeasurement)
                        ),
                        paddingValues = PaddingValues(horizontal = 16.dp)
                    )
                }
            }

            item {
                Spacer(Modifier.height(16.dp))
            }

            item {
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(
                        8.dp,
                        Alignment.CenterHorizontally
                    )
                ) {
                    Button(
                        onClick = onEditClick
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = null
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(stringResource(R.string.action_edit))
                    }

                    OutlinedButton(
                        onClick = { showDeleteDialog = true }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = null
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(stringResource(R.string.action_delete))
                    }
                }
            }

            item {
                Spacer(Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun FormFieldWithTextFieldValue<Float, MyError>.WeightUnitInput(
    asCalories: (Float) -> Int,
    asGrams: (Float) -> Int,
    onConfirm: () -> Unit,
    label: @Composable () -> Unit,
    weightUnit: WeightUnit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = Modifier
            .clickable(
                onClick = onConfirm,
                enabled = error == null
            )
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
            ),
            keyboardActions = KeyboardActions {
                if (error == null) {
                    onConfirm()
                }
            }
        )

        val weight: @Composable () -> Unit = {
            Text(
                text = "${asGrams(value)} " + weightUnit.stringResourceShort(),
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
            .clickable(
                onClick = onConfirm,
                enabled = error == null
            )
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
            ),
            keyboardActions = KeyboardActions {
                if (error == null) {
                    onConfirm()
                }
            }
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

@Composable
private fun DeleteDialog(onDismissRequest: () -> Unit, onDelete: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(
                onClick = onDelete
            ) {
                Text(stringResource(R.string.action_delete))
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismissRequest
            ) {
                Text(stringResource(R.string.action_cancel))
            }
        },
        icon = {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = null
            )
        },
        title = {
            Text(stringResource(R.string.headline_delete_product))
        },
        text = {
            Text(stringResource(R.string.description_delete_product))
        }
    )
}

@Preview
@Composable
private fun MeasurementScreenPreview() {
    val product = ProductPreviewParameterProvider().values.first {
        it.packageWeight != null && it.servingWeight != null
    }

    FoodYouTheme {
        MeasurementScreen(
            suggestion = QuantitySuggestion(
                product = product,
                quantitySuggestions = QuantitySuggestion.defaultSuggestion
            ),
            onBack = {},
            onConfirm = { _, _ -> },
            onEditClick = {},
            onDeleteClick = {},
            highlight = WeightMeasurementEnum.Serving
        )
    }
}

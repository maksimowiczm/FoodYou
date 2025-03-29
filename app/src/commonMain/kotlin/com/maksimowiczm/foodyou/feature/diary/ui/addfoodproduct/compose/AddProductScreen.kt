package com.maksimowiczm.foodyou.feature.diary.ui.addfoodproduct.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.maksimowiczm.foodyou.feature.diary.data.model.NutrientValue
import com.maksimowiczm.foodyou.feature.diary.data.model.Nutrients
import com.maksimowiczm.foodyou.feature.diary.data.model.WeightMeasurement
import com.maksimowiczm.foodyou.feature.diary.data.model.WeightMeasurementEnum
import com.maksimowiczm.foodyou.feature.diary.ui.addfoodproduct.MeasurementViewModel
import com.maksimowiczm.foodyou.feature.diary.ui.addfoodproduct.model.Product
import com.maksimowiczm.foodyou.feature.diary.ui.component.CaloriesProgressIndicator
import com.maksimowiczm.foodyou.feature.diary.ui.component.NutrientListItem
import com.maksimowiczm.foodyou.ui.res.formatClipZeros
import com.maksimowiczm.foodyou.ui.theme.LocalNutrientsPalette
import foodyou.app.generated.resources.*
import foodyou.app.generated.resources.Res
import kotlin.math.roundToInt
import kotlinx.coroutines.flow.collectLatest
import org.jetbrains.compose.resources.stringResource

@Composable
fun AddProductScreen(
    onBack: () -> Unit,
    onEditProduct: (productId: Long) -> Unit,
    viewModel: MeasurementViewModel,
    modifier: Modifier = Modifier
) {
    val isDone by viewModel.done.collectAsStateWithLifecycle()
    LaunchedEffect(isDone, onBack) {
        if (isDone) {
            onBack()
        }
    }

    val product by viewModel.product.collectAsStateWithLifecycle(null)
    val highlight by viewModel.processing.collectAsStateWithLifecycle()
    val hapticFeedback = LocalHapticFeedback.current

    when (val product = product) {
        null -> Surface(modifier) { Spacer(Modifier.fillMaxSize()) }
        else -> AddProductScreen(
            onBack = onBack,
            onEditProduct = {
                onEditProduct(product.id)
            },
            onDeleteProduct = viewModel::onDelete,
            onConfirm = { weightMeasurement ->
                hapticFeedback.performHapticFeedback(HapticFeedbackType.Confirm)
                viewModel.onConfirm(weightMeasurement)
            },
            product = product,
            highlight = highlight ?: product.highlight,
            modifier = modifier
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddProductScreen(
    onBack: () -> Unit,
    onEditProduct: () -> Unit,
    onDeleteProduct: () -> Unit,
    onConfirm: (weightMeasurement: WeightMeasurement) -> Unit,
    product: Product,
    highlight: WeightMeasurementEnum?,
    modifier: Modifier = Modifier
) {
    val formState = rememberAddProductState(
        packageSuggestion = product.packageSuggestion,
        servingSuggestion = product.servingSuggestion,
        weightSuggestion = product.weightSuggestions
    )
    val chipsState = rememberWeightChipsState(
        product = product,
        extraFilter = formState.latestWeightMeasurement
    )

    val containerColor = MaterialTheme.colorScheme.surfaceContainer
    val contentColor = MaterialTheme.colorScheme.onSurface
    val highlightContainerColor = MaterialTheme.colorScheme.primaryContainer
    val highlightContentColor = MaterialTheme.colorScheme.onPrimaryContainer

    fun containerColor(weightMeasurementEnum: WeightMeasurementEnum) =
        if (highlight == weightMeasurementEnum) highlightContainerColor else containerColor

    fun contentColor(weightMeasurementEnum: WeightMeasurementEnum) =
        if (highlight == weightMeasurementEnum) highlightContentColor else contentColor

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

    var showDeleteDialog by rememberSaveable { mutableStateOf(false) }

    if (showDeleteDialog) {
        DeleteDialog(
            onDismissRequest = { showDeleteDialog = false },
            onDelete = onDeleteProduct
        )
    }

    Scaffold(
        modifier = modifier.imePadding(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = product.name,
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
                            imageVector = Icons.AutoMirrored.Default.ArrowBack,
                            contentDescription = stringResource(Res.string.action_go_back)
                        )
                    }
                },
                scrollBehavior = scrollBehavior,
                colors = TopAppBarDefaults.topAppBarColors(
                    scrolledContainerColor = MaterialTheme.colorScheme.surfaceContainerHighest
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            state = lazyListState,
            contentPadding = paddingValues
        ) {
            item {
                Text(
                    text = product.name,
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
                val field = formState.packageField ?: return@item
                val measurement = product.packageSuggestion ?: return@item

                val contentColor = contentColor(WeightMeasurementEnum.Package)
                val containerColor = containerColor(WeightMeasurementEnum.Package)

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(
                            enabled = field.error == null
                        ) {
                            onConfirm(measurement.copy(quantity = field.value))
                        }
                        .background(containerColor)
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        state = field.textFieldState,
                        modifier = Modifier.weight(2f),
                        isError = field.error != null,
                        label = { Text(stringResource(Res.string.product_package)) },
                        shape = MaterialTheme.shapes.medium,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Done
                        ),
                        onKeyboardAction = {
                            if (field.error == null) {
                                onConfirm(measurement.copy(quantity = field.value))
                            }
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = contentColor,
                            unfocusedTextColor = contentColor,
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedLabelColor = contentColor,
                            unfocusedLabelColor = contentColor,
                            focusedSuffixColor = contentColor,
                            unfocusedSuffixColor = contentColor,
                            unfocusedBorderColor = contentColor,
                            focusedBorderColor = contentColor
                        )
                    )

                    WeightCaloriesLayout(
                        weight = {
                            val weight = (field.value * product.packageWeight!!).roundToInt()
                            val g = stringResource(Res.string.unit_gram_short)
                            Text(
                                text = "$weight $g",
                                overflow = TextOverflow.Visible,
                                textAlign = TextAlign.Center
                            )
                        },
                        calories = {
                            val weight = field.value * product.packageWeight!!
                            val calories = (weight * product.nutrients.calories / 100).roundToInt()
                            val kcal = stringResource(Res.string.unit_kcal)
                            Text(
                                text = "$calories $kcal",
                                overflow = TextOverflow.Visible,
                                textAlign = TextAlign.Center
                            )
                        },
                        modifier = Modifier
                            .weight(2f)
                            .clipToBounds()
                    )

                    FilledIconButton(
                        onClick = {
                            onConfirm(measurement.copy(quantity = field.value))
                        },
                        enabled = field.error == null
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.KeyboardArrowRight,
                            contentDescription = stringResource(Res.string.action_add)
                        )
                    }
                }

                HorizontalDivider()
            }

            item {
                val field = formState.servingField ?: return@item
                val measurement = product.servingSuggestion ?: return@item
                val contentColor = contentColor(WeightMeasurementEnum.Serving)
                val containerColor = containerColor(WeightMeasurementEnum.Serving)

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(
                            enabled = field.error == null
                        ) {
                            onConfirm(measurement.copy(quantity = field.value))
                        }
                        .background(containerColor)
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        state = field.textFieldState,
                        modifier = Modifier.weight(2f),
                        isError = field.error != null,
                        label = { Text(stringResource(Res.string.product_serving)) },
                        shape = MaterialTheme.shapes.medium,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Done
                        ),
                        onKeyboardAction = {
                            if (field.error == null) {
                                onConfirm(measurement.copy(quantity = field.value))
                            }
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = contentColor,
                            unfocusedTextColor = contentColor,
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedLabelColor = contentColor,
                            unfocusedLabelColor = contentColor,
                            focusedSuffixColor = contentColor,
                            unfocusedSuffixColor = contentColor,
                            unfocusedBorderColor = contentColor,
                            focusedBorderColor = contentColor
                        )
                    )

                    WeightCaloriesLayout(
                        weight = {
                            val weight = (field.value * product.servingWeight!!).roundToInt()
                            val g = stringResource(Res.string.unit_gram_short)
                            Text(
                                text = "$weight $g",
                                overflow = TextOverflow.Visible,
                                textAlign = TextAlign.Center
                            )
                        },
                        calories = {
                            val weight = field.value * product.servingWeight!!
                            val calories = (weight * product.nutrients.calories / 100).roundToInt()
                            val kcal = stringResource(Res.string.unit_kcal)
                            Text(
                                text = "$calories $kcal",
                                overflow = TextOverflow.Visible,
                                textAlign = TextAlign.Center
                            )
                        },
                        modifier = Modifier
                            .weight(2f)
                            .clipToBounds()

                    )

                    FilledIconButton(
                        onClick = {
                            onConfirm(measurement.copy(quantity = field.value))
                        },
                        enabled = field.error == null
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.KeyboardArrowRight,
                            contentDescription = stringResource(Res.string.action_add)
                        )
                    }
                }

                HorizontalDivider()
            }

            item {
                val field = formState.weightField
                val contentColor = contentColor(WeightMeasurementEnum.WeightUnit)
                val containerColor = containerColor(WeightMeasurementEnum.WeightUnit)

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(
                            enabled = field.error == null
                        ) {
                            onConfirm(WeightMeasurement.WeightUnit(field.value))
                        }
                        .background(containerColor)
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        state = field.textFieldState,
                        modifier = Modifier.weight(2f),
                        isError = field.error != null,
                        label = { Text(stringResource(Res.string.weight)) },
                        suffix = { Text(stringResource(Res.string.unit_gram_short)) },
                        shape = MaterialTheme.shapes.medium,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Done
                        ),
                        onKeyboardAction = {
                            if (field.error == null) {
                                onConfirm(WeightMeasurement.WeightUnit(field.value))
                            }
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = contentColor,
                            unfocusedTextColor = contentColor,
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedLabelColor = contentColor,
                            unfocusedLabelColor = contentColor,
                            focusedSuffixColor = contentColor,
                            unfocusedSuffixColor = contentColor,
                            unfocusedBorderColor = contentColor,
                            focusedBorderColor = contentColor
                        )
                    )

                    val calories = (field.value * product.nutrients.calories / 100).roundToInt()
                    val kcal = stringResource(Res.string.unit_kcal)
                    Text(
                        text = "$calories $kcal",
                        overflow = TextOverflow.Visible,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.weight(2f)
                    )

                    FilledIconButton(
                        onClick = {
                            onConfirm(WeightMeasurement.WeightUnit(field.value))
                        },
                        enabled = field.error == null
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.KeyboardArrowRight,
                            contentDescription = stringResource(Res.string.action_add)
                        )
                    }
                }

                HorizontalDivider()
            }

            item {
                Spacer(Modifier.height(8.dp))
            }

            item {
                Text(
                    text = stringResource(Res.string.headline_nutrients),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }

            item {
                Spacer(Modifier.height(8.dp))
            }

            item {
                Text(
                    text = stringResource(Res.string.neutral_all_values_per_x, ""),
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }

            item {
                WeightChips(
                    state = chipsState,
                    contentPadding = PaddingValues(horizontal = 16.dp)
                )
            }

            item {
                Spacer(Modifier.height(8.dp))
            }

            item {
                CaloriesProgressIndicator(
                    proteins = product.nutrients.proteins,
                    carbohydrates = product.nutrients.carbohydrates,
                    fats = product.nutrients.fats,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(16.dp)
                        .padding(horizontal = 16.dp)
                )
            }

            item {
                Spacer(Modifier.height(8.dp))
            }

            item {
                val weight = when (val measurement = chipsState.selectedFilter) {
                    is WeightMeasurement.Package -> {
                        measurement.quantity * product.packageWeight!!
                    }

                    is WeightMeasurement.Serving -> {
                        measurement.quantity * product.servingWeight!!
                    }

                    is WeightMeasurement.WeightUnit -> {
                        measurement.weight
                    }
                }

                NutrientsList(
                    nutrients = product.nutrients,
                    weight = weight,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
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
                        onClick = onEditProduct
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = null
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(stringResource(Res.string.action_edit))
                    }

                    OutlinedButton(
                        onClick = { showDeleteDialog = true }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = null
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(stringResource(Res.string.action_delete))
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
private fun NutrientsList(nutrients: Nutrients, weight: Float, modifier: Modifier = Modifier) {
    val nutrientsPalette = LocalNutrientsPalette.current

    @Composable
    fun Item(label: String, value: NutrientValue) {
        CompositionLocalProvider(
            LocalTextStyle provides MaterialTheme.typography.titleMedium
        ) {
            NutrientListItem(
                label = { Text(label) },
                value = {
                    when (value) {
                        is NutrientValue.Complete -> {
                            val g = stringResource(Res.string.unit_gram_short)
                            Text(
                                "${value.value.formatClipZeros()} $g"
                            )
                        }

                        is NutrientValue.Incomplete -> {
                            Text(
                                text = stringResource(Res.string.not_available_short),
                                color = MaterialTheme.colorScheme.outline
                            )
                        }
                    }
                },
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }
    }

    @Composable
    fun Item(label: @Composable () -> Unit, value: @Composable () -> Unit) {
        CompositionLocalProvider(
            LocalTextStyle provides MaterialTheme.typography.titleMedium
        ) {
            NutrientListItem(
                label = label,
                value = value,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }
    }
    Column(
        modifier = modifier
    ) {
        Item(
            label = { Text(stringResource(Res.string.unit_calories)) },
            value = {
                val value = (nutrients.calories * weight / 100).formatClipZeros()
                Text("$value ${stringResource(Res.string.unit_kcal)}")
            }
        )

        HorizontalDivider(Modifier.padding(horizontal = 48.dp))

        Item(
            label = {
                Text(
                    text = stringResource(Res.string.nutriment_proteins),
                    color = nutrientsPalette.proteinsOnSurfaceContainer
                )
            },
            value = {
                val value = (nutrients.proteins * weight / 100).formatClipZeros()
                Text("$value ${stringResource(Res.string.unit_gram_short)}")
            }
        )

        HorizontalDivider(Modifier.padding(horizontal = 48.dp))

        Item(
            label = {
                Text(
                    text = stringResource(Res.string.nutriment_carbohydrates),
                    color = nutrientsPalette.carbohydratesOnSurfaceContainer
                )
            },
            value = {
                val value = (nutrients.carbohydrates * weight / 100).formatClipZeros()
                Text("$value ${stringResource(Res.string.unit_gram_short)}")
            }
        )

        HorizontalDivider(Modifier.padding(horizontal = 48.dp))

        Item(
            label = stringResource(Res.string.nutriment_sugars),
            value = nutrients.sugars * weight / 100f
        )

        HorizontalDivider(Modifier.padding(horizontal = 48.dp))

        Item(
            label = {
                Text(
                    text = stringResource(Res.string.nutriment_fats),
                    color = nutrientsPalette.fatsOnSurfaceContainer
                )
            },
            value = {
                val value = (nutrients.fats * weight / 100).formatClipZeros()
                Text("$value ${stringResource(Res.string.unit_gram_short)}")
            }
        )

        HorizontalDivider(Modifier.padding(horizontal = 48.dp))

        Item(
            label = stringResource(Res.string.nutriment_saturated_fats),
            value = nutrients.saturatedFats * weight / 100f
        )

        HorizontalDivider(Modifier.padding(horizontal = 48.dp))

        Item(
            label = stringResource(Res.string.nutriment_salt),
            value = nutrients.salt * weight / 100f
        )

        HorizontalDivider(Modifier.padding(horizontal = 48.dp))

        Item(
            label = stringResource(Res.string.nutriment_sodium),
            value = nutrients.sodium * weight / 100f
        )

        HorizontalDivider(Modifier.padding(horizontal = 48.dp))

        Item(
            label = stringResource(Res.string.nutriment_fiber),
            value = nutrients.fiber * weight / 100f
        )
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
                Text(stringResource(Res.string.action_delete))
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismissRequest
            ) {
                Text(stringResource(Res.string.action_cancel))
            }
        },
        icon = {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = null
            )
        },
        title = {
            Text(stringResource(Res.string.headline_delete_product))
        },
        text = {
            Text(stringResource(Res.string.description_delete_product))
        }
    )
}

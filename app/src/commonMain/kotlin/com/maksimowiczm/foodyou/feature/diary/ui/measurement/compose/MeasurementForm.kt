package com.maksimowiczm.foodyou.feature.diary.ui.measurement.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.maksimowiczm.foodyou.feature.diary.data.model.Nutrients
import com.maksimowiczm.foodyou.feature.diary.data.model.WeightMeasurement
import com.maksimowiczm.foodyou.feature.diary.data.model.WeightMeasurementEnum
import com.maksimowiczm.foodyou.feature.diary.ui.component.CaloriesProgressIndicator
import com.maksimowiczm.foodyou.feature.diary.ui.component.NutrientsList
import com.maksimowiczm.foodyou.ui.res.formatClipZeros
import com.maksimowiczm.foodyou.ui.simpleform.FormField
import foodyou.app.generated.resources.*
import kotlin.math.roundToInt
import org.jetbrains.compose.resources.stringResource

/**
 * Measurement screen for editing or deleting a measurement.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MeasurementForm(
    name: String,
    nutrients: Nutrients,
    packageWeight: Float?,
    servingWeight: Float?,
    state: MeasurementFormState,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onConfirm: (WeightMeasurement) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    @Composable
    fun contentColor(enum: WeightMeasurementEnum) = if (enum == state.highlight) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.onSurface
    }

    @Composable
    fun containerColor(enum: WeightMeasurementEnum) = if (enum == state.highlight) {
        MaterialTheme.colorScheme.primaryContainer
    } else {
        MaterialTheme.colorScheme.surfaceContainer
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(Res.string.action_go_back)
                        )
                    }
                },
                scrollBehavior = scrollBehavior
            )
        },
        modifier = modifier
    ) { paddingValues ->
        LazyColumn(
            contentPadding = paddingValues,
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
        ) {
            item {
                Text(
                    text = name,
                    style = MaterialTheme.typography.headlineLarge,
                    modifier = Modifier.padding(16.dp)
                )
            }

            item {
                HorizontalDivider()
            }

            item {
                if (state.servingField != null && servingWeight != null) {
                    state.ServingInput(
                        field = state.servingField,
                        containerColor = containerColor(WeightMeasurementEnum.Serving),
                        contentColor = contentColor(WeightMeasurementEnum.Serving),
                        onConfirm = {
                            state.highlight = WeightMeasurementEnum.Serving
                            onConfirm(WeightMeasurement.Serving(state.servingField.value))
                        },
                        getWeight = {
                            servingWeight * it
                        },
                        getCalories = {
                            nutrients.calories * it * servingWeight / 100f
                        },
                        modifier = Modifier.padding(8.dp)
                    )
                    HorizontalDivider()
                }
            }

            item {
                if (state.packageField != null && packageWeight != null) {
                    state.PackageInput(
                        field = state.packageField,
                        containerColor = containerColor(WeightMeasurementEnum.Package),
                        contentColor = contentColor(WeightMeasurementEnum.Package),
                        onConfirm = {
                            state.highlight = WeightMeasurementEnum.Package
                            onConfirm(WeightMeasurement.Package(state.packageField.value))
                        },
                        getWeight = {
                            packageWeight * it
                        },
                        getCalories = {
                            nutrients.calories * it * packageWeight / 100f
                        },
                        modifier = Modifier.padding(8.dp)
                    )
                    HorizontalDivider()
                }
            }

            item {
                state.WeightInput(
                    field = state.weightField,
                    containerColor = containerColor(WeightMeasurementEnum.WeightUnit),
                    contentColor = contentColor(WeightMeasurementEnum.WeightUnit),
                    onConfirm = {
                        state.highlight = WeightMeasurementEnum.WeightUnit
                        onConfirm(WeightMeasurement.WeightUnit(state.weightField.value))
                    },
                    getCalories = {
                        nutrients.calories * it / 100f
                    },
                    modifier = Modifier.padding(8.dp)
                )
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
                val text = when (val m = state.latestWeightMeasurement) {
                    is WeightMeasurement.Package -> stringResource(
                        Res.string.in_x_times_y,
                        stringResource(Res.string.product_package),
                        m.quantity.formatClipZeros()
                    )

                    is WeightMeasurement.Serving -> stringResource(
                        Res.string.in_x_times_y,
                        stringResource(Res.string.product_serving),
                        m.quantity.formatClipZeros()
                    )

                    is WeightMeasurement.WeightUnit -> stringResource(
                        Res.string.in_x_weight_unit,
                        m.weight.formatClipZeros(),
                        stringResource(Res.string.unit_gram_short)
                    )
                }

                Text(
                    text = text,
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }

            item {
                Spacer(Modifier.height(8.dp))
            }

            item {
                CaloriesProgressIndicator(
                    proteins = nutrients.proteins,
                    carbohydrates = nutrients.carbohydrates,
                    fats = nutrients.fats,
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
                NutrientsList(
                    nutrients = nutrients,
                    incompleteValue = {
                        {
                            Text(
                                text = stringResource(Res.string.not_available_short),
                                color = MaterialTheme.colorScheme.outline
                            )
                        }
                    },
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }

            item {
                Spacer(Modifier.height(8.dp))
            }

            item {
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)
                ) {
                    Button(
                        onClick = onEdit
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = null
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(stringResource(Res.string.action_edit))
                    }

                    OutlinedButton(
                        onClick = onDelete
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
fun MeasurementFormState.ServingInput(
    field: FormField<Float, *>,
    containerColor: Color,
    contentColor: Color,
    onConfirm: () -> Unit,
    getWeight: (Float) -> Float,
    getCalories: (Float) -> Float,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = Modifier
            .clickable(
                enabled = field.error == null
            ) {
                onConfirm()
            }
            .background(containerColor)
            .then(modifier),
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
                    onConfirm()
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
                val weight = getWeight(field.value).roundToInt()
                val g = stringResource(Res.string.unit_gram_short)
                Text(
                    text = "$weight $g",
                    overflow = TextOverflow.Visible,
                    textAlign = TextAlign.Center
                )
            },
            calories = {
                val calories = getCalories(field.value).roundToInt()
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
                onConfirm()
            },
            enabled = field.error == null
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Default.KeyboardArrowRight,
                contentDescription = stringResource(Res.string.action_add)
            )
        }
    }
}

@Composable
fun MeasurementFormState.PackageInput(
    field: FormField<Float, *>,
    containerColor: Color,
    contentColor: Color,
    onConfirm: () -> Unit,
    getWeight: (Float) -> Float,
    getCalories: (Float) -> Float,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = Modifier
            .clickable(
                enabled = field.error == null
            ) {
                onConfirm()
            }
            .background(containerColor)
            .then(modifier),
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
                    onConfirm()
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
                val weight = getWeight(field.value).roundToInt()
                val g = stringResource(Res.string.unit_gram_short)
                Text(
                    text = "$weight $g",
                    overflow = TextOverflow.Visible,
                    textAlign = TextAlign.Center
                )
            },
            calories = {
                val calories = getCalories(field.value).roundToInt()
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
                onConfirm()
            },
            enabled = field.error == null
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Default.KeyboardArrowRight,
                contentDescription = stringResource(Res.string.action_add)
            )
        }
    }
}

@Composable
fun MeasurementFormState.WeightInput(
    field: FormField<Float, *>,
    containerColor: Color,
    contentColor: Color,
    onConfirm: () -> Unit,
    getCalories: (Float) -> Float,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = Modifier
            .clickable(
                enabled = field.error == null
            ) {
                onConfirm()
            }
            .background(containerColor)
            .then(modifier),
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
                    onConfirm()
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

        val calories = getCalories(field.value).roundToInt()
        val kcal = stringResource(Res.string.unit_kcal)
        Text(
            text = "$calories $kcal",
            overflow = TextOverflow.Visible,
            textAlign = TextAlign.Center,
            modifier = Modifier.weight(2f)
        )

        FilledIconButton(
            onClick = onConfirm,
            enabled = field.error == null
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Default.KeyboardArrowRight,
                contentDescription = stringResource(Res.string.action_add)
            )
        }
    }
}

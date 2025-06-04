package com.maksimowiczm.foodyou.feature.measurement.ui.basic

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.maksimowiczm.foodyou.core.model.Measurement
import com.maksimowiczm.foodyou.core.ui.simpleform.FormField
import foodyou.app.generated.resources.*
import kotlin.math.roundToInt
import org.jetbrains.compose.resources.stringResource

@Composable
fun MeasurementForm(
    state: MeasurementFormState,
    onMeasurement: (Measurement) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    colors: MeasurementInputColors = MeasurementInputDefaults.colors()
) {
    Column(modifier = modifier) {
        if (state.packageInput != null && state.packageWeight != null) {
            MeasurementRow(
                isSelected = state.selected == MeasurementEnum.Package,
                state = state.packageInput,
                type = MeasurementEnum.Package,
                weight = { value -> (state.packageWeight * value).roundToInt() },
                calories = { weight ->
                    (state.nutrients.calories.value * weight / 100).roundToInt()
                },
                onMeasurement = { value -> onMeasurement(Measurement.Package(value)) },
                contentPadding = contentPadding,
                colors = colors
            )
            HorizontalDivider()
        }

        if (state.servingInput != null && state.servingWeight != null) {
            MeasurementRow(
                isSelected = state.selected == MeasurementEnum.Serving,
                state = state.servingInput,
                type = MeasurementEnum.Serving,
                weight = { value -> (state.servingWeight * value).roundToInt() },
                calories = { weight ->
                    (state.nutrients.calories.value * weight / 100).roundToInt()
                },
                onMeasurement = { value -> onMeasurement(Measurement.Serving(value)) },
                contentPadding = contentPadding,
                colors = colors
            )
            HorizontalDivider()
        }

        MeasurementRow(
            isSelected = state.selected == MeasurementEnum.Gram,
            state = state.gramInput,
            type = MeasurementEnum.Gram,
            weight = { it.roundToInt() },
            calories = { weight -> (state.nutrients.calories.value * weight / 100).roundToInt() },
            onMeasurement = { value -> onMeasurement(Measurement.Gram(value)) },
            contentPadding = contentPadding,
            colors = colors
        )
    }
}

@Composable
private fun MeasurementRow(
    isSelected: Boolean,
    state: FormField<Float, String>,
    type: MeasurementEnum,
    weight: (Float) -> Int,
    calories: (Float) -> Int,
    onMeasurement: (Float) -> Unit,
    contentPadding: PaddingValues,
    colors: MeasurementInputColors
) {
    val testTag = when (type) {
        MeasurementEnum.Package -> MeasurementFormTestKeys.Package(state.value)
        MeasurementEnum.Serving -> MeasurementFormTestKeys.Serving(state.value)
        MeasurementEnum.Gram -> MeasurementFormTestKeys.Gram(state.value)
    }

    val onConfirm = remember(state, onMeasurement) { { onMeasurement(state.value) } }

    val containerColor = if (isSelected) {
        colors.selectedContainerColor
    } else {
        colors.containerColor
    }

    Surface(
        onClick = onConfirm,
        enabled = state.error == null,
        color = containerColor
    ) {
        Row(
            modifier = Modifier.padding(contentPadding),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                state = state.textFieldState,
                modifier = Modifier
                    .weight(2f)
                    .testTag(testTag.toString()),
                isError = state.error != null,
                label = when (type) {
                    MeasurementEnum.Package,
                    MeasurementEnum.Serving -> {
                        { Text(type.stringResource()) }
                    }

                    MeasurementEnum.Gram -> null
                },
                suffix = if (type == MeasurementEnum.Gram) {
                    { Text(stringResource(Res.string.unit_gram_short)) }
                } else {
                    null
                },
                shape = MaterialTheme.shapes.medium,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done
                ),
                onKeyboardAction = { if (state.error == null) onConfirm() },
                colors = if (isSelected) {
                    colors.toSelectedTextFieldColors()
                } else {
                    colors.toTextFieldColors()
                }
            )

            val weightValue = weight(state.value)

            if (type != MeasurementEnum.Gram) {
                WeightCaloriesLayout(
                    weight = {
                        val g = stringResource(Res.string.unit_gram_short)
                        Text(
                            text = "$weightValue $g",
                            overflow = TextOverflow.Visible,
                            textAlign = TextAlign.Center
                        )
                    },
                    calories = {
                        val caloriesValue = calories(weightValue.toFloat())
                        val kcal = stringResource(Res.string.unit_kcal)
                        Text(
                            text = "$caloriesValue $kcal",
                            overflow = TextOverflow.Visible,
                            textAlign = TextAlign.Center
                        )
                    },
                    modifier = Modifier
                        .weight(2f)
                        .clipToBounds()
                )
            } else {
                val caloriesValue = calories(state.value)
                val kcal = stringResource(Res.string.unit_kcal)
                Text(
                    text = "$caloriesValue $kcal",
                    modifier = Modifier.weight(2f),
                    overflow = TextOverflow.Visible,
                    textAlign = TextAlign.Center
                )
            }

            FilledIconButton(
                onClick = onConfirm,
                enabled = state.error == null
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Default.KeyboardArrowRight,
                    contentDescription = stringResource(Res.string.action_add)
                )
            }
        }
    }
}

internal object MeasurementFormTestKeys {
    data class Package(val value: Float)
    data class Serving(val value: Float)
    data class Gram(val value: Float)
}

@Immutable
data class MeasurementInputColors(
    val containerColor: Color,
    val selectedContainerColor: Color,
    val contentColor: Color,
    val selectedContentColor: Color
) {
    @Composable
    fun toTextFieldColors(): TextFieldColors = OutlinedTextFieldDefaults.colors(
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

    @Composable
    fun toSelectedTextFieldColors(): TextFieldColors = OutlinedTextFieldDefaults.colors(
        focusedTextColor = selectedContentColor,
        unfocusedTextColor = selectedContentColor,
        focusedContainerColor = selectedContainerColor,
        unfocusedContainerColor = selectedContainerColor,
        focusedLabelColor = selectedContentColor,
        unfocusedLabelColor = selectedContentColor,
        focusedSuffixColor = selectedContentColor,
        unfocusedSuffixColor = selectedContentColor,
        unfocusedBorderColor = selectedContentColor,
        focusedBorderColor = selectedContentColor
    )
}

object MeasurementInputDefaults {
    @Composable
    fun colors(
        containerColor: Color = MaterialTheme.colorScheme.surface,
        selectedContainerColor: Color = MaterialTheme.colorScheme.primaryContainer,
        contentColor: Color = MaterialTheme.colorScheme.onSurface,
        selectedContentColor: Color = MaterialTheme.colorScheme.onPrimaryContainer
    ): MeasurementInputColors = MeasurementInputColors(
        containerColor = containerColor,
        selectedContainerColor = selectedContainerColor,
        contentColor = contentColor,
        selectedContentColor = selectedContentColor
    )
}

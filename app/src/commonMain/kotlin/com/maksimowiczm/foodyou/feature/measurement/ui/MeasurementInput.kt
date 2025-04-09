package com.maksimowiczm.foodyou.feature.measurement.ui

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.FilledIconButton
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import com.maksimowiczm.foodyou.core.model.Measurement
import com.maksimowiczm.foodyou.core.ui.res.formatClipZeros
import com.maksimowiczm.foodyou.core.ui.simpleform.FormField
import com.maksimowiczm.foodyou.core.ui.simpleform.ParseResult.Failure
import com.maksimowiczm.foodyou.core.ui.simpleform.ParseResult.Success
import com.maksimowiczm.foodyou.core.ui.simpleform.rememberFormField
import foodyou.app.generated.resources.*
import foodyou.app.generated.resources.Res
import kotlin.math.roundToInt
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun <M : Measurement> rememberMeasurementInputState(
    measurement: M
): MeasurementInputState<M> {
    val formField = when (measurement) {
        is Measurement.Gram -> rememberFormField(
            initialValue = measurement.value,
            parser = {
                when (val f = it.toFloatOrNull()) {
                    null -> Failure("Invalid number")
                    else -> Success(f)
                }
            },
            textFieldState = rememberTextFieldState(
                initialText = measurement.value.formatClipZeros()
            )
        )
        is Measurement.Package -> rememberFormField(
            initialValue = measurement.quantity,
            parser = {
                when (val f = it.toFloatOrNull()) {
                    null -> Failure("Invalid number")
                    else -> Success(f)
                }
            },
            textFieldState = rememberTextFieldState(
                initialText = measurement.quantity.formatClipZeros()
            )
        )

        is Measurement.Serving -> rememberFormField(
            initialValue = measurement.quantity,
            parser = {
                when (val f = it.toFloatOrNull()) {
                    null -> Failure("Invalid number")
                    else -> Success(f)
                }
            },
            textFieldState = rememberTextFieldState(
                initialText = measurement.quantity.formatClipZeros()
            )
        )
    }

    return remember(measurement, formField) {
        MeasurementInputState(
            measurement = measurement,
            formField = formField
        )
    }
}

internal class MeasurementInputState<M : Measurement>(
    val measurement: M,
    val formField: FormField<Float, String>
)

@Composable
internal fun ServingMeasurementInput(
    onConfirm: (Measurement) -> Unit,
    getWeight: (Measurement.Serving) -> Float,
    getCalories: (Measurement.Serving) -> Float,
    formField: FormField<Float, String>,
    contentPadding: PaddingValues,
    colors: MeasurementInputColors,
    modifier: Modifier = Modifier
) {
    val onConfirm = {
        val quantity = formField.value
        onConfirm(Measurement.Serving(quantity))
    }

    Surface(
        onClick = onConfirm,
        modifier = modifier,
        enabled = formField.error == null,
        color = colors.containerColor
    ) {
        Row(
            modifier = Modifier.padding(contentPadding),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                state = formField.textFieldState,
                modifier = Modifier.weight(2f),
                isError = formField.error != null,
                label = { Text(stringResource(Res.string.product_serving)) },
                shape = MaterialTheme.shapes.medium,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done
                ),
                onKeyboardAction = {
                    if (formField.error == null) {
                        onConfirm()
                    }
                },
                colors = colors.toTextFieldColors()
            )

            WeightCaloriesLayout(
                weight = {
                    val weight = getWeight(Measurement.Serving(formField.value)).roundToInt()
                    val g = stringResource(Res.string.unit_gram_short)
                    Text(
                        text = "$weight $g",
                        overflow = TextOverflow.Visible,
                        textAlign = TextAlign.Center
                    )
                },
                calories = {
                    val calories = getCalories(Measurement.Serving(formField.value)).roundToInt()
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
                onClick = onConfirm,
                enabled = formField.error == null
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Default.KeyboardArrowRight,
                    contentDescription = stringResource(Res.string.action_add)
                )
            }
        }
    }
}

@Composable
internal fun PackageMeasurementInput(
    onConfirm: (Measurement) -> Unit,
    getWeight: (Measurement.Package) -> Float,
    getCalories: (Measurement.Package) -> Float,
    formField: FormField<Float, String>,
    contentPadding: PaddingValues,
    colors: MeasurementInputColors,
    modifier: Modifier = Modifier
) {
    val onConfirm = {
        val quantity = formField.value
        onConfirm(Measurement.Package(quantity))
    }

    Surface(
        onClick = onConfirm,
        color = colors.containerColor,
        modifier = modifier,
        enabled = formField.error == null
    ) {
        Row(
            modifier = Modifier.padding(contentPadding),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                state = formField.textFieldState,
                modifier = Modifier.weight(2f),
                isError = formField.error != null,
                label = { Text(stringResource(Res.string.product_package)) },
                shape = MaterialTheme.shapes.medium,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done
                ),
                onKeyboardAction = {
                    if (formField.error == null) {
                        onConfirm()
                    }
                },
                colors = colors.toTextFieldColors()
            )

            WeightCaloriesLayout(
                weight = {
                    val weight = getWeight(Measurement.Package(formField.value)).roundToInt()
                    val g = stringResource(Res.string.unit_gram_short)
                    Text(
                        text = "$weight $g",
                        overflow = TextOverflow.Visible,
                        textAlign = TextAlign.Center
                    )
                },
                calories = {
                    val calories = getCalories(Measurement.Package(formField.value)).roundToInt()
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
                onClick = onConfirm,
                enabled = formField.error == null
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Default.KeyboardArrowRight,
                    contentDescription = stringResource(Res.string.action_add)
                )
            }
        }
    }
}

@Composable
internal fun GramMeasurementInput(
    onConfirm: (Measurement) -> Unit,
    getCalories: (Measurement.Gram) -> Float,
    formField: FormField<Float, String>,
    contentPadding: PaddingValues,
    colors: MeasurementInputColors,
    modifier: Modifier = Modifier
) {
    val onConfirm = {
        val quantity = formField.value
        onConfirm(Measurement.Gram(quantity))
    }

    Surface(
        onClick = onConfirm,
        modifier = modifier,
        color = colors.containerColor,
        enabled = formField.error == null
    ) {
        Row(
            modifier = Modifier.padding(contentPadding),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                state = formField.textFieldState,
                modifier = Modifier.weight(2f),
                isError = formField.error != null,
                label = { Text(stringResource(Res.string.weight)) },
                suffix = { Text(stringResource(Res.string.unit_gram_short)) },
                shape = MaterialTheme.shapes.medium,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done
                ),
                onKeyboardAction = {
                    if (formField.error == null) {
                        onConfirm()
                    }
                },
                colors = colors.toTextFieldColors()
            )

            val calories = getCalories(Measurement.Gram(formField.value)).roundToInt()
            val kcal = stringResource(Res.string.unit_kcal)
            Text(
                text = "$calories $kcal",
                overflow = TextOverflow.Visible,
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(2f)
            )

            FilledIconButton(
                onClick = onConfirm,
                enabled = formField.error == null
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Default.KeyboardArrowRight,
                    contentDescription = stringResource(Res.string.action_add)
                )
            }
        }
    }
}

@Immutable
internal data class MeasurementInputColors(val containerColor: Color, val contentColor: Color) {
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
}

internal object MeasurementInputDefaults {
    @Composable
    fun colors(
        containerColor: Color = MaterialTheme.colorScheme.surfaceContainer,
        contentColor: Color = MaterialTheme.colorScheme.onSurface
    ): MeasurementInputColors = MeasurementInputColors(
        containerColor = containerColor,
        contentColor = contentColor
    )
}

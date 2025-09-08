package com.maksimowiczm.foodyou.feature.food.shared.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.maksimowiczm.foodyou.feature.shared.ui.Saver
import com.maksimowiczm.foodyou.feature.shared.ui.stringResource
import com.maksimowiczm.foodyou.shared.domain.measurement.Measurement
import com.maksimowiczm.foodyou.shared.domain.measurement.MeasurementType
import com.maksimowiczm.foodyou.shared.domain.measurement.from
import com.maksimowiczm.foodyou.shared.domain.measurement.rawValue
import com.maksimowiczm.foodyou.shared.domain.measurement.type
import com.maksimowiczm.foodyou.shared.ui.form.FormField
import com.maksimowiczm.foodyou.shared.ui.form.nullableFloatParser
import com.maksimowiczm.foodyou.shared.ui.form.positiveFloatValidator
import com.maksimowiczm.foodyou.shared.ui.form.rememberFormField
import com.maksimowiczm.foodyou.shared.ui.res.formatClipZeros
import foodyou.app.generated.resources.*
import org.jetbrains.compose.resources.painterResource

@Composable
fun MeasurementPicker(state: MeasurementPickerState, modifier: Modifier = Modifier) {
    val latestState by rememberUpdatedState(state)
    LaunchedEffect(state.inputField.value, state.type) {
        val value = state.inputField.value ?: return@LaunchedEffect
        val measurement = Measurement.from(state.type, value.toDouble())
        latestState.measurement = measurement
    }

    Column(modifier) {
        Row {
            Box(modifier = Modifier.size(48.dp), contentAlignment = Alignment.Center) {
                Icon(painter = painterResource(Res.drawable.ic_weight), contentDescription = null)
            }

            Spacer(Modifier.width(8.dp))

            Input(
                formField = state.inputField,
                type = state.type,
                types = state.possibleTypes,
                onSelect = { state.type = it },
                modifier = Modifier.weight(1f).padding(end = 8.dp),
            )
        }

        Spacer(Modifier.height(8.dp))

        FlowRow(
            modifier = Modifier.padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            state.suggestions.forEach { measurement ->
                SuggestionChip(
                    onClick = {
                        state.inputField.textFieldState.setTextAndPlaceCursorAtEnd(
                            text = measurement.rawValue.formatClipZeros()
                        )
                        state.type = measurement.type
                    },
                    label = { Text(measurement.stringResource()) },
                )
            }
        }
    }
}

@Composable
private fun Input(
    formField: FormField<Float?, String>,
    type: MeasurementType,
    types: List<MeasurementType>,
    onSelect: (MeasurementType) -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanded by rememberSaveable { mutableStateOf(false) }

    val inputColor by
        animateColorAsState(
            targetValue =
                if (formField.error == null) {
                    MaterialTheme.colorScheme.primaryContainer
                } else {
                    MaterialTheme.colorScheme.errorContainer
                }
        )
    val contentColor by
        animateColorAsState(
            targetValue =
                if (formField.error == null) {
                    MaterialTheme.colorScheme.onPrimaryContainer
                } else {
                    MaterialTheme.colorScheme.onErrorContainer
                }
        )

    Row(modifier = modifier, horizontalArrangement = Arrangement.spacedBy(2.dp)) {
        Surface(
            modifier = Modifier.height(48.dp).width(120.dp),
            color = inputColor,
            contentColor = contentColor,
            shape =
                RoundedCornerShape(
                    topStart = 16.dp,
                    bottomStart = 16.dp,
                    topEnd = 4.dp,
                    bottomEnd = 4.dp,
                ),
        ) {
            BasicTextField(
                state = formField.textFieldState,
                modifier = Modifier.height(48.dp).padding(horizontal = 16.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                textStyle = LocalTextStyle.current.merge(LocalContentColor.current),
                lineLimits = TextFieldLineLimits.SingleLine,
                cursorBrush = SolidColor(LocalContentColor.current),
                decorator = { Box(contentAlignment = Alignment.CenterStart) { it() } },
            )
        }

        Surface(
            onClick = { expanded = true },
            modifier = Modifier.heightIn(min = 48.dp).weight(1f),
            color = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            shape =
                RoundedCornerShape(
                    topStart = 4.dp,
                    bottomStart = 4.dp,
                    topEnd = 16.dp,
                    bottomEnd = 16.dp,
                ),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = type.stringResource(),
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                )
                Box(modifier = Modifier.size(48.dp), contentAlignment = Alignment.Center) {
                    Icon(imageVector = Icons.Outlined.KeyboardArrowDown, contentDescription = null)

                    DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                        types.forEach {
                            DropdownMenuItem(
                                text = { Text(it.stringResource()) },
                                onClick = {
                                    onSelect(it)
                                    expanded = false
                                },
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun rememberMeasurementPickerState(
    suggestions: List<Measurement>,
    possibleTypes: List<MeasurementType>,
    selectedMeasurement: Measurement,
): MeasurementPickerState {
    val inputField =
        rememberFormField(
            initialValue = selectedMeasurement.rawValue.toFloat(),
            parser = nullableFloatParser(onNotANumber = { "Invalid number format" }),
            validator =
                positiveFloatValidator(
                    onNotPositive = { "Value must be positive" },
                    onNull = { "Value cannot be empty" },
                ),
            textFieldState = rememberTextFieldState(selectedMeasurement.rawValue.formatClipZeros()),
            validateFirst = true,
        )
    val typeState = rememberSaveable { mutableStateOf(selectedMeasurement.type) }
    val measurementState =
        rememberSaveable(selectedMeasurement, stateSaver = Measurement.Saver) {
            mutableStateOf(selectedMeasurement)
        }

    return remember(suggestions, possibleTypes, inputField, typeState, measurementState) {
        MeasurementPickerState(
            suggestions = suggestions,
            possibleTypes = possibleTypes,
            inputField = inputField,
            measurementState = measurementState,
            typeState = typeState,
        )
    }
}

class MeasurementPickerState(
    val suggestions: List<Measurement>,
    val possibleTypes: List<MeasurementType>,
    val inputField: FormField<Float?, String>,
    measurementState: MutableState<Measurement>,
    typeState: MutableState<MeasurementType>,
) {
    var measurement by measurementState
    var type by typeState
}

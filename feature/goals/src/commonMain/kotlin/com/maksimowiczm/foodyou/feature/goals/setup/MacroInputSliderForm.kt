package com.maksimowiczm.foodyou.feature.goals.setup

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.maksimowiczm.foodyou.business.settings.domain.NutrientsOrder
import com.maksimowiczm.foodyou.feature.shared.ui.LocalNutrientsOrder
import com.maksimowiczm.foodyou.shared.ui.form.FormField
import com.maksimowiczm.foodyou.shared.ui.form.intParser
import com.maksimowiczm.foodyou.shared.ui.form.nonNegativeIntValidator
import com.maksimowiczm.foodyou.shared.ui.form.rememberFormField
import com.maksimowiczm.foodyou.shared.ui.theme.LocalNutrientsPalette
import foodyou.app.generated.resources.*
import kotlin.math.roundToInt
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun MacroInputSliderForm(state: MacroInputSliderFormState, modifier: Modifier = Modifier) {
    val nutrientsPalette = LocalNutrientsPalette.current
    val nutrientsOrder = LocalNutrientsOrder.current

    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(16.dp)) {
        OutlinedTextField(
            state = state.energy.textFieldState,
            modifier = Modifier.fillMaxWidth(),
            label = { Text(stringResource(Res.string.unit_energy)) },
            suffix = { Text(stringResource(Res.string.unit_kcal)) },
            isError = state.energy.error != null,
            keyboardOptions =
                KeyboardOptions(keyboardType = KeyboardType.Decimal, imeAction = ImeAction.Next),
        )

        nutrientsOrder.forEach {
            when (it) {
                NutrientsOrder.Proteins ->
                    MacroSlider(
                        value = state.proteins,
                        onValueChange = { state.proteins = it },
                        color = nutrientsPalette.proteinsOnSurfaceContainer,
                        label = stringResource(Res.string.nutriment_proteins),
                    )

                NutrientsOrder.Fats ->
                    MacroSlider(
                        value = state.fats,
                        onValueChange = { state.fats = it },
                        color = nutrientsPalette.fatsOnSurfaceContainer,
                        label = stringResource(Res.string.nutriment_fats),
                    )

                NutrientsOrder.Carbohydrates ->
                    MacroSlider(
                        value = state.carbohydrates,
                        onValueChange = { state.carbohydrates = it },
                        color = nutrientsPalette.carbohydratesOnSurfaceContainer,
                        label = stringResource(Res.string.nutriment_carbohydrates),
                    )

                NutrientsOrder.Other,
                NutrientsOrder.Vitamins,
                NutrientsOrder.Minerals -> Unit
            }
        }
    }
}

@Composable
private fun MacroSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    color: Color,
    label: String,
    modifier: Modifier = Modifier,
) {
    Column(modifier) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(text = label, color = color, style = MaterialTheme.typography.bodyMedium)
            Text(
                text =
                    buildString {
                        append(value.roundToInt())
                        append("%")
                    },
                color = color,
                style = MaterialTheme.typography.bodyMedium,
            )
        }
        Slider(
            value = value,
            onValueChange = { onValueChange(it.roundToInt().toFloat()) },
            valueRange = 0f..100f,
            colors = SliderDefaults.colors(activeTrackColor = color, thumbColor = color),
        )
    }
}

@Composable
internal fun rememberMacroInputSliderFormState(
    proteins: Float,
    carbohydrates: Float,
    fats: Float,
    energy: Int,
): MacroInputSliderFormState {
    val proteinsState = rememberSaveable(proteins) { mutableFloatStateOf(proteins) }
    val carbohydratesState = rememberSaveable(carbohydrates) { mutableFloatStateOf(carbohydrates) }
    val fatsState = rememberSaveable(fats) { mutableFloatStateOf(fats) }
    val energyField =
        rememberFormField(
            initialValue = energy,
            parser =
                intParser(
                    onNotANumber = { DailyGoalsFormFieldError.NotANumber },
                    onBlank = { DailyGoalsFormFieldError.Required },
                ),
            validator = nonNegativeIntValidator(onNegative = { DailyGoalsFormFieldError.Negative }),
            textFieldState = rememberTextFieldState(energy.toString()),
        )
    LaunchedEffect(energy) {
        energyField.textFieldState.setTextAndPlaceCursorAtEnd(energy.toString())
    }

    val adjustMacros: (MutableState<Float>, List<MutableState<Float>>) -> Unit = remember {
        { changedState, otherStates ->
            val total = proteinsState.value + carbohydratesState.value + fatsState.value
            val excess = total - 100f

            if (excess == 0f) return@remember

            if (excess > 0) {
                // Reduce other macros when total exceeds 100% (smaller values first)
                var remaining = excess
                otherStates
                    .sortedBy { it.value }
                    .forEach { state ->
                        if (remaining > 0 && state.value > 0) {
                            val reduction = minOf(remaining, state.value)
                            state.value -= reduction
                            remaining -= reduction
                        }
                    }

                // If we still have excess, reduce the changed state
                if (remaining > 0) {
                    changedState.value = (changedState.value - remaining).coerceAtLeast(0f)
                }
            } else {
                // Increase other macros when total is under 100% (smaller values first)
                var remaining = -excess
                otherStates
                    .sortedBy { it.value }
                    .forEach { state ->
                        if (remaining > 0 && state.value < 100f) {
                            val increase = minOf(remaining, 100f - state.value)
                            state.value += increase
                            remaining -= increase
                        }
                    }

                // If we still have deficit, increase the changed state
                if (remaining > 0) {
                    changedState.value = (changedState.value + remaining).coerceAtMost(100f)
                }
            }
        }
    }

    LaunchedEffect(proteinsState.value) {
        adjustMacros(proteinsState, listOf(carbohydratesState, fatsState))
    }

    LaunchedEffect(carbohydratesState.value) {
        adjustMacros(carbohydratesState, listOf(proteinsState, fatsState))
    }

    LaunchedEffect(fatsState.value) {
        adjustMacros(fatsState, listOf(proteinsState, carbohydratesState))
    }

    val isModifiedState = remember {
        derivedStateOf {
            proteinsState.value != proteins ||
                carbohydratesState.value != carbohydrates ||
                fatsState.value != fats ||
                energyField.value != energy
        }
    }

    return remember(proteinsState, carbohydratesState, fatsState, energyField, isModifiedState) {
        MacroInputSliderFormState(
            proteinsState = proteinsState,
            carbohydratesState = carbohydratesState,
            fatsState = fatsState,
            energy = energyField,
            isModifiedState = isModifiedState,
        )
    }
}

@Stable
internal class MacroInputSliderFormState(
    proteinsState: MutableState<Float>,
    carbohydratesState: MutableState<Float>,
    fatsState: MutableState<Float>,
    val energy: FormField<Int, DailyGoalsFormFieldError>,
    isModifiedState: State<Boolean>,
) {
    var proteins: Float by proteinsState
    var carbohydrates: Float by carbohydratesState
    var fats: Float by fatsState

    val isModified: Boolean by isModifiedState

    val isValid by derivedStateOf { energy.error == null }
}

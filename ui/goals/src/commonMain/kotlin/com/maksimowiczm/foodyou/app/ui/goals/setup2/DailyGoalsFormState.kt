package com.maksimowiczm.foodyou.app.ui.goals.setup2

import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import com.maksimowiczm.foodyou.app.ui.goals.setup.AdditionalGoalsFormState
import com.maksimowiczm.foodyou.app.ui.goals.setup.rememberAdditionalGoalsFormState
import com.maksimowiczm.foodyou.goals.domain.entity.DailyGoal
import com.maksimowiczm.foodyou.goals.domain.entity.MacronutrientGoal
import com.maksimowiczm.foodyou.shared.compose.form.FormField
import com.maksimowiczm.foodyou.shared.compose.form.doubleParser
import com.maksimowiczm.foodyou.shared.compose.form.rememberFormField
import com.maksimowiczm.foodyou.shared.compose.utility.formatClipZeros
import com.maksimowiczm.foodyou.shared.domain.food.NutrientsHelper
import kotlin.math.abs
import kotlin.math.roundToInt
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine

internal enum class DailyGoalsFormError {
    Empty,
    NotANumber,
    Negative,
}

internal enum class InputType {
    Weight,
    Percentage,
}

internal class DailyGoalsFormState(
    val energy: FormField<Double, DailyGoalsFormError>,
    val proteins: FormField<Double, DailyGoalsFormError>,
    proteinsSliderState: MutableState<Float>,
    val fats: FormField<Double, DailyGoalsFormError>,
    fatsSliderState: MutableState<Float>,
    val carbs: FormField<Double, DailyGoalsFormError>,
    carbsSliderState: MutableState<Float>,
    isModifiedState: State<Boolean>,
    inputTypeState: MutableState<InputType>,
    autoCalculateEnergyState: MutableState<Boolean>,
    val additionalState: AdditionalGoalsFormState,
) {
    val isValid: Boolean by derivedStateOf {
        energy.error == null &&
            proteins.error == null &&
            fats.error == null &&
            carbs.error == null &&
            additionalState.isValid
    }

    val isModified: Boolean by derivedStateOf {
        isModifiedState.value || additionalState.isModified
    }

    var proteinsSlider by proteinsSliderState
    var fatsSlider by fatsSliderState
    var carbsSlider by carbsSliderState

    var autoCalculateEnergy by autoCalculateEnergyState
    var inputType by inputTypeState
}

@Composable
internal fun rememberDailyGoalsFormState(dailyGoal: DailyGoal? = null): DailyGoalsFormState {
    val dailyGoal = dailyGoal ?: DailyGoal.defaultGoals

    val energyFormField =
        rememberFormField(
            initialValue = dailyGoal.macronutrientGoal.energyKcal,
            parser =
                doubleParser(
                    onBlank = { DailyGoalsFormError.Empty },
                    onNotANumber = { DailyGoalsFormError.NotANumber },
                ),
            validator = { if (it < 0) DailyGoalsFormError.Negative else null },
            textFieldState =
                rememberTextFieldState(dailyGoal.macronutrientGoal.energyKcal.formatClipZeros()),
        )

    val proteinsFormField =
        rememberFormField(
            initialValue = dailyGoal.macronutrientGoal.proteinsGrams,
            parser =
                doubleParser(
                    onBlank = { DailyGoalsFormError.Empty },
                    onNotANumber = { DailyGoalsFormError.NotANumber },
                ),
            validator = { if (it < 0) DailyGoalsFormError.Negative else null },
            textFieldState =
                rememberTextFieldState(dailyGoal.macronutrientGoal.proteinsGrams.formatClipZeros()),
        )

    val proteinsSlider = rememberSaveable {
        val sliderValue =
            (dailyGoal.macronutrientGoal as? MacronutrientGoal.Distribution)
                ?.proteinsPercentage
                ?.times(100)
                ?.toFloat()
        mutableFloatStateOf(sliderValue ?: 0f)
    }

    val fatsFormField =
        rememberFormField(
            initialValue = dailyGoal.macronutrientGoal.fatsGrams,
            parser =
                doubleParser(
                    onBlank = { DailyGoalsFormError.Empty },
                    onNotANumber = { DailyGoalsFormError.NotANumber },
                ),
            validator = { if (it < 0) DailyGoalsFormError.Negative else null },
            textFieldState =
                rememberTextFieldState(dailyGoal.macronutrientGoal.fatsGrams.formatClipZeros()),
        )

    val fatsSlider = rememberSaveable {
        val sliderValue =
            (dailyGoal.macronutrientGoal as? MacronutrientGoal.Distribution)
                ?.fatsPercentage
                ?.times(100)
                ?.toFloat()
        mutableFloatStateOf(sliderValue ?: 0f)
    }

    val carbsFormField =
        rememberFormField(
            initialValue = dailyGoal.macronutrientGoal.carbohydratesGrams,
            parser =
                doubleParser(
                    onBlank = { DailyGoalsFormError.Empty },
                    onNotANumber = { DailyGoalsFormError.NotANumber },
                ),
            validator = { if (it < 0) DailyGoalsFormError.Negative else null },
            textFieldState =
                rememberTextFieldState(
                    dailyGoal.macronutrientGoal.carbohydratesGrams.formatClipZeros()
                ),
        )

    val carbsSlider = rememberSaveable {
        val sliderValue =
            (dailyGoal.macronutrientGoal as? MacronutrientGoal.Distribution)
                ?.carbohydratesPercentage
                ?.times(100)
                ?.toFloat()
        mutableFloatStateOf(sliderValue ?: 0f)
    }

    val inputType = rememberSaveable {
        mutableStateOf(
            when (dailyGoal.macronutrientGoal) {
                is MacronutrientGoal.Distribution -> InputType.Percentage
                else -> InputType.Weight
            }
        )
    }

    val isModifiedState = remember {
        derivedStateOf {
            if (!energyFormField.value.isCloseTo(dailyGoal.macronutrientGoal.energyKcal)) {
                return@derivedStateOf true
            }

            if (
                inputType.value == InputType.Weight &&
                    dailyGoal.macronutrientGoal is MacronutrientGoal.Distribution
            ) {
                return@derivedStateOf true
            }

            if (
                inputType.value == InputType.Percentage &&
                    dailyGoal.macronutrientGoal is MacronutrientGoal.Manual
            ) {
                return@derivedStateOf true
            }

            !proteinsFormField.value.isCloseTo(dailyGoal.macronutrientGoal.proteinsGrams) &&
                !fatsFormField.value.isCloseTo(dailyGoal.macronutrientGoal.fatsGrams) &&
                !carbsFormField.value.isCloseTo(dailyGoal.macronutrientGoal.carbohydratesGrams)
        }
    }

    val autoCalculateEnergyState = rememberSaveable {
        val currentEnergy =
            NutrientsHelper.calculateEnergy(
                    proteins = dailyGoal.macronutrientGoal.proteinsGrams,
                    carbohydrates = dailyGoal.macronutrientGoal.carbohydratesGrams,
                    fats = dailyGoal.macronutrientGoal.fatsGrams,
                )
                .roundToInt()

        // Allow 2% error
        val allowedError = (currentEnergy * 0.02).toInt()
        val shouldAutoCalculate =
            dailyGoal.macronutrientGoal.energyKcal.roundToInt() in
                (currentEnergy - allowedError)..(currentEnergy + allowedError)

        mutableStateOf(shouldAutoCalculate)
    }

    LaunchedEffect(Unit) {
        combine(
                snapshotFlow { inputType.value },
                snapshotFlow { autoCalculateEnergyState.value },
                snapshotFlow {
                    arrayOf(
                        energyFormField.value,
                        proteinsFormField.value,
                        fatsFormField.value,
                        carbsFormField.value,
                    )
                },
            ) { inputType, autoCalculateEnergy, (energy, proteins, fats, carbs) ->
                if (inputType != InputType.Weight) {
                    return@combine
                }

                if (autoCalculateEnergy) {
                    val energyKcal =
                        NutrientsHelper.calculateEnergy(
                            proteins = proteins,
                            carbohydrates = carbs,
                            fats = fats,
                        )
                    energyFormField.textFieldState.setTextAndPlaceCursorAtEnd(
                        energyKcal.roundToInt().toString()
                    )
                }

                // Update sliders
                proteinsSlider.value =
                    NutrientsHelper.proteinsPercentage(energy.roundToInt(), proteins) * 100
                fatsSlider.value = NutrientsHelper.fatsPercentage(energy.roundToInt(), fats) * 100
                carbsSlider.value =
                    NutrientsHelper.carbohydratesPercentage(energy.roundToInt(), carbs) * 100
            }
            .collectLatest {}
    }

    LaunchedEffect(Unit) {
        combine(
                snapshotFlow { inputType.value },
                snapshotFlow { energyFormField.value },
                snapshotFlow { arrayOf(proteinsSlider.value, fatsSlider.value, carbsSlider.value) },
            ) { inputType, energy, (proteins, fats, carbs) ->
                if (inputType != InputType.Percentage) {
                    return@combine
                }

                val proteinsGrams =
                    NutrientsHelper.proteinsPercentageToGrams(
                        energy.roundToInt(),
                        proteins.toDouble() / 100,
                    )

                if (!proteinsFormField.value.isCloseTo(proteinsGrams)) {
                    proteinsFormField.textFieldState.setTextAndPlaceCursorAtEnd(
                        proteinsGrams.formatClipZeros()
                    )
                }

                val fatsGrams =
                    NutrientsHelper.fatsPercentageToGrams(
                        energy.roundToInt(),
                        fats.toDouble() / 100,
                    )
                if (!fatsFormField.value.isCloseTo(fatsGrams)) {
                    fatsFormField.textFieldState.setTextAndPlaceCursorAtEnd(
                        fatsGrams.formatClipZeros()
                    )
                }

                val carbsGrams =
                    NutrientsHelper.carbohydratesPercentageToGrams(
                        energy.roundToInt(),
                        carbs.toDouble() / 100,
                    )
                if (!carbsFormField.value.isCloseTo(carbsGrams)) {
                    carbsFormField.textFieldState.setTextAndPlaceCursorAtEnd(
                        carbsGrams.formatClipZeros()
                    )
                }
            }
            .collectLatest {}
    }

    val adjustMacros: (MutableState<Float>, List<MutableState<Float>>) -> Unit = remember {
        { changedState, otherStates ->
            val total = proteinsSlider.value + carbsSlider.value + fatsSlider.value
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

    LaunchedEffect(proteinsSlider.value) {
        adjustMacros(proteinsSlider, listOf(carbsSlider, fatsSlider))
    }
    LaunchedEffect(carbsSlider.value) {
        adjustMacros(carbsSlider, listOf(proteinsSlider, fatsSlider))
    }
    LaunchedEffect(fatsSlider.value) {
        adjustMacros(fatsSlider, listOf(proteinsSlider, carbsSlider))
    }

    val additionalState = rememberAdditionalGoalsFormState(dailyGoal)

    return remember(
        energyFormField,
        proteinsFormField,
        proteinsSlider,
        fatsFormField,
        fatsSlider,
        carbsFormField,
        carbsSlider,
        isModifiedState,
        inputType,
        autoCalculateEnergyState,
        additionalState,
    ) {
        DailyGoalsFormState(
            energy = energyFormField,
            proteins = proteinsFormField,
            proteinsSliderState = proteinsSlider,
            fats = fatsFormField,
            fatsSliderState = fatsSlider,
            carbs = carbsFormField,
            carbsSliderState = carbsSlider,
            isModifiedState = isModifiedState,
            inputTypeState = inputType,
            autoCalculateEnergyState = autoCalculateEnergyState,
            additionalState = additionalState,
        )
    }
}

private fun Double.isCloseTo(other: Double, epsilon: Double = 1e-2): Boolean =
    abs(this - other) < epsilon

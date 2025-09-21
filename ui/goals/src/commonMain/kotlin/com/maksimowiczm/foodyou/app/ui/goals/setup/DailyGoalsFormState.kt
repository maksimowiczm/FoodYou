package com.maksimowiczm.foodyou.app.ui.goals.setup

import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import com.maksimowiczm.foodyou.goals.domain.entity.DailyGoal
import com.maksimowiczm.foodyou.goals.domain.entity.MacronutrientGoal
import com.maksimowiczm.foodyou.shared.compose.form.FormField
import com.maksimowiczm.foodyou.shared.compose.form.doubleParser
import com.maksimowiczm.foodyou.shared.compose.form.rememberFormField
import com.maksimowiczm.foodyou.shared.compose.utility.formatClipZeros
import com.maksimowiczm.foodyou.shared.domain.food.NutrientsHelper
import com.maksimowiczm.foodyou.shared.domain.food.NutritionFactsField
import kotlin.math.abs
import kotlin.math.roundToInt
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine

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

    var inputType by inputTypeState

    fun intoDailyGoals(): DailyGoal {
        val macronutrientGoal =
            when (inputType) {
                InputType.Weight ->
                    MacronutrientGoal.Manual(
                        energyKcal = energy.value,
                        proteinsGrams = proteins.value,
                        fatsGrams = fats.value,
                        carbohydratesGrams = carbs.value,
                    )

                InputType.Percentage ->
                    MacronutrientGoal.Distribution(
                        energyKcal = energy.value,
                        proteinsPercentage = proteinsSlider.toDouble() / 100,
                        fatsPercentage = fatsSlider.toDouble() / 100,
                        carbohydratesPercentage = carbsSlider.toDouble() / 100,
                    )
            }

        val map =
            NutritionFactsField.entries
                .associateWith {
                    when (it) {
                        NutritionFactsField.Energy -> null
                        NutritionFactsField.Proteins -> null
                        NutritionFactsField.Fats -> null
                        NutritionFactsField.SaturatedFats -> additionalState.saturatedFats.value
                        NutritionFactsField.TransFats -> additionalState.transFats.value
                        NutritionFactsField.MonounsaturatedFats ->
                            additionalState.monounsaturatedFats.value

                        NutritionFactsField.PolyunsaturatedFats ->
                            additionalState.polyunsaturatedFats.value

                        NutritionFactsField.Omega3 -> additionalState.omega3.value
                        NutritionFactsField.Omega6 -> additionalState.omega6.value
                        NutritionFactsField.Carbohydrates -> null
                        NutritionFactsField.Sugars -> additionalState.sugars.value
                        NutritionFactsField.AddedSugars -> additionalState.addedSugars.value
                        NutritionFactsField.DietaryFiber -> additionalState.dietaryFiber.value
                        NutritionFactsField.SolubleFiber -> additionalState.solubleFiber.value
                        NutritionFactsField.InsolubleFiber -> additionalState.insolubleFiber.value
                        NutritionFactsField.Salt -> additionalState.salt.value
                        NutritionFactsField.Cholesterol ->
                            additionalState.cholesterolMilli.value / 1000

                        NutritionFactsField.Caffeine -> additionalState.caffeineMilli.value / 1000
                        NutritionFactsField.VitaminA ->
                            additionalState.vitaminAMicro.value / 1000_000

                        NutritionFactsField.VitaminB1 -> additionalState.vitaminB1Milli.value / 1000
                        NutritionFactsField.VitaminB2 -> additionalState.vitaminB2Milli.value / 1000
                        NutritionFactsField.VitaminB3 -> additionalState.vitaminB3Milli.value / 1000
                        NutritionFactsField.VitaminB5 -> additionalState.vitaminB5Milli.value / 1000
                        NutritionFactsField.VitaminB6 -> additionalState.vitaminB6Milli.value / 1000
                        NutritionFactsField.VitaminB7 ->
                            additionalState.vitaminB7Micro.value / 1000_000

                        NutritionFactsField.VitaminB9 ->
                            additionalState.vitaminB9Micro.value / 1000_000

                        NutritionFactsField.VitaminB12 ->
                            additionalState.vitaminB12Micro.value / 1000_000

                        NutritionFactsField.VitaminC -> additionalState.vitaminCMilli.value / 1000
                        NutritionFactsField.VitaminD ->
                            additionalState.vitaminDMicro.value / 1000_000

                        NutritionFactsField.VitaminE -> additionalState.vitaminEMilli.value / 1000
                        NutritionFactsField.VitaminK ->
                            additionalState.vitaminKMicro.value / 1000_000

                        NutritionFactsField.Manganese -> additionalState.manganeseMilli.value / 1000
                        NutritionFactsField.Magnesium -> additionalState.magnesiumMilli.value / 1000
                        NutritionFactsField.Potassium -> additionalState.potassiumMilli.value / 1000
                        NutritionFactsField.Calcium -> additionalState.calciumMilli.value / 1000
                        NutritionFactsField.Copper -> additionalState.copperMilli.value / 1000
                        NutritionFactsField.Zinc -> additionalState.zincMilli.value / 1000
                        NutritionFactsField.Sodium -> additionalState.sodiumMilli.value / 1000
                        NutritionFactsField.Iron -> additionalState.ironMilli.value / 1000
                        NutritionFactsField.Phosphorus ->
                            additionalState.phosphorusMilli.value / 1000

                        NutritionFactsField.Selenium ->
                            additionalState.seleniumMicro.value / 1000_000

                        NutritionFactsField.Iodine -> additionalState.iodineMicro.value / 1000_000
                        NutritionFactsField.Chromium ->
                            additionalState.chromiumMicro.value / 1000_000
                    }
                }
                .filterValues { it != null }
                .mapValues { it.value!! }

        return DailyGoal(macronutrientGoal = macronutrientGoal, map = map)
    }
}

@Composable
internal fun rememberDailyGoalsFormState(dailyGoal: DailyGoal? = null): DailyGoalsFormState {
    val dailyGoal = dailyGoal ?: DailyGoal.defaultGoals

    val energyFormField =
        rememberFormField(
            initialValue = dailyGoal.macronutrientGoal.energyKcal,
            parser =
                doubleParser(
                    onBlank = { DailyGoalsFormError.Required },
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
                    onBlank = { DailyGoalsFormError.Required },
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
                    onBlank = { DailyGoalsFormError.Required },
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
                    onBlank = { DailyGoalsFormError.Required },
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

    LaunchedEffect(Unit) {
        combine(
                snapshotFlow { inputType.value },
                snapshotFlow {
                    arrayOf(
                        energyFormField.value,
                        proteinsFormField.value,
                        fatsFormField.value,
                        carbsFormField.value,
                    )
                },
            ) { inputType, (energy, proteins, fats, carbs) ->
                if (inputType != InputType.Weight) {
                    return@combine
                }

                val energyKcal =
                    NutrientsHelper.calculateEnergy(
                        proteins = proteins,
                        carbohydrates = carbs,
                        fats = fats,
                    )
                energyFormField.textFieldState.setTextAndPlaceCursorAtEnd(
                    energyKcal.roundToInt().toString()
                )

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
            additionalState = additionalState,
        )
    }
}

private fun Double.isCloseTo(other: Double, epsilon: Double = 1e-2): Boolean =
    abs(this - other) < epsilon

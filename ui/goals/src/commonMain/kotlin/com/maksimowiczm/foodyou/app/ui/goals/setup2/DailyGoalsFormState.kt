package com.maksimowiczm.foodyou.app.ui.goals.setup2

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
import kotlin.math.roundToInt
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filter

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
) {
    val isValid: Boolean by derivedStateOf {
        energy.error == null && proteins.error == null && fats.error == null && carbs.error == null
    }

    val isModified: Boolean by isModifiedState

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

    val proteinsSlider = rememberSaveable { mutableFloatStateOf(0f) }

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

    val fatsSlider = rememberSaveable { mutableFloatStateOf(0f) }

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

    val carbsSlider = rememberSaveable { mutableFloatStateOf(0f) }

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
            if (energyFormField.value != dailyGoal.macronutrientGoal.energyKcal) {
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

            proteinsFormField.value.toInt() != dailyGoal.macronutrientGoal.proteinsGrams.toInt() ||
                fatsFormField.value.toInt() != dailyGoal.macronutrientGoal.fatsGrams.toInt() ||
                carbsFormField.value.toInt() !=
                    dailyGoal.macronutrientGoal.carbohydratesGrams.toInt()
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
                snapshotFlow { inputType.value }.filter { it == InputType.Weight },
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
            }
            .collectLatest {}
    }

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
        )
    }
}

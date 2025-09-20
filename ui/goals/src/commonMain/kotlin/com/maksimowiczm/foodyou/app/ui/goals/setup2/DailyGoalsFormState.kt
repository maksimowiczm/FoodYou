package com.maksimowiczm.foodyou.app.ui.goals.setup2

import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.maksimowiczm.foodyou.goals.domain.entity.DailyGoal
import com.maksimowiczm.foodyou.goals.domain.entity.MacronutrientGoal
import com.maksimowiczm.foodyou.shared.compose.form.FormField
import com.maksimowiczm.foodyou.shared.compose.form.doubleParser
import com.maksimowiczm.foodyou.shared.compose.form.rememberFormField
import com.maksimowiczm.foodyou.shared.compose.utility.formatClipZeros

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

    val energy =
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

    val proteins =
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

    val fats =
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

    val carbs =
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
            if (energy.value != dailyGoal.macronutrientGoal.energyKcal) {
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

            proteins.value.toInt() != dailyGoal.macronutrientGoal.proteinsGrams.toInt() ||
                fats.value.toInt() != dailyGoal.macronutrientGoal.fatsGrams.toInt() ||
                carbs.value.toInt() != dailyGoal.macronutrientGoal.carbohydratesGrams.toInt()
        }
    }

    val autoCalculateEnergyState = rememberSaveable { mutableStateOf(true) }

    return remember(
        energy,
        proteins,
        proteinsSlider,
        fats,
        fatsSlider,
        carbs,
        carbsSlider,
        isModifiedState,
        inputType,
        autoCalculateEnergyState,
    ) {
        DailyGoalsFormState(
            energy = energy,
            proteins = proteins,
            proteinsSliderState = proteinsSlider,
            fats = fats,
            fatsSliderState = fatsSlider,
            carbs = carbs,
            carbsSliderState = carbsSlider,
            isModifiedState = isModifiedState,
            inputTypeState = inputType,
            autoCalculateEnergyState = autoCalculateEnergyState,
        )
    }
}

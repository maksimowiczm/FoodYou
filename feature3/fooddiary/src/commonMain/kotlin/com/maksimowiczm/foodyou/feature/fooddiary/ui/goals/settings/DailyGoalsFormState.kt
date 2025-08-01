package com.maksimowiczm.foodyou.feature.fooddiary.ui.goals.settings

import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import com.maksimowiczm.foodyou.core.ui.form.FormField
import com.maksimowiczm.foodyou.core.ui.form.floatParser
import com.maksimowiczm.foodyou.core.ui.form.rememberFormField
import com.maksimowiczm.foodyou.core.ui.res.formatClipZeros
import com.maksimowiczm.foodyou.core.util.NutrientsHelper
import com.maksimowiczm.foodyou.feature.food.domain.NutritionFactsField
import com.maksimowiczm.foodyou.feature.fooddiary.domain.DailyGoal
import kotlin.math.roundToInt

internal enum class DailyGoalsFormFieldError {
    Required,
    NotANumber
}

@Stable
internal class DailyGoalsFormState(
    useDistributionState: MutableState<Boolean>,
    val proteins: FormField<Float, DailyGoalsFormFieldError>,
    proteinsSliderState: MutableState<Float>,
    val carbohydrates: FormField<Float, DailyGoalsFormFieldError>,
    carbohydratesSliderState: MutableState<Float>,
    val fats: FormField<Float, DailyGoalsFormFieldError>,
    fatsSliderState: MutableState<Float>,
    autoCalculateEnergyState: MutableState<Boolean>,
    val energy: FormField<Float, DailyGoalsFormFieldError>
) {
    var useDistribution by useDistributionState
    var autoCalculateEnergy by autoCalculateEnergyState

    var proteinsSlider by proteinsSliderState
    var carbohydratesSlider by carbohydratesSliderState
    var fatsSlider by fatsSliderState

    val badEnergy by derivedStateOf {
        val kcal = NutrientsHelper.calculateEnergy(
            proteins = proteins.value,
            carbohydrates = carbohydrates.value,
            fats = fats.value
        ).roundToInt()

        // Allow 2% error
        val allowedError = (kcal * 0.02).toInt()
        val energyValue = energy.value.roundToInt()
        energyValue < (kcal - allowedError) || energyValue > (kcal + allowedError)
    }

    fun autoSetEnergy() {
        val kcal = NutrientsHelper.calculateEnergy(
            proteins = proteins.value,
            carbohydrates = carbohydrates.value,
            fats = fats.value
        ).roundToInt()

        energy.textFieldState.setTextAndPlaceCursorAtEnd(kcal.toString())
    }
}

@Composable
internal fun rememberDailyGoalsFormState(dailyGoals: DailyGoal): DailyGoalsFormState {
    val useDistributionState = rememberSaveable {
        mutableStateOf(dailyGoals.isDistribution)
    }

    val proteins = rememberFormField(
        initialValue = dailyGoals[NutritionFactsField.Proteins].toFloat(),
        parser = floatParser(
            onNotANumber = { DailyGoalsFormFieldError.NotANumber },
            onBlank = { DailyGoalsFormFieldError.NotANumber }
        ),
        textFieldState = rememberTextFieldState(
            dailyGoals[NutritionFactsField.Proteins].formatClipZeros()
        )
    )

    val carbohydrates = rememberFormField(
        initialValue = dailyGoals[NutritionFactsField.Carbohydrates].toFloat(),
        parser = floatParser(
            onNotANumber = { DailyGoalsFormFieldError.NotANumber },
            onBlank = { DailyGoalsFormFieldError.NotANumber }
        ),
        textFieldState = rememberTextFieldState(
            dailyGoals[NutritionFactsField.Carbohydrates].formatClipZeros()
        )
    )

    val fats = rememberFormField(
        initialValue = dailyGoals[NutritionFactsField.Fats].toFloat(),
        parser = floatParser(
            onNotANumber = { DailyGoalsFormFieldError.NotANumber },
            onBlank = { DailyGoalsFormFieldError.NotANumber }
        ),
        textFieldState = rememberTextFieldState(
            dailyGoals[NutritionFactsField.Fats].formatClipZeros()
        )
    )

    val autoCalculateEnergyState = rememberSaveable {
        mutableStateOf(true)
    }

    val energy = rememberFormField(
        initialValue = dailyGoals[NutritionFactsField.Energy].toFloat(),
        parser = floatParser(
            onNotANumber = { DailyGoalsFormFieldError.NotANumber },
            onBlank = { DailyGoalsFormFieldError.NotANumber }
        ),
        textFieldState = rememberTextFieldState(
            dailyGoals[NutritionFactsField.Energy].formatClipZeros()
        )
    )

    val proteinsSliderState = rememberSaveable {
        mutableStateOf(
            NutrientsHelper.proteinsPercentage(
                energy = dailyGoals[NutritionFactsField.Energy].toInt(),
                proteins = dailyGoals[NutritionFactsField.Proteins].toFloat()
            ) * 100f
        )
    }
    LaunchedEffect(
        energy.value,
        proteins.value,
        proteinsSliderState.value,
        useDistributionState.value
    ) {
    }
    val carbohydratesSliderState = rememberSaveable {
        mutableStateOf(
            NutrientsHelper.carbohydratesPercentage(
                energy = dailyGoals[NutritionFactsField.Energy].toInt(),
                carbohydrates = dailyGoals[NutritionFactsField.Carbohydrates].toFloat()
            ) * 100f
        )
    }
    val fatsSliderState = rememberSaveable {
        mutableStateOf(
            NutrientsHelper.fatsPercentage(
                energy = dailyGoals[NutritionFactsField.Energy].toInt(),
                fats = dailyGoals[NutritionFactsField.Fats].toFloat()
            ) * 100f
        )
    }

    LaunchedEffect(
        proteins.value,
        carbohydrates.value,
        fats.value,
        autoCalculateEnergyState.value,
        useDistributionState.value
    ) {
        if (!useDistributionState.value && autoCalculateEnergyState.value) {
            val kcal = NutrientsHelper.calculateEnergy(
                proteins = proteins.value,
                carbohydrates = carbohydrates.value,
                fats = fats.value
            ).roundToInt()

            energy.textFieldState.setTextAndPlaceCursorAtEnd(kcal.toString())
        }
    }

    return DailyGoalsFormState(
        useDistributionState = useDistributionState,
        proteins = proteins,
        proteinsSliderState = proteinsSliderState,
        carbohydrates = carbohydrates,
        carbohydratesSliderState = carbohydratesSliderState,
        fats = fats,
        fatsSliderState = fatsSliderState,
        autoCalculateEnergyState = autoCalculateEnergyState,
        energy = energy
    )
}

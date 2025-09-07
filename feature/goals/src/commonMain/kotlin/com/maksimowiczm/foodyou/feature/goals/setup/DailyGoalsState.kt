package com.maksimowiczm.foodyou.feature.goals.setup

import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import com.maksimowiczm.foodyou.goals.domain.entity.DailyGoal
import com.maksimowiczm.foodyou.goals.domain.entity.WeeklyGoals
import com.maksimowiczm.foodyou.shared.domain.food.NutrientsHelper
import com.maksimowiczm.foodyou.shared.domain.food.NutritionFactsField
import kotlin.math.roundToInt

@Composable
internal fun rememberDailyGoalsState(weeklyGoals: WeeklyGoals): DailyGoalsState {
    val monday = rememberDayGoalsState(weeklyGoals.monday)
    val tuesday = rememberDayGoalsState(weeklyGoals.tuesday)
    val wednesday = rememberDayGoalsState(weeklyGoals.wednesday)
    val thursday = rememberDayGoalsState(weeklyGoals.thursday)
    val friday = rememberDayGoalsState(weeklyGoals.friday)
    val saturday = rememberDayGoalsState(weeklyGoals.saturday)
    val sunday = rememberDayGoalsState(weeklyGoals.sunday)

    val selectedDayState = rememberSaveable { mutableIntStateOf(0) }

    val useSeparateGoalsState = rememberSaveable { mutableStateOf(weeklyGoals.useSeparateGoals) }

    LaunchedEffect(useSeparateGoalsState.value) {
        if (!useSeparateGoalsState.value) {
            selectedDayState.value = 0
        }
    }

    val isModified = remember {
        derivedStateOf {
            monday.isModified ||
                tuesday.isModified ||
                wednesday.isModified ||
                thursday.isModified ||
                friday.isModified ||
                saturday.isModified ||
                sunday.isModified ||
                useSeparateGoalsState.value != weeklyGoals.useSeparateGoals
        }
    }

    return remember(
        monday,
        tuesday,
        wednesday,
        thursday,
        friday,
        saturday,
        sunday,
        selectedDayState,
        useSeparateGoalsState,
        isModified,
    ) {
        DailyGoalsState(
            monday = monday,
            tuesday = tuesday,
            wednesday = wednesday,
            thursday = thursday,
            friday = friday,
            saturday = saturday,
            sunday = sunday,
            selectedDayState = selectedDayState,
            useSeparateGoalsState = useSeparateGoalsState,
            isModifiedState = isModified,
        )
    }
}

@Stable
internal class DailyGoalsState(
    val monday: DayGoalsState,
    val tuesday: DayGoalsState,
    val wednesday: DayGoalsState,
    val thursday: DayGoalsState,
    val friday: DayGoalsState,
    val saturday: DayGoalsState,
    val sunday: DayGoalsState,
    selectedDayState: MutableState<Int>,
    useSeparateGoalsState: MutableState<Boolean>,
    isModifiedState: State<Boolean>,
) {
    var selectedDay by selectedDayState
    var useSeparateGoals by useSeparateGoalsState

    val isValid by derivedStateOf {
        if (!useSeparateGoals) {
            monday.isValid
        } else {
            monday.isValid &&
                tuesday.isValid &&
                wednesday.isValid &&
                thursday.isValid &&
                friday.isValid &&
                saturday.isValid &&
                sunday.isValid
        }
    }

    val isModified by isModifiedState

    val selectedDayGoals: DayGoalsState by derivedStateOf {
        when (selectedDay) {
            0 -> monday
            1 -> tuesday
            2 -> wednesday
            3 -> thursday
            4 -> friday
            5 -> saturday
            6 -> sunday
            else -> error("Invalid day index: $selectedDay")
        }
    }

    fun intoWeeklyGoals() =
        if (!useSeparateGoals) {
            val dailyGoals = monday.intoDailyGoals()
            WeeklyGoals(
                monday = dailyGoals,
                tuesday = dailyGoals,
                wednesday = dailyGoals,
                thursday = dailyGoals,
                friday = dailyGoals,
                saturday = dailyGoals,
                sunday = dailyGoals,
                useSeparateGoals = false,
            )
        } else {
            WeeklyGoals(
                monday = monday.intoDailyGoals(),
                tuesday = tuesday.intoDailyGoals(),
                wednesday = wednesday.intoDailyGoals(),
                thursday = thursday.intoDailyGoals(),
                friday = friday.intoDailyGoals(),
                saturday = saturday.intoDailyGoals(),
                sunday = sunday.intoDailyGoals(),
                useSeparateGoals = true,
            )
        }
}

@Composable
internal fun rememberDayGoalsState(goal: DailyGoal): DayGoalsState {
    val proteins = goal[NutritionFactsField.Proteins].toFloat()
    val carbohydrates = goal[NutritionFactsField.Carbohydrates].toFloat()
    val fats = goal[NutritionFactsField.Fats].toFloat()
    val energy = goal[NutritionFactsField.Energy].roundToInt()

    val sliderState =
        rememberMacroInputSliderFormState(
            proteins = NutrientsHelper.proteinsPercentage(energy, proteins) * 100f,
            carbohydrates = NutrientsHelper.carbohydratesPercentage(energy, carbohydrates) * 100f,
            fats = NutrientsHelper.fatsPercentage(energy, fats) * 100f,
            energy = energy,
        )
    val weightState =
        rememberMacroWeightInputFormState(
            proteins = proteins,
            carbohydrates = carbohydrates,
            fats = fats,
            energy = energy,
        )

    val additionalState = rememberAdditionalGoalsFormState(goal)

    val useDistribution = rememberSaveable { mutableStateOf(goal.isDistribution) }

    val isModified = remember {
        derivedStateOf {
            if (useDistribution.value != goal.isDistribution) {
                true
            } else if (useDistribution.value) {
                sliderState.isModified || additionalState.isModified
            } else {
                weightState.isModified || additionalState.isModified
            }
        }
    }

    return remember(useDistribution, sliderState, weightState, additionalState, isModified) {
        DayGoalsState(
            useDistributionState = useDistribution,
            sliderState = sliderState,
            weightState = weightState,
            additionalState = additionalState,
            isModifiedState = isModified,
        )
    }
}

@Stable
internal class DayGoalsState(
    useDistributionState: MutableState<Boolean>,
    val sliderState: MacroInputSliderFormState,
    val weightState: MacroWeightInputFormState,
    val additionalState: AdditionalGoalsFormState,
    isModifiedState: State<Boolean>,
) {
    var useDistribution by useDistributionState

    val isValid by derivedStateOf {
        if (useDistribution) {
            additionalState.isValid && sliderState.isValid
        } else {
            weightState.isValid && additionalState.isValid
        }
    }

    val isModified: Boolean by isModifiedState

    fun intoDailyGoals() =
        if (useDistribution) {
            intoDailyGoals(sliderState, additionalState)
        } else {
            intoDailyGoals(weightState, additionalState)
        }
}

private fun intoDailyGoals(
    sliderState: MacroInputSliderFormState,
    additionalState: AdditionalGoalsFormState,
): DailyGoal {
    val energy = sliderState.energy.value
    val proteins = NutrientsHelper.proteinsPercentageToGrams(energy, sliderState.proteins / 100.0)
    val carbohydrates =
        NutrientsHelper.carbohydratesPercentageToGrams(energy, sliderState.carbohydrates / 100.0)
    val fats = NutrientsHelper.fatsPercentageToGrams(energy, sliderState.fats / 100.0)

    val map =
        NutritionFactsField.entries.associateWith {
            when (it) {
                NutritionFactsField.Energy -> energy.toDouble()
                NutritionFactsField.Proteins -> proteins
                NutritionFactsField.Fats -> fats
                NutritionFactsField.SaturatedFats -> additionalState.saturatedFats.value
                NutritionFactsField.TransFats -> additionalState.transFats.value
                NutritionFactsField.MonounsaturatedFats -> additionalState.monounsaturatedFats.value
                NutritionFactsField.PolyunsaturatedFats -> additionalState.polyunsaturatedFats.value
                NutritionFactsField.Omega3 -> additionalState.omega3.value
                NutritionFactsField.Omega6 -> additionalState.omega6.value
                NutritionFactsField.Carbohydrates -> carbohydrates
                NutritionFactsField.Sugars -> additionalState.sugars.value
                NutritionFactsField.AddedSugars -> additionalState.addedSugars.value
                NutritionFactsField.DietaryFiber -> additionalState.dietaryFiber.value
                NutritionFactsField.SolubleFiber -> additionalState.solubleFiber.value
                NutritionFactsField.InsolubleFiber -> additionalState.insolubleFiber.value
                NutritionFactsField.Salt -> additionalState.salt.value
                NutritionFactsField.Cholesterol -> additionalState.cholesterolMilli.value / 1000
                NutritionFactsField.Caffeine -> additionalState.caffeineMilli.value / 1000
                NutritionFactsField.VitaminA -> additionalState.vitaminAMicro.value / 1000_000
                NutritionFactsField.VitaminB1 -> additionalState.vitaminB1Milli.value / 1000
                NutritionFactsField.VitaminB2 -> additionalState.vitaminB2Milli.value / 1000
                NutritionFactsField.VitaminB3 -> additionalState.vitaminB3Milli.value / 1000
                NutritionFactsField.VitaminB5 -> additionalState.vitaminB5Milli.value / 1000
                NutritionFactsField.VitaminB6 -> additionalState.vitaminB6Milli.value / 1000
                NutritionFactsField.VitaminB7 -> additionalState.vitaminB7Micro.value / 1000_000
                NutritionFactsField.VitaminB9 -> additionalState.vitaminB9Micro.value / 1000_000
                NutritionFactsField.VitaminB12 -> additionalState.vitaminB12Micro.value / 1000_000
                NutritionFactsField.VitaminC -> additionalState.vitaminCMilli.value / 1000
                NutritionFactsField.VitaminD -> additionalState.vitaminDMicro.value / 1000_000
                NutritionFactsField.VitaminE -> additionalState.vitaminEMilli.value / 1000
                NutritionFactsField.VitaminK -> additionalState.vitaminKMicro.value / 1000_000
                NutritionFactsField.Manganese -> additionalState.manganeseMilli.value / 1000
                NutritionFactsField.Magnesium -> additionalState.magnesiumMilli.value / 1000
                NutritionFactsField.Potassium -> additionalState.potassiumMilli.value / 1000
                NutritionFactsField.Calcium -> additionalState.calciumMilli.value / 1000
                NutritionFactsField.Copper -> additionalState.copperMilli.value / 1000
                NutritionFactsField.Zinc -> additionalState.zincMilli.value / 1000
                NutritionFactsField.Sodium -> additionalState.sodiumMilli.value / 1000
                NutritionFactsField.Iron -> additionalState.ironMilli.value / 1000
                NutritionFactsField.Phosphorus -> additionalState.phosphorusMilli.value / 1000
                NutritionFactsField.Selenium -> additionalState.seleniumMicro.value / 1000_000
                NutritionFactsField.Iodine -> additionalState.iodineMicro.value / 1000_000
                NutritionFactsField.Chromium -> additionalState.chromiumMicro.value / 1000_000
            }
        }

    return DailyGoal(map = map, isDistribution = true)
}

private fun intoDailyGoals(
    weightState: MacroWeightInputFormState,
    additionalState: AdditionalGoalsFormState,
): DailyGoal {
    val energy = weightState.energy.value
    val proteins = weightState.proteins.value
    val carbohydrates = weightState.carbohydrates.value
    val fats = weightState.fats.value

    val map =
        NutritionFactsField.entries.associateWith {
            when (it) {
                NutritionFactsField.Energy -> energy.toDouble()
                NutritionFactsField.Proteins -> proteins.toDouble()
                NutritionFactsField.Fats -> fats.toDouble()
                NutritionFactsField.SaturatedFats -> additionalState.saturatedFats.value
                NutritionFactsField.TransFats -> additionalState.transFats.value
                NutritionFactsField.MonounsaturatedFats -> additionalState.monounsaturatedFats.value
                NutritionFactsField.PolyunsaturatedFats -> additionalState.polyunsaturatedFats.value
                NutritionFactsField.Omega3 -> additionalState.omega3.value
                NutritionFactsField.Omega6 -> additionalState.omega6.value
                NutritionFactsField.Carbohydrates -> carbohydrates.toDouble()
                NutritionFactsField.Sugars -> additionalState.sugars.value
                NutritionFactsField.AddedSugars -> additionalState.addedSugars.value
                NutritionFactsField.DietaryFiber -> additionalState.dietaryFiber.value
                NutritionFactsField.SolubleFiber -> additionalState.solubleFiber.value
                NutritionFactsField.InsolubleFiber -> additionalState.insolubleFiber.value
                NutritionFactsField.Salt -> additionalState.salt.value
                NutritionFactsField.Cholesterol -> additionalState.cholesterolMilli.value / 1000
                NutritionFactsField.Caffeine -> additionalState.caffeineMilli.value / 1000
                NutritionFactsField.VitaminA -> additionalState.vitaminAMicro.value / 1000_000
                NutritionFactsField.VitaminB1 -> additionalState.vitaminB1Milli.value / 1000
                NutritionFactsField.VitaminB2 -> additionalState.vitaminB2Milli.value / 1000
                NutritionFactsField.VitaminB3 -> additionalState.vitaminB3Milli.value / 1000
                NutritionFactsField.VitaminB5 -> additionalState.vitaminB5Milli.value / 1000
                NutritionFactsField.VitaminB6 -> additionalState.vitaminB6Milli.value / 1000
                NutritionFactsField.VitaminB7 -> additionalState.vitaminB7Micro.value / 1000_000
                NutritionFactsField.VitaminB9 -> additionalState.vitaminB9Micro.value / 1000_000
                NutritionFactsField.VitaminB12 -> additionalState.vitaminB12Micro.value / 1000_000
                NutritionFactsField.VitaminC -> additionalState.vitaminCMilli.value / 1000
                NutritionFactsField.VitaminD -> additionalState.vitaminDMicro.value / 1000_000
                NutritionFactsField.VitaminE -> additionalState.vitaminEMilli.value / 1000
                NutritionFactsField.VitaminK -> additionalState.vitaminKMicro.value / 1000_000
                NutritionFactsField.Manganese -> additionalState.manganeseMilli.value / 1000
                NutritionFactsField.Magnesium -> additionalState.magnesiumMilli.value / 1000
                NutritionFactsField.Potassium -> additionalState.potassiumMilli.value / 1000
                NutritionFactsField.Calcium -> additionalState.calciumMilli.value / 1000
                NutritionFactsField.Copper -> additionalState.copperMilli.value / 1000
                NutritionFactsField.Zinc -> additionalState.zincMilli.value / 1000
                NutritionFactsField.Sodium -> additionalState.sodiumMilli.value / 1000
                NutritionFactsField.Iron -> additionalState.ironMilli.value / 1000
                NutritionFactsField.Phosphorus -> additionalState.phosphorusMilli.value / 1000
                NutritionFactsField.Selenium -> additionalState.seleniumMicro.value / 1000_000
                NutritionFactsField.Iodine -> additionalState.iodineMicro.value / 1000_000
                NutritionFactsField.Chromium -> additionalState.chromiumMicro.value / 1000_000
            }
        }

    return DailyGoal(map = map, isDistribution = false)
}

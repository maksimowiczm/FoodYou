package com.maksimowiczm.foodyou.feature.settings.goalssettings.ui.calories

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import com.maksimowiczm.foodyou.data.model.DailyGoals
import com.maksimowiczm.foodyou.data.model.NutrimentHelper
import com.maksimowiczm.foodyou.ui.form.FormFieldWithTextFieldValue
import com.maksimowiczm.foodyou.ui.form.between
import com.maksimowiczm.foodyou.ui.form.floatParser
import com.maksimowiczm.foodyou.ui.form.intParser
import com.maksimowiczm.foodyou.ui.form.nonNegative
import com.maksimowiczm.foodyou.ui.form.rememberFormFieldWithTextFieldValue
import foodyou.app.generated.resources.*
import java.util.Locale
import kotlin.math.roundToInt
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource

@Composable
fun rememberCaloriesFoalFormState(dailyGoals: DailyGoals): CaloriesGoalFormState {
    val calories = rememberFormFieldWithTextFieldValue(
        initialValue = dailyGoals.calories,
        parser = intParser(
            onEmpty = { GoalsFormInputError.Required },
            onNan = { GoalsFormInputError.MustBeInteger }
        )
    ) {
        nonNegative(
            onError = { GoalsFormInputError.NegativeNumber }
        ) {
            between(
                min = 0,
                max = 40_000,
                onError = { GoalsFormInputError.MustBeLessThan40000 }
            )
        }
    }

    val proteinsPercentage = rememberFormFieldWithTextFieldValue(
        initialValue = dailyGoals.proteinsAsPercentage,
        parser = floatParser(
            onEmpty = { GoalsFormInputError.Required },
            onNan = { GoalsFormInputError.InvalidNumber }
        ),
        formatter = { "%.2f".format(Locale.ENGLISH, it).trimEnd('0').trimEnd('.') }
    ) {
        between(
            min = 0f,
            max = 100f,
            onError = { GoalsFormInputError.MustBeLessThan100 }
        )
    }

    val proteinsGrams = rememberFormFieldWithTextFieldValue(
        initialValue = dailyGoals.proteinsAsGrams,
        parser = intParser(
            onEmpty = { GoalsFormInputError.Required },
            onNan = { GoalsFormInputError.MustBeInteger }
        )
    ) {
        nonNegative(
            onError = { GoalsFormInputError.NegativeNumber }
        )
    }

    val carbsPercentage = rememberFormFieldWithTextFieldValue(
        initialValue = dailyGoals.carbohydratesAsPercentage,
        parser = floatParser(
            onEmpty = { GoalsFormInputError.Required },
            onNan = { GoalsFormInputError.InvalidNumber }
        ),
        formatter = { "%.2f".format(Locale.ENGLISH, it).trimEnd('0').trimEnd('.') }
    ) {
        between(
            min = 0f,
            max = 100f,
            onError = { GoalsFormInputError.MustBeLessThan100 }
        )
    }

    val carbsGrams = rememberFormFieldWithTextFieldValue(
        initialValue = dailyGoals.carbohydratesAsGrams,
        parser = intParser(
            onEmpty = { GoalsFormInputError.Required },
            onNan = { GoalsFormInputError.MustBeInteger }
        )
    ) {
        nonNegative(
            onError = { GoalsFormInputError.NegativeNumber }
        )
    }

    val fatsPercentage = rememberFormFieldWithTextFieldValue(
        initialValue = dailyGoals.fatsAsPercentage,
        parser = floatParser(
            onEmpty = { GoalsFormInputError.Required },
            onNan = { GoalsFormInputError.InvalidNumber }
        ),
        formatter = { "%.2f".format(Locale.ENGLISH, it).trimEnd('0').trimEnd('.') }
    ) {
        between(
            min = 0f,
            max = 100f,
            onError = { GoalsFormInputError.MustBeLessThan100 }
        )
    }

    val fatsGrams = rememberFormFieldWithTextFieldValue(
        initialValue = dailyGoals.fatsAsGrams,
        parser = intParser(
            onEmpty = { GoalsFormInputError.Required },
            onNan = { GoalsFormInputError.MustBeInteger }
        )
    ) {
        nonNegative(
            onError = { GoalsFormInputError.NegativeNumber }
        )
    }

    val coroutineScope = rememberCoroutineScope()

    return remember {
        CaloriesGoalFormState(
            calories = calories,
            proteinsPercentage = proteinsPercentage,
            proteinsGrams = proteinsGrams,
            carbohydratesPercentage = carbsPercentage,
            carbohydratesGrams = carbsGrams,
            fatsPercentage = fatsPercentage,
            fatsGrams = fatsGrams,
            coroutineScope = coroutineScope
        )
    }
}

enum class GoalsFormInputError {
    Required,
    InvalidNumber,
    MustBeInteger,
    NegativeNumber,
    MustBeLessThan100,
    MustBeLessThan40000
    ;

    @Composable
    fun stringResource() = when (this) {
        Required -> stringResource(Res.string.error_this_field_is_required)
        InvalidNumber -> stringResource(Res.string.error_invalid_number)
        MustBeInteger -> stringResource(Res.string.error_value_must_be_integer)
        NegativeNumber -> stringResource(Res.string.error_value_cannot_be_negative)
        MustBeLessThan100 -> stringResource(Res.string.error_value_must_be_less_than, "100")
        MustBeLessThan40000 -> stringResource(Res.string.error_value_must_be_less_than, "40000")
    }
}

enum class GoalsFormError {
    PercentageMustSumUpTo100
    ;

    @Composable
    fun stringResource() = when (this) {
        PercentageMustSumUpTo100 -> stringResource(Res.string.error_sum_of_percentages_must_be_100)
    }
}

enum class Editing {
    None,
    Calories,
    Percentage,
    Grams
}

@Stable
class CaloriesGoalFormState(
    val calories: FormFieldWithTextFieldValue<Int, GoalsFormInputError>,
    val proteinsPercentage: FormFieldWithTextFieldValue<Float, GoalsFormInputError>,
    val proteinsGrams: FormFieldWithTextFieldValue<Int, GoalsFormInputError>,
    val carbohydratesPercentage: FormFieldWithTextFieldValue<Float, GoalsFormInputError>,
    val carbohydratesGrams: FormFieldWithTextFieldValue<Int, GoalsFormInputError>,
    val fatsPercentage: FormFieldWithTextFieldValue<Float, GoalsFormInputError>,
    val fatsGrams: FormFieldWithTextFieldValue<Int, GoalsFormInputError>,
    coroutineScope: CoroutineScope
) {
    // No idea if there are race conditions here. Should probably test it xd.
    init {
        var editing = Editing.None

        coroutineScope.launch {
            launch {
                calories.interactionSource.interactions.collectLatest {
                    editing = Editing.Calories
                }
            }

            launch {
                merge(
                    proteinsPercentage.interactionSource.interactions,
                    carbohydratesPercentage.interactionSource.interactions,
                    fatsPercentage.interactionSource.interactions
                ).collectLatest {
                    editing = Editing.Percentage
                }
            }

            launch {
                merge(
                    proteinsGrams.interactionSource.interactions,
                    carbohydratesGrams.interactionSource.interactions,
                    fatsGrams.interactionSource.interactions
                ).collectLatest {
                    editing = Editing.Grams
                }
            }
        }

        // Auto calculate grams when calories change
        coroutineScope.launch {
            snapshotFlow { calories.value }.collectLatest {
                val dailyGoals = intoDailyGoals()

                if (editing != Editing.Calories) return@collectLatest

                proteinsGrams.onRawValueChange(dailyGoals.proteinsAsGrams)
                carbohydratesGrams.onRawValueChange(dailyGoals.carbohydratesAsGrams)
                fatsGrams.onRawValueChange(dailyGoals.fatsAsGrams)
            }
        }

        // Auto calculate grams when percentages change
        coroutineScope.launch {
            combine(
                snapshotFlow { proteinsPercentage.value },
                snapshotFlow { carbohydratesPercentage.value },
                snapshotFlow { fatsPercentage.value }
            ) { proteins, carbohydrates, fats ->
                arrayListOf(proteins, carbohydrates, fats)
            }.collectLatest {
                if (editing != Editing.Percentage) return@collectLatest

                val dailyGoals = intoDailyGoals()

                if (isValid) {
                    proteinsGrams.onRawValueChange(dailyGoals.proteinsAsGrams)
                    carbohydratesGrams.onRawValueChange(dailyGoals.carbohydratesAsGrams)
                    fatsGrams.onRawValueChange(dailyGoals.fatsAsGrams)
                }
            }
        }

        // Auto calculate percentages when grams change
        coroutineScope.launch {
            combine(
                snapshotFlow { proteinsGrams.value },
                snapshotFlow { carbohydratesGrams.value },
                snapshotFlow { fatsGrams.value }
            ) { proteins, carbohydrates, fats ->
                arrayListOf(proteins, carbohydrates, fats)
            }.collectLatest { (proteinsValue, carbohydratesValue, fatsValue) ->
                if (editing != Editing.Grams) return@collectLatest

                val caloriesValue = NutrimentHelper.calculateCalories(
                    proteins = proteinsValue.toFloat(),
                    carbohydrates = carbohydratesValue.toFloat(),
                    fats = fatsValue.toFloat()
                ).roundToInt()

                val dailyGoals = DailyGoals(
                    calories = caloriesValue,
                    proteins = NutrimentHelper.proteinsPercentage(caloriesValue, proteinsValue),
                    carbohydrates = NutrimentHelper.carbohydratesPercentage(
                        caloriesValue,
                        carbohydratesValue
                    ),
                    fats = NutrimentHelper.fatsPercentage(caloriesValue, fatsValue)
                )

                calories.onRawValueChange(caloriesValue)
                proteinsPercentage.onRawValueChange(dailyGoals.proteinsAsPercentage)
                carbohydratesPercentage.onRawValueChange(dailyGoals.carbohydratesAsPercentage)
                fatsPercentage.onRawValueChange(dailyGoals.fatsAsPercentage)
            }
        }
    }

    val isValid by derivedStateOf {
        calories.error == null &&
            calories.isValid &&
            proteinsPercentage.error == null &&
            proteinsPercentage.isValid &&
            proteinsGrams.error == null &&
            proteinsGrams.isValid &&
            carbohydratesPercentage.error == null &&
            carbohydratesPercentage.isValid &&
            carbohydratesGrams.error == null &&
            carbohydratesGrams.isValid &&
            fatsPercentage.error == null &&
            fatsPercentage.isValid &&
            fatsGrams.error == null &&
            fatsGrams.isValid &&
            error == null
    }

    val error: GoalsFormError? by derivedStateOf {
        val sum = proteinsPercentage.value + carbohydratesPercentage.value + fatsPercentage.value

        if ((sum - 100f) !in (-0.01f..0.01f)) {
            return@derivedStateOf GoalsFormError.PercentageMustSumUpTo100
        }

        null
    }

    fun intoDailyGoals() = DailyGoals(
        calories = calories.value,
        proteins = proteinsPercentage.value / 100f,
        carbohydrates = carbohydratesPercentage.value / 100f,
        fats = fatsPercentage.value / 100f
    )
}

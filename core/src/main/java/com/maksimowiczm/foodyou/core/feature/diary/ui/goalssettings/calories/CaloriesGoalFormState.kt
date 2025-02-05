package com.maksimowiczm.foodyou.core.feature.diary.ui.goalssettings.calories

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import com.maksimowiczm.foodyou.core.R
import com.maksimowiczm.foodyou.core.feature.diary.data.model.DailyGoals
import com.maksimowiczm.foodyou.core.feature.diary.data.model.NutrimentHelper
import com.maksimowiczm.foodyou.core.ui.form.FormFieldWithTextFieldValue
import com.maksimowiczm.foodyou.core.ui.form.between
import com.maksimowiczm.foodyou.core.ui.form.nonNegative
import com.maksimowiczm.foodyou.core.ui.form.notNull
import com.maksimowiczm.foodyou.core.ui.form.nullableFloatParser
import com.maksimowiczm.foodyou.core.ui.form.nullableIntParser
import com.maksimowiczm.foodyou.core.ui.form.rememberFormFieldWithTextFieldValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.util.Locale
import kotlin.math.roundToInt

@Composable
fun rememberCaloriesFoalFormState(
    dailyGoals: DailyGoals
): CaloriesGoalFormState {
    val calories = rememberFormFieldWithTextFieldValue(
        initialTextFieldValue = TextFieldValue(
            text = dailyGoals.calories.toString(),
            selection = TextRange(dailyGoals.calories.toString().length)
        ),
        initialValue = dailyGoals.calories,
        requireDirty = false,
        parser = nullableIntParser { GoalsFormInputError.MustBeInteger }
    ) {
        notNull(
            onError = { GoalsFormInputError.Required }
        ) {
            nonNegative(
                onError = { GoalsFormInputError.NegativeNumber }
            ) {
                between(
                    min = 0,
                    max = 40_000,
                    onMinError = { GoalsFormInputError.MustBeLessThan40000 }
                )
            }
        }
    }

    val proteinsPercentage = rememberFormFieldWithTextFieldValue(
        initialTextFieldValue = TextFieldValue(
            text = "%.2f"
                // Locale.ENGLISH to use dot as decimal separator
                .format(Locale.ENGLISH, dailyGoals.proteinsAsPercentage)
                .trimEnd('0')
                .trimEnd('.'),
            selection = TextRange(dailyGoals.proteinsAsPercentage.toString().length)
        ),
        initialValue = dailyGoals.proteinsAsPercentage,
        requireDirty = false,
        parser = nullableFloatParser { GoalsFormInputError.InvalidNumber },
        formatter = { "%.2f".format(Locale.ENGLISH, it).trimEnd('0').trimEnd('.') }
    ) {
        notNull(
            onError = { GoalsFormInputError.Required }
        ) {
            between(
                min = 0f,
                max = 100f,
                onMinError = { GoalsFormInputError.MustBeLessThan100 }
            )
        }
    }

    val proteinsGrams = rememberFormFieldWithTextFieldValue(
        initialTextFieldValue = TextFieldValue(
            text = dailyGoals.proteinsAsGrams.toString(),
            selection = TextRange(dailyGoals.proteinsAsGrams.toString().length)
        ),
        initialValue = dailyGoals.proteinsAsGrams,
        requireDirty = false,
        parser = nullableIntParser { GoalsFormInputError.MustBeInteger }
    ) {
        notNull(
            onError = { GoalsFormInputError.Required }
        ) {
            nonNegative(
                onError = { GoalsFormInputError.NegativeNumber }
            )
        }
    }

    val carbsPercentage = rememberFormFieldWithTextFieldValue(
        initialTextFieldValue = TextFieldValue(
            text = "%.2f"
                .format(Locale.ENGLISH, dailyGoals.carbohydratesAsPercentage)
                .trimEnd('0')
                .trimEnd('.'),
            selection = TextRange(dailyGoals.carbohydratesAsPercentage.toString().length)
        ),
        initialValue = dailyGoals.carbohydratesAsPercentage,
        requireDirty = false,
        parser = nullableFloatParser { GoalsFormInputError.InvalidNumber },
        formatter = { "%.2f".format(Locale.ENGLISH, it).trimEnd('0').trimEnd('.') }
    ) {
        notNull(
            onError = { GoalsFormInputError.Required }
        ) {
            between(
                min = 0f,
                max = 100f,
                onMinError = { GoalsFormInputError.MustBeLessThan100 }
            )
        }
    }

    val carbsGrams = rememberFormFieldWithTextFieldValue(
        initialTextFieldValue = TextFieldValue(
            text = dailyGoals.carbohydratesAsGrams.toString(),
            selection = TextRange(dailyGoals.carbohydratesAsGrams.toString().length)
        ),
        initialValue = dailyGoals.carbohydratesAsGrams,
        requireDirty = false,
        parser = nullableIntParser { GoalsFormInputError.MustBeInteger }
    ) {
        notNull(
            onError = { GoalsFormInputError.Required }
        ) {
            nonNegative(
                onError = { GoalsFormInputError.NegativeNumber }
            )
        }
    }

    val fatsPercentage = rememberFormFieldWithTextFieldValue(
        initialTextFieldValue = TextFieldValue(
            text = "%.2f"
                .format(Locale.ENGLISH, dailyGoals.fatsAsPercentage)
                .trimEnd('0')
                .trimEnd('.'),
            selection = TextRange(dailyGoals.fatsAsPercentage.toString().length)
        ),
        initialValue = dailyGoals.fatsAsPercentage,
        requireDirty = false,
        parser = nullableFloatParser { GoalsFormInputError.InvalidNumber },
        formatter = { "%.2f".format(Locale.ENGLISH, it).trimEnd('0').trimEnd('.') }
    ) {
        notNull(
            onError = { GoalsFormInputError.Required }
        ) {
            between(
                min = 0f,
                max = 100f,
                onMinError = { GoalsFormInputError.MustBeLessThan100 }
            )
        }
    }

    val fatsGrams = rememberFormFieldWithTextFieldValue(
        initialTextFieldValue = TextFieldValue(
            text = dailyGoals.fatsAsGrams.toString(),
            selection = TextRange(dailyGoals.fatsAsGrams.toString().length)
        ),
        initialValue = dailyGoals.fatsAsGrams,
        requireDirty = false,
        parser = nullableIntParser { GoalsFormInputError.MustBeInteger }
    ) {
        notNull(
            onError = { GoalsFormInputError.Required }
        ) {
            nonNegative(
                onError = { GoalsFormInputError.NegativeNumber }
            )
        }
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
        Required -> stringResource(R.string.error_this_field_is_required)
        InvalidNumber -> stringResource(R.string.error_invalid_number)
        MustBeInteger -> stringResource(R.string.error_value_must_be_integer)
        NegativeNumber -> stringResource(R.string.error_value_cannot_be_negative)
        MustBeLessThan100 -> stringResource(R.string.error_value_must_be_less_than, "100")
        MustBeLessThan40000 -> stringResource(R.string.error_value_must_be_less_than, "40000")
    }
}

enum class GoalsFormError {
    PercentageMustSumUpTo100
    ;

    @Composable
    fun stringResource() = when (this) {
        PercentageMustSumUpTo100 -> stringResource(R.string.error_sum_of_percentages_must_be_100)
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
    val calories: FormFieldWithTextFieldValue<Int?, GoalsFormInputError>,
    val proteinsPercentage: FormFieldWithTextFieldValue<Float?, GoalsFormInputError>,
    val proteinsGrams: FormFieldWithTextFieldValue<Int?, GoalsFormInputError>,
    val carbohydratesPercentage: FormFieldWithTextFieldValue<Float?, GoalsFormInputError>,
    val carbohydratesGrams: FormFieldWithTextFieldValue<Int?, GoalsFormInputError>,
    val fatsPercentage: FormFieldWithTextFieldValue<Float?, GoalsFormInputError>,
    val fatsGrams: FormFieldWithTextFieldValue<Int?, GoalsFormInputError>,
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
                proteinsPercentage.interactionSource.interactions.collectLatest {
                    editing = Editing.Percentage
                }
            }
            launch {
                carbohydratesPercentage.interactionSource.interactions.collectLatest {
                    editing = Editing.Percentage
                }
            }
            launch {
                carbohydratesPercentage.interactionSource.interactions.collectLatest {
                    editing = Editing.Percentage
                }
            }

            launch {
                proteinsGrams.interactionSource.interactions.collectLatest {
                    editing = Editing.Grams
                }
            }
            launch {
                carbohydratesGrams.interactionSource.interactions.collectLatest {
                    editing = Editing.Grams
                }
            }
            launch {
                fatsGrams.interactionSource.interactions.collectLatest {
                    editing = Editing.Grams
                }
            }
        }

        // Auto calculate grams when calories change
        coroutineScope.launch {
            snapshotFlow { calories.value }.collectLatest {
                val dailyGoals = intoDailyGoals() ?: return@collectLatest

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

                val dailyGoals = intoDailyGoals() ?: return@collectLatest

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
            }.filter {
                it.all { it != null }
            }.map {
                it.map { it!! }
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
        calories.error == null && calories.isValid &&
            proteinsPercentage.error == null && proteinsPercentage.isValid &&
            proteinsGrams.error == null && proteinsGrams.isValid &&
            carbohydratesPercentage.error == null && carbohydratesPercentage.isValid &&
            carbohydratesGrams.error == null && carbohydratesGrams.isValid &&
            fatsPercentage.error == null && fatsPercentage.isValid &&
            fatsGrams.error == null && fatsGrams.isValid &&
            error == null
    }

    val error: GoalsFormError? by derivedStateOf {
        if (
            proteinsPercentage.value != null &&
            carbohydratesPercentage.value != null &&
            fatsPercentage.value != null &&
            (proteinsPercentage.value + carbohydratesPercentage.value + fatsPercentage.value - 100f) !in (-0.01f..0.01f)
        ) {
            return@derivedStateOf GoalsFormError.PercentageMustSumUpTo100
        }

        null
    }

    fun intoDailyGoals(): DailyGoals? {
        if (
            calories.value == null ||
            proteinsPercentage.value == null ||
            proteinsGrams.value == null ||
            carbohydratesPercentage.value == null ||
            carbohydratesGrams.value == null ||
            fatsPercentage.value == null ||
            fatsGrams.value == null
        ) {
            return null
        }

        return DailyGoals(
            calories = calories.value,
            proteins = proteinsPercentage.value / 100f,
            carbohydrates = carbohydratesPercentage.value / 100f,
            fats = fatsPercentage.value / 100f
        )
    }
}

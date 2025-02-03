package com.maksimowiczm.foodyou.core.feature.diary.ui.goalssettings.calories

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import kotlin.math.roundToInt

@Composable
fun rememberCaloriesFoalFormState(
    dailyGoals: com.maksimowiczm.foodyou.core.feature.diary.data.model.DailyGoals
): CaloriesGoalFormState {
    val caloriesTextFieldValue = rememberSaveable(
        dailyGoals.calories,
        stateSaver = TextFieldValue.Saver
    ) {
        val string = dailyGoals.calories.toString()

        mutableStateOf(
            TextFieldValue(
                text = string,
                selection = TextRange(string.length)
            )
        )
    }

    val proteinsPercentage = rememberSaveable(
        dailyGoals.proteins,
        stateSaver = TextFieldValue.Saver
    ) {
        val string = (dailyGoals.proteins * 100).toInt().toString()

        mutableStateOf(
            TextFieldValue(
                text = string,
                selection = TextRange(string.length)
            )
        )
    }

    val proteinsGrams = rememberSaveable(
        dailyGoals.proteinsAsGrams,
        stateSaver = TextFieldValue.Saver
    ) {
        val string = dailyGoals.proteinsAsGrams.toString()

        mutableStateOf(
            TextFieldValue(
                text = string,
                selection = TextRange(string.length)
            )
        )
    }

    val carbsPercentage = rememberSaveable(
        dailyGoals.carbohydrates,
        stateSaver = TextFieldValue.Saver
    ) {
        val string = (dailyGoals.carbohydrates * 100).toInt().toString()

        mutableStateOf(
            TextFieldValue(
                text = string,
                selection = TextRange(string.length)
            )
        )
    }

    val carbsGrams = rememberSaveable(
        dailyGoals.carbohydratesAsGrams,
        stateSaver = TextFieldValue.Saver
    ) {
        val string = dailyGoals.carbohydratesAsGrams.toString()

        mutableStateOf(
            TextFieldValue(
                text = string,
                selection = TextRange(string.length)
            )
        )
    }

    val fatsPercentage = rememberSaveable(
        dailyGoals.fats,
        stateSaver = TextFieldValue.Saver
    ) {
        val string = (dailyGoals.fats * 100).toInt().toString()

        mutableStateOf(
            TextFieldValue(
                text = string,
                selection = TextRange(string.length)
            )
        )
    }

    val fatsGrams = rememberSaveable(
        dailyGoals.fatsAsGrams,
        stateSaver = TextFieldValue.Saver
    ) {
        val string = dailyGoals.fatsAsGrams.toString()

        mutableStateOf(
            TextFieldValue(
                text = string,
                selection = TextRange(string.length)
            )
        )
    }

    return remember {
        CaloriesGoalFormState(
            initialDailyGoals = dailyGoals,
            initialCaloriesTextFieldValue = caloriesTextFieldValue,
            initialProteinsPercentageTextFieldValue = proteinsPercentage,
            initialProteinsGramsTextFieldValue = proteinsGrams,
            initialCarbsPercentageTextFieldValue = carbsPercentage,
            initialCarbsGramsTextFieldValue = carbsGrams,
            initialFatsPercentageTextFieldValue = fatsPercentage,
            initialFatsGramsTextFieldValue = fatsGrams
        )
    }
}

class CaloriesGoalFormState(
    initialDailyGoals: com.maksimowiczm.foodyou.core.feature.diary.data.model.DailyGoals,
    initialCaloriesTextFieldValue: MutableState<TextFieldValue>,
    initialProteinsPercentageTextFieldValue: MutableState<TextFieldValue>,
    initialProteinsGramsTextFieldValue: MutableState<TextFieldValue>,
    initialCarbsPercentageTextFieldValue: MutableState<TextFieldValue>,
    initialCarbsGramsTextFieldValue: MutableState<TextFieldValue>,
    initialFatsPercentageTextFieldValue: MutableState<TextFieldValue>,
    initialFatsGramsTextFieldValue: MutableState<TextFieldValue>
) {
    var dailyGoals by mutableStateOf(initialDailyGoals)
        private set

    val totalPercentage by derivedStateOf {
        ((dailyGoals.proteins + dailyGoals.carbohydrates + dailyGoals.fats) * 100).roundToInt()
    }

    val isError by derivedStateOf { totalPercentage != 100 }

    var caloriesTextFieldValue by initialCaloriesTextFieldValue
        private set

    fun onCaloriesChanged(newValue: TextFieldValue) {
        val text = newValue.text

        val int = if (text.isEmpty()) {
            0
        } else {
            text
                .takeIf { it.length <= 5 }
                ?.toIntOrNull()
                ?.takeIf { it in 0..99999 }
                ?: return
        }

        caloriesTextFieldValue = newValue

        dailyGoals = dailyGoals.copy(
            calories = int
        )

        val proteinsPercentageString = (dailyGoals.proteins * 100).toInt().toString()
        proteinsPercentageTextFieldValue = TextFieldValue(
            text = proteinsPercentageString,
            selection = TextRange(proteinsPercentageString.length)
        )

        val proteinsGramsString = dailyGoals.proteinsAsGrams.toString()
        proteinsGramsTextFieldValue = TextFieldValue(
            text = proteinsGramsString,
            selection = TextRange(proteinsGramsString.length)
        )

        val carbsPercentageString = (dailyGoals.carbohydrates * 100).toInt().toString()
        carbsPercentageTextFieldValue = TextFieldValue(
            text = carbsPercentageString,
            selection = TextRange(carbsPercentageString.length)
        )

        val carbsGramsString = dailyGoals.carbohydratesAsGrams.toString()
        carbsGramsTextFieldValue = TextFieldValue(
            text = carbsGramsString,
            selection = TextRange(carbsGramsString.length)
        )

        val fatsPercentageString = (dailyGoals.fats * 100).toInt().toString()
        fatsPercentageTextFieldValue = TextFieldValue(
            text = fatsPercentageString,
            selection = TextRange(fatsPercentageString.length)
        )

        val fatsGramsString = dailyGoals.fatsAsGrams.toString()
        fatsGramsTextFieldValue = TextFieldValue(
            text = fatsGramsString,
            selection = TextRange(fatsGramsString.length)
        )
    }

    private fun onPercentageChanged(
        newValue: TextFieldValue,
        callback: (TextFieldValue, percentage: Float) -> Unit
    ) {
        val text = newValue.text

        val int = if (text.isEmpty()) {
            0
        } else {
            text
                .takeIf { it.length <= 3 }
                ?.toIntOrNull()
                ?: return
        }

        callback(newValue, int / 100f)
    }

    private fun onGramsChanged(
        newValue: TextFieldValue,
        callback: (TextFieldValue, grams: Int) -> Unit
    ) {
        val text = newValue.text

        val int = if (text.isEmpty()) {
            0
        } else {
            text
                .takeIf { it.length <= 4 }
                ?.toIntOrNull()
                ?: return
        }

        callback(newValue, int)
    }

    var proteinsPercentageTextFieldValue by initialProteinsPercentageTextFieldValue
        private set

    var proteinsGramsTextFieldValue by initialProteinsGramsTextFieldValue
        private set

    fun onProteinsPercentageChanged(newValue: TextFieldValue) {
        onPercentageChanged(newValue) { value, percentage ->
            if (percentage > 1 && percentage > dailyGoals.proteins) {
                return@onPercentageChanged
            }

            proteinsPercentageTextFieldValue = value

            dailyGoals = dailyGoals.copy(
                proteins = percentage
            )

            val grams = dailyGoals.proteinsAsGrams.toString()
            proteinsGramsTextFieldValue = TextFieldValue(
                text = grams,
                selection = TextRange(grams.length)
            )
        }
    }

    fun onProteinsGramsChanged(newValue: TextFieldValue) {
        onGramsChanged(newValue) { value, grams ->
            proteinsGramsTextFieldValue = value

            val percentage = grams.toFloat() * com.maksimowiczm.foodyou.core.feature.diary.data.model.NutrimentsAsGrams.PROTEINS / dailyGoals.calories

            dailyGoals = dailyGoals.copy(
                proteins = percentage
            )

            val percentageString = (percentage * 100).toInt().toString()

            proteinsPercentageTextFieldValue = TextFieldValue(
                text = percentageString,
                selection = TextRange(percentageString.length)
            )
        }
    }

    var carbsPercentageTextFieldValue by initialCarbsPercentageTextFieldValue
        private set

    var carbsGramsTextFieldValue by initialCarbsGramsTextFieldValue
        private set

    fun onCarbohydratesPercentageChanged(newValue: TextFieldValue) {
        onPercentageChanged(newValue) { value, percentage ->
            if (percentage > 1 && percentage > dailyGoals.carbohydrates) {
                return@onPercentageChanged
            }

            carbsPercentageTextFieldValue = value

            dailyGoals = dailyGoals.copy(
                carbohydrates = percentage
            )

            val grams = dailyGoals.carbohydratesAsGrams.toString()
            carbsGramsTextFieldValue = TextFieldValue(
                text = grams,
                selection = TextRange(grams.length)
            )
        }
    }

    fun onCarbohydratesGramsChanged(newValue: TextFieldValue) {
        onGramsChanged(newValue) { value, grams ->
            carbsGramsTextFieldValue = value

            val percentage =
                grams.toFloat() * com.maksimowiczm.foodyou.core.feature.diary.data.model.NutrimentsAsGrams.CARBOHYDRATES / dailyGoals.calories

            dailyGoals = dailyGoals.copy(
                carbohydrates = percentage
            )

            val percentageString = (percentage * 100).toInt().toString()

            carbsPercentageTextFieldValue = TextFieldValue(
                text = percentageString,
                selection = TextRange(percentageString.length)
            )
        }
    }

    var fatsPercentageTextFieldValue by initialFatsPercentageTextFieldValue
        private set

    var fatsGramsTextFieldValue by initialFatsGramsTextFieldValue
        private set

    fun onFatsPercentageChanged(newValue: TextFieldValue) {
        onPercentageChanged(newValue) { value, percentage ->
            if (percentage > 1 && percentage > dailyGoals.fats) {
                return@onPercentageChanged
            }

            fatsPercentageTextFieldValue = value

            dailyGoals = dailyGoals.copy(
                fats = percentage
            )

            val grams = dailyGoals.fatsAsGrams.toString()
            fatsGramsTextFieldValue = TextFieldValue(
                text = grams,
                selection = TextRange(grams.length)
            )
        }
    }

    fun onFatsGramsChanged(newValue: TextFieldValue) {
        onGramsChanged(newValue) { value, grams ->
            fatsGramsTextFieldValue = value

            val percentage = grams.toFloat() * com.maksimowiczm.foodyou.core.feature.diary.data.model.NutrimentsAsGrams.FATS / dailyGoals.calories

            dailyGoals = dailyGoals.copy(
                fats = percentage
            )

            val percentageString = (percentage * 100).toInt().toString()

            fatsPercentageTextFieldValue = TextFieldValue(
                text = percentageString,
                selection = TextRange(percentageString.length)
            )
        }
    }
}

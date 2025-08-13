package com.maksimowiczm.foodyou.feature.food.diary.shared.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import com.maksimowiczm.foodyou.feature.food.shared.ui.MeasurementPickerState
import com.maksimowiczm.foodyou.feature.food.shared.ui.rememberMeasurementPickerState
import com.maksimowiczm.foodyou.shared.common.domain.measurement.Measurement
import com.maksimowiczm.foodyou.shared.common.domain.measurement.MeasurementType
import kotlinx.datetime.LocalDate

@Composable
fun rememberFoodMeasurementFormState(
    today: LocalDate,
    possibleDates: List<LocalDate>,
    selectedDate: LocalDate,
    meals: List<String>,
    selectedMeal: String?,
    suggestions: List<Measurement>,
    possibleTypes: List<MeasurementType>,
    selectedMeasurement: Measurement,
): FoodMeasurementFormState {
    val dateState =
        rememberChipsDatePickerState(
            today = today,
            initialDates = possibleDates,
            selectedDate = selectedDate,
        )
    val mealsState = rememberChipsMealPickerState(meals = meals, selectedMeal = selectedMeal)

    val measurementState =
        rememberMeasurementPickerState(
            suggestions = suggestions,
            possibleTypes = possibleTypes,
            selectedMeasurement = selectedMeasurement,
        )

    return remember(dateState, mealsState, measurementState) {
        FoodMeasurementFormState(dateState, mealsState, measurementState)
    }
}

@Stable
class FoodMeasurementFormState(
    val dateState: ChipsDatePickerState,
    val mealsState: ChipsMealPickerState,
    val measurementState: MeasurementPickerState,
) {
    val isValid by derivedStateOf {
        measurementState.inputField.error == null && mealsState.selectedMeal != null
    }
}

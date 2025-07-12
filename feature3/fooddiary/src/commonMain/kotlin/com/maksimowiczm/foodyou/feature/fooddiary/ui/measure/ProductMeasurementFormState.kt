package com.maksimowiczm.foodyou.feature.fooddiary.ui.measure

import androidx.compose.runtime.*
import com.maksimowiczm.foodyou.feature.fooddiary.data.Meal
import com.maksimowiczm.foodyou.feature.measurement.data.Measurement as MeasurementType
import com.maksimowiczm.foodyou.feature.measurement.domain.Measurement
import kotlinx.datetime.LocalDate

@Composable
internal fun rememberProductMeasurementFormState(
    today: LocalDate,
    possibleDates: Set<LocalDate>,
    meals: Set<Meal>,
    selectedMeal: Meal,
    suggestions: Set<Measurement>,
    possibleTypes: Set<MeasurementType>,
    selectedMeasurement: Measurement
): ProductMeasurementFormState {
    val dateState = rememberChipsDatePickerState(
        today = today,
        initialDates = possibleDates
    )
    val mealsState = rememberChipsMealPickerState(
        meals = meals.map { it.name }.toSet(),
        selectedMeal = selectedMeal.name
    )
    val measurementState = rememberMeasurementPickerState(
        suggestions = suggestions,
        possibleTypes = possibleTypes,
        selectedMeasurement = selectedMeasurement
    )

    return remember(dateState, mealsState, measurementState) {
        ProductMeasurementFormState(
            dateState = dateState,
            mealsState = mealsState,
            measurementState = measurementState
        )
    }
}

@Stable
internal class ProductMeasurementFormState(
    val dateState: ChipsDatePickerState,
    val mealsState: ChipsMealPickerState,
    val measurementState: MeasurementPickerState
) {
    val isValid by derivedStateOf {
        measurementState.inputField.error == null
    }
}

package com.maksimowiczm.foodyou.feature.measurement.ui.advanced

import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.maksimowiczm.foodyou.core.domain.model.Food
import com.maksimowiczm.foodyou.core.domain.model.Meal
import com.maksimowiczm.foodyou.core.domain.model.Measurement
import com.maksimowiczm.foodyou.core.ui.ext.Saver
import com.maksimowiczm.foodyou.core.ui.res.Saver
import com.maksimowiczm.foodyou.feature.measurement.ui.basic.MeasurementFormState
import com.maksimowiczm.foodyou.feature.measurement.ui.basic.rememberMeasurementFormState
import kotlinx.datetime.LocalDate

@Composable
fun rememberAdvancedMeasurementFormState(
    food: Food,
    initialDate: LocalDate,
    meals: List<Meal>,
    measurements: List<Measurement>,
    initialMeal: Int? = null,
    initialMeasurement: Int? = null
): AdvancedMeasurementFormState {
    val formState = rememberMeasurementFormState(food)

    return rememberSaveable(
        formState,
        meals,
        saver = Saver(
            save = { original ->
                val savedDate = with(LocalDate.Saver) { save(original.selectedDate) }

                // Save measurements list using the Measurement.Saver
                val measurementsList = original.measurements.map { measurement ->
                    with(Measurement.Saver) { save(measurement) }
                }

                listOf(
                    savedDate,
                    original.selectedMeal,
                    original.selectedMeasurement,
                    measurementsList
                )
            },
            restore = { saved ->
                val date = LocalDate.Saver.restore(saved[0] as Int) ?: initialDate
                val selectedMeal = saved[1] as Int?
                val selectedMeasurement = saved[2] as Int?

                val measurementsList = (saved[3] as List<*>).mapNotNull { savedMeasurement ->
                    @Suppress("UNCHECKED_CAST")
                    Measurement.Saver.restore(savedMeasurement as ArrayList<Any>)
                }

                AdvancedMeasurementFormState(
                    initialDate = date,
                    meals = meals,
                    initialMeal = selectedMeal,
                    initialMeasurements = measurementsList,
                    initialMeasurement = selectedMeasurement,
                    formState = formState
                )
            }
        )
    ) {
        AdvancedMeasurementFormState(
            initialDate = initialDate,
            meals = meals,
            initialMeal = initialMeal,
            initialMeasurements = measurements,
            initialMeasurement = initialMeasurement,
            formState = formState
        )
    }
}

class AdvancedMeasurementFormState(
    initialDate: LocalDate,
    val meals: List<Meal>,
    initialMeal: Int?,
    initialMeasurements: List<Measurement>,
    initialMeasurement: Int?,
    val formState: MeasurementFormState
) {
    /**
     * The selected date for the measurement.
     */
    var selectedDate by mutableStateOf<LocalDate>(initialDate)

    /**
     * The selected meal index.
     */
    var selectedMeal by mutableStateOf<Int?>(initialMeal)

    /**
     * The selected measurement index.
     */
    var selectedMeasurement by mutableStateOf<Int?>(initialMeasurement)

    /**
     * The list of available measurements.
     */
    var measurements by mutableStateOf(initialMeasurements)
        private set

    /**
     * The selected measurement.
     */
    val date = selectedDate

    /**
     * The selected meal.
     */
    val meal by derivedStateOf { selectedMeal?.let { meals[it] } }

    /**
     * The selected measurement.
     */
    val measurement by derivedStateOf { selectedMeasurement?.let { measurements[it] } }

    /**
     * Add a new measurement to the list of available measurements.
     */
    fun addMeasurement(measurement: Measurement) {
        measurements = (measurements + measurement).distinct()
        selectedMeasurement = measurements.indexOf(measurement)
    }

    val isValid: Boolean
        get() = selectedMeal != null && selectedMeasurement != null
}

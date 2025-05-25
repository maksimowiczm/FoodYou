package com.maksimowiczm.foodyou.feature.measurement.ui.advanced

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.maksimowiczm.foodyou.core.ui.res.stringResource
import com.maksimowiczm.foodyou.feature.measurement.ui.basic.MeasurementForm
import foodyou.app.generated.resources.*
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdvancedMeasurementForm(state: AdvancedMeasurementFormState, modifier: Modifier = Modifier) {
    var showDatePicker by rememberSaveable { mutableStateOf(false) }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState()

        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let {
                            Instant
                                .fromEpochMilliseconds(it)
                                .toLocalDateTime(TimeZone.currentSystemDefault())
                                .date
                        }?.let {
                            state.selectedDate = it
                        }

                        showDatePicker = false
                    }
                ) {
                    Text(stringResource(Res.string.positive_ok))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDatePicker = false }
                ) {
                    Text(stringResource(Res.string.action_cancel))
                }
            }
        ) {
            DatePicker(
                state = datePickerState
            )
        }
    }

    Column(
        modifier = modifier
    ) {
        ChipsDatePicker(
            dates = listOf(state.selectedDate),
            selectedDate = state.selectedDate,
            onDateChange = { state.selectedDate = it },
            onChooseDate = { showDatePicker = true },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        )
        HorizontalDivider()
        ChipsMealPicker(
            meals = state.meals.map { it.name },
            selectedMeal = state.selectedMeal,
            onMealSelect = { state.selectedMeal = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        )
        HorizontalDivider()
        AnimatedContent(
            targetState = state.selectedMeasurement == null
        ) {
            if (it) {
                MeasurementForm(
                    state = state.formState,
                    onMeasurement = {
                        state.addMeasurement(it)
                    },
                    contentPadding = PaddingValues(8.dp)
                )
            } else {
                ChipsMeasurementPicker(
                    measurements = state.measurements.map { it.stringResource() },
                    selectedMeasurement = state.selectedMeasurement,
                    onMeasurementSelect = { state.selectedMeasurement = it },
                    onChooseOtherMeasurement = { state.selectedMeasurement = null },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                )
            }
        }
    }
}

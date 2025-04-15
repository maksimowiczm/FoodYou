package com.maksimowiczm.foodyou.feature.measurement

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.maksimowiczm.foodyou.core.domain.model.Measurement
import com.maksimowiczm.foodyou.feature.measurement.ui.MeasurementScreen as MeasurementScreenImpl

@Composable
fun MeasurementScreen(
    food: MeasurableFood,
    selectedMeasurement: Measurement?,
    onBack: () -> Unit,
    onMeasurement: (Measurement) -> Unit,
    onEditFood: () -> Unit,
    onDeleteFood: () -> Unit,
    modifier: Modifier = Modifier
) {
    MeasurementScreenImpl(
        food = food,
        selectedMeasurement = selectedMeasurement,
        onBack = onBack,
        onMeasurement = onMeasurement,
        onEditFood = onEditFood,
        onDeleteFood = onDeleteFood,
        modifier = modifier
    )
}

package com.maksimowiczm.foodyou.feature.diary.ui.addfoodproduct

import androidx.lifecycle.ViewModel
import com.maksimowiczm.foodyou.feature.diary.data.model.WeightMeasurement
import com.maksimowiczm.foodyou.feature.diary.data.model.WeightMeasurementEnum
import com.maksimowiczm.foodyou.feature.diary.ui.addfoodproduct.model.Product
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

abstract class MeasurementViewModel : ViewModel() {
    abstract val product: Flow<Product?>
    abstract val done: StateFlow<Boolean>
    abstract val processing: StateFlow<WeightMeasurementEnum?>

    abstract fun onConfirm(weightMeasurement: WeightMeasurement)
    abstract fun onDelete()
}

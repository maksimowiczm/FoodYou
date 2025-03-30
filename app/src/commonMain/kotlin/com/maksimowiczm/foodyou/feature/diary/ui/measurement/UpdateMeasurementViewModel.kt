package com.maksimowiczm.foodyou.feature.diary.ui.measurement

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maksimowiczm.foodyou.feature.diary.data.model.MeasurementId
import com.maksimowiczm.foodyou.feature.diary.ui.measurement.cases.DeleteProductCase
import com.maksimowiczm.foodyou.feature.diary.ui.measurement.cases.ObserveProductCase
import kotlinx.coroutines.launch

class UpdateMeasurementViewModel(
    observeProductCase: ObserveProductCase,
    private val deleteProductCase: DeleteProductCase,
    private val measurementId: MeasurementId
) : ViewModel() {

    val food = observeProductCase(measurementId)

    fun onDelete() {
        viewModelScope.launch {
            deleteProductCase(measurementId)
        }
    }
}

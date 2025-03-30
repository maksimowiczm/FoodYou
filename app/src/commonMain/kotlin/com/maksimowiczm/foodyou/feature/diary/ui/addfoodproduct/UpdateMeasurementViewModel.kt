package com.maksimowiczm.foodyou.feature.diary.ui.addfoodproduct

import androidx.lifecycle.viewModelScope
import com.maksimowiczm.foodyou.feature.diary.data.model.MeasurementId
import com.maksimowiczm.foodyou.feature.diary.ui.addfoodproduct.cases.DeleteProductCase
import com.maksimowiczm.foodyou.feature.diary.ui.addfoodproduct.cases.ObserveProductCase
import kotlinx.coroutines.launch

class UpdateMeasurementViewModel(
    observeProductCase: ObserveProductCase,
    private val deleteProductCase: DeleteProductCase,
    private val measurementId: MeasurementId
) : MeasurementViewModel() {

    override val product = observeProductCase(measurementId)

    override fun onDelete() {
        viewModelScope.launch {
            deleteProductCase(measurementId)
        }
    }
}

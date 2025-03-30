package com.maksimowiczm.foodyou.feature.diary.ui.addfoodproduct

import androidx.lifecycle.viewModelScope
import com.maksimowiczm.foodyou.feature.diary.ui.addfoodproduct.cases.DeleteProductCase
import com.maksimowiczm.foodyou.feature.diary.ui.addfoodproduct.cases.ObserveProductCase
import kotlinx.coroutines.launch

class CreateMeasurementViewModel(
    observeProductCase: ObserveProductCase,
    private val deleteProductCase: DeleteProductCase,
    private val productId: Long
) : MeasurementViewModel() {

    override val product = observeProductCase(productId)

    override fun onDelete() {
        viewModelScope.launch {
            deleteProductCase(productId)
        }
    }
}

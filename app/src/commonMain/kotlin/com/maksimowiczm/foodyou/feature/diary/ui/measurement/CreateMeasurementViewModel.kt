package com.maksimowiczm.foodyou.feature.diary.ui.measurement

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maksimowiczm.foodyou.feature.diary.data.model.FoodId
import com.maksimowiczm.foodyou.feature.diary.ui.measurement.cases.DeleteProductCase
import com.maksimowiczm.foodyou.feature.diary.ui.measurement.cases.ObserveProductCase
import kotlinx.coroutines.launch

class CreateMeasurementViewModel(
    observeProductCase: ObserveProductCase,
    private val deleteProductCase: DeleteProductCase,
    private val foodId: FoodId
) : ViewModel() {

    val food = observeProductCase(foodId)

    fun onDelete() {
        viewModelScope.launch {
            deleteProductCase(foodId)
        }
    }
}

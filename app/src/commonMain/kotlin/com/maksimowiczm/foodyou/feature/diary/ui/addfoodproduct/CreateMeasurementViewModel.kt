package com.maksimowiczm.foodyou.feature.diary.ui.addfoodproduct

import androidx.lifecycle.viewModelScope
import com.maksimowiczm.foodyou.feature.diary.data.model.FoodId
import com.maksimowiczm.foodyou.feature.diary.ui.addfoodproduct.cases.DeleteProductCase
import com.maksimowiczm.foodyou.feature.diary.ui.addfoodproduct.cases.ObserveProductCase
import kotlinx.coroutines.launch

class CreateMeasurementViewModel(
    observeProductCase: ObserveProductCase,
    private val deleteProductCase: DeleteProductCase,
    private val foodId: FoodId
) : MeasurementViewModel() {

    override val food = observeProductCase(foodId)

    override fun onDelete() {
        viewModelScope.launch {
            deleteProductCase(foodId)
        }
    }
}

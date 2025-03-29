package com.maksimowiczm.foodyou.feature.diary.ui.addfoodproduct

import androidx.lifecycle.viewModelScope
import com.maksimowiczm.foodyou.feature.diary.data.MeasurementRepository
import com.maksimowiczm.foodyou.feature.diary.data.model.FoodId
import com.maksimowiczm.foodyou.feature.diary.data.model.WeightMeasurement
import com.maksimowiczm.foodyou.feature.diary.data.model.WeightMeasurementEnum
import com.maksimowiczm.foodyou.feature.diary.ui.addfoodproduct.cases.DeleteProductCase
import com.maksimowiczm.foodyou.feature.diary.ui.addfoodproduct.cases.ObserveProductCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate

class CreateMeasurementViewModel(
    observeProductCase: ObserveProductCase,
    private val deleteProductCase: DeleteProductCase,
    private val productId: Long,
    private val mealId: Long,
    private val date: LocalDate,
    private val measurementRepository: MeasurementRepository
) : MeasurementViewModel() {

    override val product = observeProductCase(productId)
    private val _done = MutableStateFlow<Boolean>(false)

    /**
     * If true view model won't react to any events
     */
    override val done = _done.asStateFlow()

    private val _processing = MutableStateFlow<WeightMeasurementEnum?>(null)

    override val processing = _processing.asStateFlow()

    private var addJob: Job? = null
    override fun onConfirm(weightMeasurement: WeightMeasurement) {
        // Run it only once
        if (addJob != null) {
            return
        }

        addJob = viewModelScope.launch {
            _processing.value = weightMeasurement.asEnum()
            measurementRepository.addMeasurement(
                date = date,
                mealId = mealId,
                foodId = FoodId.Product(productId),
                weightMeasurement = weightMeasurement
            )
            _done.value = true
        }
    }

    override fun onDelete() {
        viewModelScope.launch {
            deleteProductCase(productId)
            _done.value = true
        }
    }
}

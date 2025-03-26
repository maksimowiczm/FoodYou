package com.maksimowiczm.foodyou.feature.diary.ui.search

import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.maksimowiczm.foodyou.feature.diary.data.AddFoodRepository
import com.maksimowiczm.foodyou.feature.diary.data.MeasurementRepository
import com.maksimowiczm.foodyou.feature.diary.data.model.WeightMeasurement
import com.maksimowiczm.foodyou.feature.diary.domain.QueryProductsUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate

class MealDateSearchViewModel(
    addFoodRepository: AddFoodRepository,
    private val measurementRepository: MeasurementRepository,
    private val queryProductsUseCase: QueryProductsUseCase,
    val mealId: Long,
    val date: LocalDate
) : SearchViewModel(
    addFoodRepository
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    override val pages = mutableSearchQuery.flatMapLatest { query ->
        queryProductsUseCase(
            mealId = mealId,
            date = date,
            query = query
        )
    }.cachedIn(viewModelScope)

    override fun onQuickAdd(productId: Long, measurement: WeightMeasurement) {
        viewModelScope.launch {
            measurementRepository.addMeasurement(
                date = date,
                mealId = mealId,
                productId = productId,
                weightMeasurement = measurement
            )
        }
    }

    override fun onQuickRemove(measurementId: Long) {
        viewModelScope.launch {
            measurementRepository.removeMeasurement(measurementId)
        }
    }
}

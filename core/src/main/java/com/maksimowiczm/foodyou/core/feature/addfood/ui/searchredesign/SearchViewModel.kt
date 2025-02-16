package com.maksimowiczm.foodyou.core.feature.addfood.ui.searchredesign

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.maksimowiczm.foodyou.core.feature.addfood.data.AddFoodRepository
import com.maksimowiczm.foodyou.core.feature.addfood.data.model.ProductWithWeightMeasurement
import com.maksimowiczm.foodyou.core.feature.addfood.data.model.WeightMeasurement
import com.maksimowiczm.foodyou.core.feature.addfood.navigation.AddFoodFeature
import com.maksimowiczm.foodyou.core.feature.product.data.ProductRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate

class SearchViewModel(
    private val addFoodRepository: AddFoodRepository,
    private val productRepository: ProductRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    val mealId: Long
    val date: LocalDate

    init {
        val (epochDay, meal) = savedStateHandle.toRoute<AddFoodFeature>()

        this.mealId = meal
        this.date = LocalDate.fromEpochDays(epochDay)
    }

    val measurements = addFoodRepository.observeWeightMeasurementIds(
        mealId = mealId,
        date = date
    )

    val productIds = productRepository.observePagedProductsIds()

    fun itemViewModel(
        productId: Long,
        measurementId: Long? = null
    ): SearchListItemViewModel {
        return SearchListItemViewModel(
            productId = productId,
            measurementId = measurementId
        )
    }

    inner class SearchListItemViewModel(
        productId: Long,
        measurementId: Long? = null
    ) {
        private val _measurementId = MutableStateFlow(measurementId)

        @OptIn(ExperimentalCoroutinesApi::class)
        val measurement = _measurementId.flatMapLatest {
            if (it != null) {
                addFoodRepository.observeWeightMeasurementById(it)
            } else {
                addFoodRepository.observeWeightMeasurementSuggestionByProductId(productId)
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(2_000),
            initialValue = null
        )

        val isChecked = _measurementId.map { it != null }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(2_000),
            initialValue = measurementId != null
        )

        val product = productRepository.observeProductById(productId).stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(2_000),
            initialValue = null
        )

        fun onCheckChange(isChecked: Boolean) {
            viewModelScope.launch {
                if (isChecked) {
                    val measurement = measurement.value ?: return@launch
                    val productId = product.value?.id ?: return@launch

                    val id = onQuickAdd(productId, measurement)
                    _measurementId.value = id
                } else {
                    _measurementId.value?.let { addFoodRepository.removeFood(it) }
                    _measurementId.value = null
                }
            }
        }

        private suspend fun onQuickAdd(
            productId: Long,
            measurement: WeightMeasurement
        ): Long {
            return addFoodRepository.addFood(
                date = date,
                mealId = mealId,
                productId = productId,
                weightMeasurement = measurement
            )
        }

        private suspend fun onQuickRemove(model: ProductWithWeightMeasurement) {
            model.measurementId?.let { addFoodRepository.removeFood(it) }
        }
    }
}

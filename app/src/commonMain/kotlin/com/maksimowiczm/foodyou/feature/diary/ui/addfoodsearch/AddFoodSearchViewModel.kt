package com.maksimowiczm.foodyou.feature.diary.ui.addfoodsearch

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.maksimowiczm.foodyou.feature.diary.data.MeasurementRepository
import com.maksimowiczm.foodyou.feature.diary.data.model.FoodId
import com.maksimowiczm.foodyou.feature.diary.data.model.MeasurementId
import com.maksimowiczm.foodyou.feature.diary.domain.ObserveProductQueriesUseCase
import com.maksimowiczm.foodyou.feature.diary.ui.addfoodsearch.cases.ObserveAddFoodSearchListItemCase
import com.maksimowiczm.foodyou.feature.diary.ui.addfoodsearch.model.AddFoodSearchListItem
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate

class AddFoodSearchViewModel(
    private val observeUseCase: ObserveAddFoodSearchListItemCase,
    observeProductQueriesUseCase: ObserveProductQueriesUseCase,
    private val measurementRepository: MeasurementRepository,
    private val mealId: Long,
    private val date: LocalDate
) : ViewModel() {
    private val mutableSearchQuery = MutableSharedFlow<String?>(replay = 1).apply { tryEmit(null) }

    val recentQueries = observeProductQueriesUseCase.observeProductQueries(20).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(30_000L),
        initialValue = emptyList()
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    val pages: Flow<PagingData<AddFoodSearchListItem>> = mutableSearchQuery.flatMapLatest { query ->
        observeUseCase(
            query = query,
            mealId = mealId,
            date = date
        )
    }.cachedIn(viewModelScope)

    fun onSearch(query: String?) {
        viewModelScope.launch {
            mutableSearchQuery.emit(query)
        }
    }

    fun onQuickAdd(item: AddFoodSearchListItem) {
        when (item.id) {
            is FoodId.Product -> {
                viewModelScope.launch {
                    measurementRepository.addMeasurement(
                        mealId = mealId,
                        date = date,
                        productId = item.id.productId,
                        weightMeasurement = item.weightMeasurement
                    )
                }
            }
        }
    }

    fun onQuickRemove(measurementId: MeasurementId) {
        when (measurementId) {
            is MeasurementId.Product -> {
                viewModelScope.launch {
                    measurementRepository.removeMeasurement(measurementId)
                }
            }
        }
    }
}

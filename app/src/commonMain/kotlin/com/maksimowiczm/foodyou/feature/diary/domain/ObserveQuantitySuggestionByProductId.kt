package com.maksimowiczm.foodyou.feature.diary.domain

import com.maksimowiczm.foodyou.feature.diary.data.model.WeightMeasurement
import kotlinx.coroutines.flow.Flow

interface ObserveQuantitySuggestionByProductId {
    fun observeQuantitySuggestionByProductId(productId: Long): Flow<List<WeightMeasurement>>
}

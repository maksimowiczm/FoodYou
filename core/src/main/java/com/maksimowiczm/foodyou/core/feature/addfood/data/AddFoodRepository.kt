package com.maksimowiczm.foodyou.core.feature.addfood.data

import com.maksimowiczm.foodyou.core.feature.addfood.data.model.ProductQuery
import com.maksimowiczm.foodyou.core.feature.addfood.data.model.QuantitySuggestion
import com.maksimowiczm.foodyou.core.feature.addfood.data.model.WeightMeasurement
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate

interface AddFoodRepository {
    /**
     * Add a measurement to the diary.
     *
     * @param date The date of the diary entry.
     * @param mealId The meal ID.
     * @param productId The product ID.
     * @param weightMeasurement The weight measurement of the portion.
     * @return The ID of the added measurement.
     */
    suspend fun addMeasurement(
        date: LocalDate,
        mealId: Long,
        productId: Long,
        weightMeasurement: WeightMeasurement
    ): Long

    suspend fun removeMeasurement(portionId: Long)

    fun observeTotalCalories(
        mealId: Long,
        date: LocalDate
    ): Flow<Int>

    fun observeQuantitySuggestionByProductId(productId: Long): Flow<QuantitySuggestion>

    fun observeProductQueries(limit: Int): Flow<List<ProductQuery>>
}

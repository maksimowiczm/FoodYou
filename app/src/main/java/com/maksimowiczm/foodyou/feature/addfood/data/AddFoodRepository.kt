package com.maksimowiczm.foodyou.feature.addfood.data

import androidx.paging.PagingData
import com.maksimowiczm.foodyou.feature.addfood.data.model.ProductQuery
import com.maksimowiczm.foodyou.feature.addfood.data.model.ProductWithWeightMeasurement
import com.maksimowiczm.foodyou.feature.addfood.data.model.QuantitySuggestion
import com.maksimowiczm.foodyou.feature.addfood.data.model.WeightMeasurement
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
    )

    suspend fun removeMeasurement(measurementId: Long)

    suspend fun restoreMeasurement(measurementId: Long)

    suspend fun updateMeasurement(measurementId: Long, weightMeasurement: WeightMeasurement)

    fun queryProducts(
        mealId: Long,
        date: LocalDate,
        query: String?
    ): Flow<PagingData<ProductWithWeightMeasurement>>

    fun observeTotalCalories(mealId: Long, date: LocalDate): Flow<Int>

    fun observeQuantitySuggestionByProductId(productId: Long): Flow<QuantitySuggestion>

    fun observeProductQueries(limit: Int): Flow<List<ProductQuery>>

    fun observeProductByMeasurementId(measurementId: Long): Flow<ProductWithWeightMeasurement?>
}

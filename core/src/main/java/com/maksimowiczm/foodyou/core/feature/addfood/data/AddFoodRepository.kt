package com.maksimowiczm.foodyou.core.feature.addfood.data

import com.maksimowiczm.foodyou.core.feature.addfood.data.model.Meal
import com.maksimowiczm.foodyou.core.feature.addfood.data.model.ProductQuery
import com.maksimowiczm.foodyou.core.feature.addfood.data.model.ProductWithWeightMeasurement
import com.maksimowiczm.foodyou.core.feature.addfood.data.model.QuantitySuggestion
import com.maksimowiczm.foodyou.core.feature.addfood.data.model.WeightMeasurement
import com.maksimowiczm.foodyou.core.feature.addfood.data.model.WeightMeasurementEnum
import com.maksimowiczm.foodyou.core.feature.diary.data.QueryResult
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

interface AddFoodRepository {
    /**
     * Add a measurement to the diary.
     *
     * @param date The date of the diary entry.
     * @param meal The meal of the diary entry.
     * @param productId The product ID.
     * @param weightMeasurement The weight measurement of the portion.
     * @return The ID of the added measurement.
     */
    suspend fun addFood(
        date: LocalDate,
        meal: Meal,
        productId: Long,
        weightMeasurement: WeightMeasurement
    ): Long

    suspend fun addFood(
        date: LocalDate,
        meal: Meal,
        productId: Long,
        weightMeasurement: WeightMeasurementEnum,
        quantity: Float
    ): Long

    suspend fun removeFood(
        portionId: Long
    )

    fun queryProducts(
        meal: Meal,
        date: LocalDate,
        query: String?,
        localOnly: Boolean
    ): Flow<QueryResult<List<ProductWithWeightMeasurement>>>

    fun observeTotalCalories(
        meal: Meal,
        date: LocalDate
    ): Flow<Int>

    suspend fun getQuantitySuggestionByProductId(productId: Long): QuantitySuggestion

    fun observeProductQueries(limit: Int): Flow<List<ProductQuery>>
}

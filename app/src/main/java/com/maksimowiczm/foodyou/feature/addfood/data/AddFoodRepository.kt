package com.maksimowiczm.foodyou.feature.addfood.data

import com.maksimowiczm.foodyou.feature.addfood.data.model.Meal
import com.maksimowiczm.foodyou.feature.addfood.data.model.ProductSearchModel
import com.maksimowiczm.foodyou.feature.addfood.data.model.QuantitySuggestion
import com.maksimowiczm.foodyou.feature.addfood.data.model.WeightMeasurement
import com.maksimowiczm.foodyou.feature.addfood.data.model.WeightMeasurementEnum
import com.maksimowiczm.foodyou.feature.product.data.model.Product
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
        query: String?
    ): Flow<QueryResult<List<ProductSearchModel>>>

    suspend fun getQuantitySuggestionByProductId(productId: Long): QuantitySuggestion
}

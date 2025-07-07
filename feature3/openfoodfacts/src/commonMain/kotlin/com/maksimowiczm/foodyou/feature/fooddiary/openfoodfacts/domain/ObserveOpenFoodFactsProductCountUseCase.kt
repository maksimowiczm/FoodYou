package com.maksimowiczm.foodyou.feature.fooddiary.openfoodfacts.domain

import com.maksimowiczm.foodyou.feature.fooddiary.openfoodfacts.data.OpenFoodFactsDatabase
import kotlinx.coroutines.flow.Flow

class ObserveOpenFoodFactsProductCountUseCase(database: OpenFoodFactsDatabase) {
    private val dao = database.openFoodFactsDao

    operator fun invoke(query: String?): Flow<Int> {
        val isBarcode = query?.all { it.isDigit() } ?: false

        return if (isBarcode) {
            dao.observeProductsCountByBarcode(query)
        } else {
            // Split queries into 3 words
            val words = query?.split(" ") ?: emptyList()
            val query1 = words.getOrNull(0)
            val query2 = words.getOrNull(1)
            val query3 = if (words.size > 2) {
                words.drop(2).joinToString(" ")
            } else {
                null
            }

            dao.observeProductsCount(
                query1 = query1,
                query2 = query2,
                query3 = query3
            )
        }
    }
}

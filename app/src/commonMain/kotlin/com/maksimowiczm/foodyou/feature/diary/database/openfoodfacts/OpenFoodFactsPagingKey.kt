package com.maksimowiczm.foodyou.feature.diary.database.openfoodfacts

import androidx.room.Entity

/**
 * Helps to keep track of the current page and total count of products for a given query. Allows to
 * resume the search from the last page and avoid unnecessary network calls.
 */
@Entity(
    primaryKeys = [
        "queryString",
        "country"
    ]
)
data class OpenFoodFactsPagingKey(
    val queryString: String,
    val country: String,
    val fetchedCount: Int,
    val totalCount: Int
)

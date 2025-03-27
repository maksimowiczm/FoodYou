package com.maksimowiczm.foodyou.feature.search.database

import com.maksimowiczm.foodyou.feature.search.database.dao.OpenFoodFactsDao
import com.maksimowiczm.foodyou.feature.search.database.dao.ProductDao

interface SearchDatabase {
    fun productDao(): ProductDao
    fun openFoodFactsPagingKeyDao(): OpenFoodFactsDao
}

val SearchDatabase.productDao
    get() = productDao()

val SearchDatabase.openFoodFactsPagingKeyDao
    get() = openFoodFactsPagingKeyDao()

package com.maksimowiczm.foodyou.feature.search.database

import com.maksimowiczm.foodyou.feature.search.database.dao.OpenFoodFactsDao
import com.maksimowiczm.foodyou.feature.search.database.dao.ProductDao
import com.maksimowiczm.foodyou.feature.search.database.dao.SearchDao

interface SearchDatabase {
    fun productDao(): ProductDao
    val productDao: ProductDao
        get() = productDao()

    fun openFoodFactsPagingKeyDao(): OpenFoodFactsDao
    val openFoodFactsPagingKeyDao: OpenFoodFactsDao
        get() = openFoodFactsPagingKeyDao()

    fun searchDao(): SearchDao
    val searchDao: SearchDao
        get() = searchDao()
}

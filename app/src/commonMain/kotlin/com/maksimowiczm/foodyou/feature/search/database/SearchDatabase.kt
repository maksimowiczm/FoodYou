package com.maksimowiczm.foodyou.feature.search.database

import com.maksimowiczm.foodyou.feature.search.database.dao.OpenFoodFactsDao
import com.maksimowiczm.foodyou.feature.search.database.dao.ProductDao
import com.maksimowiczm.foodyou.feature.search.database.dao.SearchDao

interface SearchDatabase {
    fun productDao(): ProductDao
    val productDao: ProductDao
        get() = productDao()

    fun openFoodFactsDao(): OpenFoodFactsDao
    val openFoodFactsDao: OpenFoodFactsDao
        get() = openFoodFactsDao()

    fun searchDao(): SearchDao
    val searchDao: SearchDao
        get() = searchDao()
}

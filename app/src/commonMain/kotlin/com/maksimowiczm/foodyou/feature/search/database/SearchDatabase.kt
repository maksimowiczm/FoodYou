package com.maksimowiczm.foodyou.feature.search.database

import com.maksimowiczm.foodyou.feature.search.database.dao.OpenFoodFactsDao
import com.maksimowiczm.foodyou.feature.search.database.dao.ProductDao
import com.maksimowiczm.foodyou.feature.search.database.dao.SearchDao

interface SearchDatabase {
    val productDao: ProductDao
    val openFoodFactsDao: OpenFoodFactsDao
    val searchDao: SearchDao
}

package com.maksimowiczm.foodyou.feature.diary.database

import com.maksimowiczm.foodyou.feature.diary.database.dao.AddFoodDao
import com.maksimowiczm.foodyou.feature.diary.database.dao.OpenFoodFactsDao
import com.maksimowiczm.foodyou.feature.diary.database.dao.ProductDao
import com.maksimowiczm.foodyou.feature.diary.database.measurement.MeasurementDao
import com.maksimowiczm.foodyou.feature.diary.database.search.SearchDao

interface DiaryDatabase {
    fun addFoodDao(): AddFoodDao
    fun productDao(): ProductDao
    fun openFoodFactsDao(): OpenFoodFactsDao

    val searchDao: SearchDao
    val measurementDao: MeasurementDao
}

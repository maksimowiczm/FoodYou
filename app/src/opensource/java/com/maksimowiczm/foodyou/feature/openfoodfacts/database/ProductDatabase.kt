package com.maksimowiczm.foodyou.feature.openfoodfacts.database

interface ProductDatabase {
    fun productDao(): ProductDao
}

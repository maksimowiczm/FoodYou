package com.maksimowiczm.foodyou.feature.openfoodfacts.database

interface OpenFoodFactsDatabase {
    fun openFoodFactsDao(): OpenFoodFactsDao
}

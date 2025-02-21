package com.maksimowiczm.foodyou.core.feature.openfoodfacts.database

interface OpenFoodFactsDatabase {
    fun openFoodFactsDao(): OpenFoodFactsDao
}

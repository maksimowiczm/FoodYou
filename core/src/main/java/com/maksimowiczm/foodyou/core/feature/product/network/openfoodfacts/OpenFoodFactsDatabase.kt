package com.maksimowiczm.foodyou.core.feature.product.network.openfoodfacts

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.maksimowiczm.foodyou.core.feature.product.data.ProductPreferences
import com.maksimowiczm.foodyou.core.feature.product.data.model.toEntity
import com.maksimowiczm.foodyou.core.feature.product.database.ProductDao
import com.maksimowiczm.foodyou.core.feature.product.database.ProductDatabase
import com.maksimowiczm.foodyou.core.feature.product.network.RemoteProductDatabase
import com.maksimowiczm.foodyou.core.infrastructure.datastore.get

internal class OpenFoodFactsDatabase(
    private val dataStore: DataStore<Preferences>,
    productDatabase: ProductDatabase,
    private val openFoodFactsNetworkDataSource: OpenFoodFactsNetworkDataSource
) : RemoteProductDatabase {
    private val productDao: ProductDao = productDatabase.productDao()

    override suspend fun queryAndInsertByName(query: String?) {
        // Don't query if query is null
        if (query == null) {
            return
        }

        // Get open food facts settings
        if (dataStore.get(ProductPreferences.openFoodFactsEnabled) != true) {
            Log.d(TAG, "OpenFoodFacts is disabled")
            return
        }

        val country = dataStore.get(ProductPreferences.openFoodCountryCode)
            ?: error("Country code is not set")

        val openFoodResponse = openFoodFactsNetworkDataSource.queryProducts(
            query = query,
            country = country,
            page = 1,
            pageSize = PAGE_SIZE
        )

        val openFoodProducts = openFoodResponse.products.mapNotNull { product ->
            product.toEntity().also { entity ->
                if (entity == null) {
                    Log.w(
                        TAG,
                        "Failed to convert product: (name=${product.productName}, code=${product.code})"
                    )
                }
            }
        }

        productDao.insertOpenFoodFactsProducts(openFoodProducts)
    }

    override suspend fun queryAndInsertByBarcode(barcode: String) {
        // Get open food facts settings
        if (dataStore.get(ProductPreferences.openFoodFactsEnabled) != true) {
            error("OpenFoodFacts is disabled")
        }

        val country = dataStore.get(ProductPreferences.openFoodCountryCode)
            ?: error("Country code is not set")

        val openFoodProduct = openFoodFactsNetworkDataSource.getProduct(
            code = barcode,
            country = country
        )

        if (openFoodProduct == null) {
            Log.d(
                TAG,
                "Product not found: (barcode=$barcode)"
            )

            return
        }

        val product = openFoodProduct.toEntity() ?: run {
            Log.w(
                TAG,
                "Failed to convert product: (name=${openFoodProduct.productName}, code=${openFoodProduct.code})"
            )

            return
        }

        productDao.insertOpenFoodFactsProducts(listOf(product))
    }

    companion object {
        private const val TAG = "OpenFoodFactsDatabase"
        private const val PAGE_SIZE = 30
    }
}

package com.maksimowiczm.foodyou.core.feature.product.network.openfoodfacts

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.maksimowiczm.foodyou.core.feature.product.data.ProductPreferences
import com.maksimowiczm.foodyou.core.feature.product.data.model.toEntity
import com.maksimowiczm.foodyou.core.feature.product.database.ProductDatabase
import com.maksimowiczm.foodyou.core.feature.product.network.ProductsRemoteDatabase
import com.maksimowiczm.foodyou.core.infrastructure.datastore.get
import kotlinx.coroutines.runBlocking

internal class OpenFoodFactsRemoteDatabase(
    private val dataStore: DataStore<Preferences>,
    productDatabase: ProductDatabase
) : ProductsRemoteDatabase {
    private val productDao = productDatabase.productDao()

    private val _openFoodFactsNetworkDataSource by lazy {
        OpenFoodFactsNetworkDataSource()
    }

    private val openFoodFactsNetworkDataSource: OpenFoodFactsNetworkDataSource?
        get() {
            val isEnabled =
                runBlocking { dataStore.get(ProductPreferences.openFoodFactsEnabled) }

            return if (isEnabled != true) {
                Log.w(TAG, "Open Food Facts is not enabled")
                null
            } else {
                _openFoodFactsNetworkDataSource
            }
        }

    private val countryCode
        get() = runBlocking { dataStore.get(ProductPreferences.openFoodFactsCountryCode) }

    override suspend fun queryAndInsertByName(query: String?, limit: Int) {
        val openFoodFactsNetworkDataSource = openFoodFactsNetworkDataSource ?: return

        val country = countryCode
        if (country == null) {
            Log.e(TAG, "Country code is not set")
            return
        }

        if (query == null) {
            Log.d(TAG, "Empty query is not supported")
            return
        }

        val response = openFoodFactsNetworkDataSource.queryProducts(
            query = query,
            country = country,
            page = 1,
            pageSize = limit
        )

        val entities = response.products.mapNotNull {
            it.toEntity().also { e ->
                if (e == null) {
                    Log.w(TAG, "Failed to convert product to entity: $it")
                }
            }
        }

        productDao.insertOpenFoodFactsProducts(entities)
    }

    override suspend fun queryAndInsertByBarcode(barcode: String) {
        val openFoodFactsNetworkDataSource = openFoodFactsNetworkDataSource ?: return

        val country = countryCode
        if (country == null) {
            Log.e(TAG, "Country code is not set")
            return
        }

        val product = openFoodFactsNetworkDataSource.getProduct(barcode, country)
        if (product == null) {
            Log.d(TAG, "Product not found: $barcode")
            return
        }

        val entity = product.toEntity()
        if (entity == null) {
            Log.w(TAG, "Failed to convert product to entity: $product")
            return
        }

        productDao.insertOpenFoodFactsProducts(listOf(entity))
    }

    private companion object {
        const val TAG = "OpenFoodFactsRemoteDatabase"
    }
}

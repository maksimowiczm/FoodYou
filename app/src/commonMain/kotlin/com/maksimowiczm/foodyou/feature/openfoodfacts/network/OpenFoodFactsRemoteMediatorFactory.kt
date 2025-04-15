package com.maksimowiczm.foodyou.feature.openfoodfacts.network

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.paging.ExperimentalPagingApi
import co.touchlab.kermit.Logger
import com.maksimowiczm.foodyou.core.data.database.FoodYouDatabase
import com.maksimowiczm.foodyou.core.data.database.openfoodfacts.OpenFoodFactsDao
import com.maksimowiczm.foodyou.core.data.database.product.ProductDao
import com.maksimowiczm.foodyou.core.domain.source.ProductNetworkDataSource
import com.maksimowiczm.foodyou.core.domain.source.ProductRemoteMediator
import com.maksimowiczm.foodyou.core.ext.get
import com.maksimowiczm.foodyou.feature.openfoodfacts.data.OpenFoodFactsPreferences
import kotlinx.coroutines.runBlocking

@OptIn(ExperimentalPagingApi::class)
internal class OpenFoodFactsRemoteMediatorFactory(
    private val dataStore: DataStore<Preferences>,
    database: FoodYouDatabase
) : ProductNetworkDataSource {

    private val openFoodFactsDao: OpenFoodFactsDao = database.openFoodFactsDao
    private val productDao: ProductDao = database.productDao

    private val _openFoodFactsNetworkDataSource by lazy {
        OpenFoodFactsNetworkDataSource()
    }

    private val openFoodFactsNetworkDataSource: OpenFoodFactsNetworkDataSource?
        get() {
            val isEnabled =
                runBlocking { dataStore.get(OpenFoodFactsPreferences.isEnabled) }

            return if (isEnabled != true) {
                Logger.w(TAG) { "Open Food Facts is not enabled" }
                null
            } else {
                _openFoodFactsNetworkDataSource
            }
        }

    private val countryCode
        get() = runBlocking { dataStore.get(OpenFoodFactsPreferences.countryCode) }

    override fun <T : Any> createRemoteMediatorWithQuery(
        query: String?
    ): ProductRemoteMediator<T>? {
        val openFoodFactsNetworkDataSource = openFoodFactsNetworkDataSource ?: return null

        if (query == null) {
            Logger.d(TAG) { "Empty query is not supported" }
            return null
        }

        val country = countryCode
        if (country == null) {
            Logger.w(TAG) { "Country code is not set" }
        }

        return OpenFoodFactsRemoteMediator(
            isBarcode = false,
            query = query,
            country = countryCode,
            openFoodFactsDao = openFoodFactsDao,
            productDao = productDao,
            openFoodFactsNetworkDataSource = openFoodFactsNetworkDataSource
        )
    }

    override fun <T : Any> createRemoteMediatorWithBarcode(
        barcode: String
    ): ProductRemoteMediator<T>? {
        val openFoodFactsNetworkDataSource = openFoodFactsNetworkDataSource ?: return null

        val country = countryCode
        if (country == null) {
            Logger.w(TAG) { "Country code is not set" }
        }

        return OpenFoodFactsRemoteMediator(
            isBarcode = true,
            query = barcode,
            country = countryCode,
            openFoodFactsDao = openFoodFactsDao,
            productDao = productDao,
            openFoodFactsNetworkDataSource = openFoodFactsNetworkDataSource
        )
    }

    private companion object {
        private const val TAG = "OpenFoodFactsRemoteMediatorFactory"
    }
}

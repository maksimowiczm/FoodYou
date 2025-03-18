package com.maksimowiczm.foodyou.network

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.paging.ExperimentalPagingApi
import co.touchlab.kermit.Logger
import com.maksimowiczm.foodyou.data.preferences.OpenFoodFactsPreferences
import com.maksimowiczm.foodyou.database.dao.OpenFoodFactsDao
import com.maksimowiczm.foodyou.database.dao.ProductDao
import com.maksimowiczm.foodyou.infrastructure.datastore.get
import kotlinx.coroutines.runBlocking

// Should be used as singleton to avoid creating multiple instances of
// OpenFoodFactsNetworkDataSource because it wraps retrofit client.
@OptIn(ExperimentalPagingApi::class)
class OpenFoodFactsRemoteMediatorFactory(
    private val dataStore: DataStore<Preferences>,
    private val openFoodFactsDao: OpenFoodFactsDao,
    private val productDao: ProductDao
) : ProductRemoteMediatorFactory {

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

    override fun createWithQuery(query: String?): ProductRemoteMediator? {
        val openFoodFactsNetworkDataSource = openFoodFactsNetworkDataSource ?: return null

        val country = countryCode
        if (country == null) {
            Logger.e(TAG) { "Country code is not set" }
            return null
        }

        if (query == null) {
            Logger.d(TAG) { "Empty query is not supported" }
            return null
        }

        return OpenFoodFactsRemoteMediator(
            isBarcode = false,
            query = query,
            country = country,
            openFoodFactsDao = openFoodFactsDao,
            productDao = productDao,
            openFoodFactsNetworkDataSource = openFoodFactsNetworkDataSource
        )
    }

    override fun createWithBarcode(barcode: String): ProductRemoteMediator? {
        val openFoodFactsNetworkDataSource = openFoodFactsNetworkDataSource ?: return null

        val country = countryCode
        if (country == null) {
            Logger.e(TAG) { "Country code is not set" }
            return null
        }

        return OpenFoodFactsRemoteMediator(
            isBarcode = true,
            query = barcode,
            country = country,
            openFoodFactsDao = openFoodFactsDao,
            productDao = productDao,
            openFoodFactsNetworkDataSource = openFoodFactsNetworkDataSource
        )
    }

    private companion object {
        private const val TAG = "OpenFoodFactsRemoteMediatorFactory"
    }
}

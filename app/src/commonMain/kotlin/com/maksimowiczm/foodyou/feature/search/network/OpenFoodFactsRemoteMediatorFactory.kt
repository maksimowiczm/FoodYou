package com.maksimowiczm.foodyou.feature.search.network

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.paging.ExperimentalPagingApi
import co.touchlab.kermit.Logger
import com.maksimowiczm.foodyou.feature.search.data.preferences.OpenFoodFactsPreferences
import com.maksimowiczm.foodyou.feature.search.database.SearchDatabase
import com.maksimowiczm.foodyou.infrastructure.datastore.get
import kotlinx.coroutines.runBlocking

// Should be used as singleton to avoid creating multiple instances of
// OpenFoodFactsNetworkDataSource because it wraps retrofit client.
@OptIn(ExperimentalPagingApi::class)
internal class OpenFoodFactsRemoteMediatorFactory(
    private val dataStore: DataStore<Preferences>,
    searchDatabase: SearchDatabase
) : ProductRemoteMediatorFactory {

    private val openFoodFactsDao = searchDatabase.openFoodFactsPagingKeyDao
    private val productDao = searchDatabase.productDao

    private val _openFoodFactsNetworkDataSource by lazy {
        OpenFoodFactsNetworkDataSource()
    }

    private val openFoodFactsNetworkDataSource: OpenFoodFactsNetworkDataSource?
        get() {
            val isEnabled = runBlocking { dataStore.get(OpenFoodFactsPreferences.isEnabled) }

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

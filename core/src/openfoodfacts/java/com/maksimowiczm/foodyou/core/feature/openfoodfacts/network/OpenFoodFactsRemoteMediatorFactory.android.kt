package com.maksimowiczm.foodyou.core.feature.openfoodfacts.network

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.paging.ExperimentalPagingApi
import com.maksimowiczm.foodyou.core.feature.openfoodfacts.data.OpenFoodFactsPreferences
import com.maksimowiczm.foodyou.core.feature.openfoodfacts.database.OpenFoodFactsDatabase
import com.maksimowiczm.foodyou.core.feature.product.network.ProductRemoteMediator
import com.maksimowiczm.foodyou.core.feature.product.network.ProductRemoteMediatorFactory
import com.maksimowiczm.foodyou.core.infrastructure.datastore.get
import kotlinx.coroutines.runBlocking

// Should be used as singleton to avoid creating multiple instances of
// OpenFoodFactsNetworkDataSource because it wraps retrofit client.
@OptIn(ExperimentalPagingApi::class)
class OpenFoodFactsRemoteMediatorFactory(
    private val dataStore: DataStore<Preferences>,
    openFoodFactsDatabase: OpenFoodFactsDatabase
) : ProductRemoteMediatorFactory {
    private val openFoodFactsDao = openFoodFactsDatabase.openFoodFactsDao()

    private val _openFoodFactsNetworkDataSource by lazy {
        OpenFoodFactsNetworkDataSource()
    }

    private val openFoodFactsNetworkDataSource: OpenFoodFactsNetworkDataSource?
        get() {
            val isEnabled =
                runBlocking { dataStore.get(OpenFoodFactsPreferences.isEnabled) }

            return if (isEnabled != true) {
                Log.w(TAG, "Open Food Facts is not enabled")
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
            Log.e(TAG, "Country code is not set")
            return null
        }

        if (query == null) {
            Log.d(TAG, "Empty query is not supported")
            return null
        }

        return OpenFoodFactsRemoteMediator(
            isBarcode = false,
            query = query,
            country = country,
            openFoodFactsDao = openFoodFactsDao,
            openFoodFactsNetworkDataSource = openFoodFactsNetworkDataSource
        )
    }

    override fun createWithBarcode(barcode: String): ProductRemoteMediator? {
        val openFoodFactsNetworkDataSource = openFoodFactsNetworkDataSource ?: return null

        val country = countryCode
        if (country == null) {
            Log.e(TAG, "Country code is not set")
            return null
        }

        return OpenFoodFactsRemoteMediator(
            isBarcode = true,
            query = barcode,
            country = country,
            openFoodFactsDao = openFoodFactsDao,
            openFoodFactsNetworkDataSource = openFoodFactsNetworkDataSource
        )
    }

    private companion object {
        private const val TAG = "OpenFoodFactsRemoteMediatorFactory"
    }
}

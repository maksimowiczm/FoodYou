package com.maksimowiczm.foodyou.core.feature.product.network.openfoodfacts

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import com.maksimowiczm.foodyou.core.feature.product.data.ProductPreferences
import com.maksimowiczm.foodyou.core.feature.product.data.model.toEntity
import com.maksimowiczm.foodyou.core.feature.product.database.ProductDao
import com.maksimowiczm.foodyou.core.feature.product.database.ProductDatabase
import com.maksimowiczm.foodyou.core.feature.product.database.ProductEntity
import com.maksimowiczm.foodyou.core.feature.product.network.ProductRemoteMediator
import com.maksimowiczm.foodyou.core.feature.product.network.ProductRemoteMediatorFactory
import com.maksimowiczm.foodyou.core.infrastructure.datastore.get
import kotlinx.coroutines.runBlocking

@OptIn(ExperimentalPagingApi::class)
internal class OpenFoodFactsRemoteMediator(
    private val query: String,
    private val country: String,
    private val productDao: ProductDao,
    private val openFoodFactsNetworkDataSource: OpenFoodFactsNetworkDataSource
) : ProductRemoteMediator() {
    override suspend fun initialize(): InitializeAction {
        return InitializeAction.LAUNCH_INITIAL_REFRESH
    }

    private var page = 1

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, ProductEntity>
    ): MediatorResult {
        try {
            when (loadType) {
                LoadType.REFRESH -> {
                    page = 1
                }

                LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
                LoadType.APPEND -> {
                    page++
                }
            }

            Log.d(TAG, "Loading page $page")

            val page = openFoodFactsNetworkDataSource.queryProducts(
                query = query,
                country = country,
                page = page,
                pageSize = state.config.pageSize
            )

            val products = page.products.mapNotNull { remoteProduct ->
                remoteProduct.toEntity().also {
                    if (it == null) {
                        Log.w(
                            TAG,
                            "Failed to convert product: (name=${remoteProduct.productName}, code=${remoteProduct.code})"
                        )
                    }
                }
            }

            productDao.insertOpenFoodFactsProducts(products)

            val endOfPaginationReached = products.size < state.config.pageSize

            return MediatorResult.Success(endOfPaginationReached)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to load page", e)
            return MediatorResult.Error(e)
        }
    }

    private companion object {
        private const val TAG = "ProductRemoteMediator"
    }

    class FactoryProduct(
        private val dataStore: DataStore<Preferences>,
        productDatabase: ProductDatabase
    ) : ProductRemoteMediatorFactory {
        private val productDao = productDatabase.productDao()

        override fun create(query: String?): ProductRemoteMediator? {
            val isEnabled =
                runBlocking { dataStore.get(ProductPreferences.openFoodFactsEnabled) }

            if (isEnabled != true) {
                Log.w(TAG, "Open Food Facts is not enabled")
                return null
            }

            val country =
                runBlocking { dataStore.get(ProductPreferences.openFoodFactsCountryCode) }

            if (country == null) {
                Log.e(TAG, "Country code is not set")
                return null
            }

            if (query == null) {
                Log.d(TAG, "Empty query is not supported")
                return null
            }

            return OpenFoodFactsRemoteMediator(
                query = query,
                country = country,
                productDao = productDao,
                openFoodFactsNetworkDataSource = OpenFoodFactsNetworkDataSource()
            )
        }

        private companion object {
            private const val TAG = "ProductRemoteMediator.Factory"
        }
    }
}

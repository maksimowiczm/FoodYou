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
    private val isBarcode: Boolean,
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
            page = when (loadType) {
                LoadType.REFRESH -> 1
                LoadType.PREPEND -> {
                    return MediatorResult.Success(endOfPaginationReached = true)
                }

                LoadType.APPEND -> {
                    val lastItem = state.lastItemOrNull()

                    if (lastItem == null) {
                        Log.d(TAG, "Last item is null")
                        return MediatorResult.Success(endOfPaginationReached = true)
                    }

                    if (isBarcode) {
                        return MediatorResult.Success(endOfPaginationReached = true)
                    }

                    // TODO
                    //  Make it work because it won't return valid page because invalid open food
                    //  facts products are skipped

                    val count = productDao.getProductsCountByQuery(query)
                    val nextPage = count / state.config.pageSize + 2
                    nextPage
                }
            }

            Log.d(TAG, "Loading page $page")

            val page = if (isBarcode) {
                listOfNotNull(
                    openFoodFactsNetworkDataSource.getProduct(
                        code = query,
                        country = country
                    )
                )
            } else {
                openFoodFactsNetworkDataSource.queryProducts(
                    query = query,
                    country = country,
                    page = page,
                    pageSize = state.config.pageSize
                ).products
            }

            val products = page.map { remoteProduct ->
                remoteProduct.toEntity().also {
                    if (it == null) {
                        Log.w(
                            TAG,
                            "Failed to convert product: (name=${remoteProduct.productName}, code=${remoteProduct.code})"
                        )
                    }
                }
            }

            productDao.insertOpenFoodFactsProducts(products.filterNotNull())

            val skipped = products.count { it == null }

            val endOfPaginationReached = (products.size + skipped) < state.config.pageSize

            return MediatorResult.Success(endOfPaginationReached)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to load page", e)
            return MediatorResult.Error(e)
        }
    }

    private companion object {
        private const val TAG = "OpenFoodFactsRemoteMediator"
    }

    // Should be used as singleton to avoid creating multiple instances of
    // OpenFoodFactsNetworkDataSource because it wraps retrofit client.
    class Factory(
        private val dataStore: DataStore<Preferences>,
        productDatabase: ProductDatabase
    ) : ProductRemoteMediatorFactory {
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
                productDao = productDao,
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
                productDao = productDao,
                openFoodFactsNetworkDataSource = openFoodFactsNetworkDataSource
            )
        }

        private companion object {
            private const val TAG = "ProductRemoteMediator.Factory"
        }
    }
}

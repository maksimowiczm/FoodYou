package com.maksimowiczm.foodyou.openfoodfacts.infrastructure

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadState.NotLoading
import androidx.paging.LoadStates
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import co.touchlab.kermit.Logger
import com.maksimowiczm.foodyou.common.Err
import com.maksimowiczm.foodyou.common.Ok
import com.maksimowiczm.foodyou.common.Result
import com.maksimowiczm.foodyou.common.domain.RemoteData
import com.maksimowiczm.foodyou.foodsearch.domain.FoodSearchPreferencesRepository
import com.maksimowiczm.foodyou.foodsearch.domain.SearchQuery
import com.maksimowiczm.foodyou.openfoodfacts.domain.OpenFoodFactsApiError
import com.maksimowiczm.foodyou.openfoodfacts.domain.OpenFoodFactsProduct
import com.maksimowiczm.foodyou.openfoodfacts.domain.OpenFoodFactsProductIdentity
import com.maksimowiczm.foodyou.openfoodfacts.domain.OpenFoodFactsRepository
import com.maksimowiczm.foodyou.openfoodfacts.domain.OpenFoodFactsSearchParameters
import com.maksimowiczm.foodyou.openfoodfacts.infrastructure.network.OpenFoodFactsRemoteDataSource
import com.maksimowiczm.foodyou.openfoodfacts.infrastructure.room.OpenFoodFactsDao
import com.maksimowiczm.foodyou.openfoodfacts.infrastructure.room.OpenFoodFactsDatabase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

internal class OpenFoodFactsRepositoryImpl(
    private val networkDataSource: OpenFoodFactsRemoteDataSource,
    private val searchPreferencesRepository: FoodSearchPreferencesRepository,
    private val database: OpenFoodFactsDatabase,
    private val dao: OpenFoodFactsDao,
    private val logger: Logger,
) : OpenFoodFactsRepository {
    private val mapper = OpenFoodFactsProductMapper()

    @OptIn(ExperimentalPagingApi::class)
    override fun search(
        parameters: OpenFoodFactsSearchParameters,
        pageSize: Int,
    ): Flow<PagingData<OpenFoodFactsProduct>> {
        val config = PagingConfig(pageSize = pageSize)

        if (parameters.query is SearchQuery.FoodDataCentralUrl) {
            return flowOf(
                PagingData.empty(
                    sourceLoadStates =
                        LoadStates(NotLoading(true), NotLoading(true), NotLoading(true))
                )
            )
        }

        val factory = {
            when (parameters.query) {
                SearchQuery.Blank -> dao.getPagingSource()
                is SearchQuery.Barcode -> dao.getPagingSourceByBarcode(parameters.query.barcode)

                is SearchQuery.Text -> dao.getPagingSourceByQuery(parameters.query.query)
                is SearchQuery.OpenFoodFactsUrl ->
                    dao.getPagingSourceByBarcode(parameters.query.barcode)

                is SearchQuery.FoodDataCentralUrl -> error("Unreachable")
            }
        }

        return searchPreferencesRepository.observe().flatMapLatest { prefs ->
            val remoteMediator =
                if (prefs.allowOpenFoodFacts && parameters.query is SearchQuery.NotBlank) {
                    OpenFoodFactsRemoteMediator(
                        query = parameters.query,
                        database = database,
                        remote = networkDataSource,
                        logger = logger,
                    )
                } else null

            Pager(config = config, pagingSourceFactory = factory, remoteMediator = remoteMediator)
                .flow
                .map { data -> data.map(mapper::openFoodFactsProduct) }
        }
    }

    override fun count(parameters: OpenFoodFactsSearchParameters): Flow<Int> {
        return when (parameters.query) {
            is SearchQuery.Blank -> dao.observeCount()
            is SearchQuery.Barcode -> dao.observeCountByBarcode(parameters.query.barcode)
            is SearchQuery.Text -> dao.observeCountByQuery(parameters.query.query)
            is SearchQuery.OpenFoodFactsUrl -> dao.observeCountByBarcode(parameters.query.barcode)
            is SearchQuery.FoodDataCentralUrl -> flowOf(0)
        }
    }

    override fun observe(
        identity: OpenFoodFactsProductIdentity
    ): Flow<RemoteData<OpenFoodFactsProduct>> = channelFlow {
        send(RemoteData.Loading(null))

        val barcode = identity.barcode

        val localProduct = dao.observe(barcode).first()

        if (localProduct == null) {
            try {
                val openFoodFactsProduct = networkDataSource.getProduct(barcode).getOrThrow()
                val entity = mapper.openFoodFactsProductEntity(openFoodFactsProduct)
                dao.upsertProduct(entity)
                send(RemoteData.Success(mapper.openFoodFactsProduct(entity)))
            } catch (e: OpenFoodFactsApiError) {
                when (e) {
                    is OpenFoodFactsApiError.ProductNotFound -> send(RemoteData.NotFound)

                    else -> send(RemoteData.Error(e, null))
                }
            }
        } else {
            send(RemoteData.Success(mapper.openFoodFactsProduct(localProduct)))
        }

        dao.observe(barcode).drop(1).collectLatest {
            when (it) {
                null -> send(RemoteData.NotFound)
                else -> send(RemoteData.Success(mapper.openFoodFactsProduct(it)))
            }
        }
    }

    override suspend fun refresh(
        identity: OpenFoodFactsProductIdentity
    ): Result<OpenFoodFactsProduct, OpenFoodFactsApiError> {
        val barcode = identity.barcode

        return try {
            val remote = networkDataSource.getProduct(barcode).getOrThrow()
            val entity = mapper.openFoodFactsProductEntity(remote)
            dao.upsertProduct(entity)
            Ok(mapper.openFoodFactsProduct(entity))
        } catch (e: OpenFoodFactsApiError) {
            Err(e)
        }
    }
}

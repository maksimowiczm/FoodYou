package com.maksimowiczm.foodyou.fooddatacentral.infrastructure

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
import com.maksimowiczm.foodyou.fooddatacentral.domain.FoodDataCentralApiError
import com.maksimowiczm.foodyou.fooddatacentral.domain.FoodDataCentralProduct
import com.maksimowiczm.foodyou.fooddatacentral.domain.FoodDataCentralProductIdentity
import com.maksimowiczm.foodyou.fooddatacentral.domain.FoodDataCentralRepository
import com.maksimowiczm.foodyou.fooddatacentral.domain.FoodDataCentralSearchParameters
import com.maksimowiczm.foodyou.fooddatacentral.domain.FoodDataCentralSettingsRepository
import com.maksimowiczm.foodyou.fooddatacentral.infrastructure.network.FoodDataCentralRemoteDataSource
import com.maksimowiczm.foodyou.fooddatacentral.infrastructure.room.FoodDataCentralDao
import com.maksimowiczm.foodyou.fooddatacentral.infrastructure.room.FoodDataCentralDatabase
import com.maksimowiczm.foodyou.foodsearch.domain.FoodSearchPreferencesRepository
import com.maksimowiczm.foodyou.foodsearch.domain.SearchQuery
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

internal class FoodDataCentralRepositoryImpl(
    private val networkDataSource: FoodDataCentralRemoteDataSource,
    private val searchPreferencesRepository: FoodSearchPreferencesRepository,
    private val settingsRepository: FoodDataCentralSettingsRepository,
    private val database: FoodDataCentralDatabase,
    private val dao: FoodDataCentralDao,
    private val logger: Logger,
) : FoodDataCentralRepository {
    private val mapper = FoodDataCentralProductMapper()

    @OptIn(ExperimentalPagingApi::class)
    override fun search(
        parameters: FoodDataCentralSearchParameters,
        pageSize: Int,
    ): Flow<PagingData<FoodDataCentralProduct>> {
        val config = PagingConfig(pageSize = pageSize)

        if (parameters.query is SearchQuery.OpenFoodFactsUrl) {
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
                is SearchQuery.OpenFoodFactsUrl -> error("Unreachable")
                is SearchQuery.FoodDataCentralUrl ->
                    dao.getPagingSourceByFdcId(parameters.query.fdcId)
            }
        }

        return searchPreferencesRepository.observe().flatMapLatest { prefs ->
            val remoteMediator =
                if (prefs.allowFoodDataCentralUSDA && parameters.query is SearchQuery.NotBlank) {
                    FoodDataCentralRemoteMediator(
                        query = parameters.query,
                        database = database,
                        remote = networkDataSource,
                        apiKey = settingsRepository.load().apiKey,
                        logger = logger,
                    )
                } else null

            Pager(config = config, remoteMediator = remoteMediator, pagingSourceFactory = factory)
                .flow
                .map { pagingData ->
                    pagingData.map { entity -> mapper.foodDataCentralProduct(entity) }
                }
        }
    }

    override fun count(parameters: FoodDataCentralSearchParameters): Flow<Int> {
        return when (parameters.query) {
            is SearchQuery.Blank -> dao.observeCount()
            is SearchQuery.Barcode -> dao.observeCountByBarcode(parameters.query.barcode)
            is SearchQuery.Text -> dao.observeCountByQuery(parameters.query.query)
            is SearchQuery.OpenFoodFactsUrl -> flowOf(0)
            is SearchQuery.FoodDataCentralUrl -> dao.observeCountByFdcId(parameters.query.fdcId)
        }
    }

    override fun observe(
        identity: FoodDataCentralProductIdentity
    ): Flow<RemoteData<FoodDataCentralProduct>> = channelFlow {
        send(RemoteData.Loading(null))

        val fdcId = identity.fdcId

        val localProduct = dao.observe(fdcId).first()

        if (localProduct == null) {
            try {
                val apiKey = settingsRepository.load().apiKey
                val openFoodFactsProduct = networkDataSource.getProduct(fdcId, apiKey).getOrThrow()
                val entity = mapper.foodDataCentralProductEntity(openFoodFactsProduct)
                dao.upsertProduct(entity)
                send(RemoteData.Success(mapper.foodDataCentralProduct(entity)))
            } catch (e: FoodDataCentralApiError) {
                when (e) {
                    is FoodDataCentralApiError.ProductNotFound -> send(RemoteData.NotFound)

                    else -> send(RemoteData.Error(e, null))
                }
            }
        } else {
            send(RemoteData.Success(mapper.foodDataCentralProduct(localProduct)))
        }

        dao.observe(fdcId).drop(1).collectLatest {
            when (it) {
                null -> send(RemoteData.NotFound)
                else -> send(RemoteData.Success(mapper.foodDataCentralProduct(it)))
            }
        }
    }

    override suspend fun refresh(
        identity: FoodDataCentralProductIdentity
    ): Result<FoodDataCentralProduct, FoodDataCentralApiError> {
        val fdcId = identity.fdcId

        return try {
            val remote = networkDataSource.getProduct(id = fdcId, apiKey = "DEMO_KEY").getOrThrow()
            val entity = mapper.foodDataCentralProductEntity(remote)
            dao.upsertProduct(entity)
            Ok(mapper.foodDataCentralProduct(entity))
        } catch (e: FoodDataCentralApiError) {
            Err(e)
        }
    }
}

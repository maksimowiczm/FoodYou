package com.maksimowiczm.foodyou.food.infrastructure.usda

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
import com.maksimowiczm.foodyou.food.domain.FoodDataCentralSettingsRepository
import com.maksimowiczm.foodyou.food.domain.FoodDatabaseError
import com.maksimowiczm.foodyou.food.domain.FoodProductDto
import com.maksimowiczm.foodyou.food.domain.FoodProductIdentity
import com.maksimowiczm.foodyou.food.domain.FoodStatus
import com.maksimowiczm.foodyou.food.domain.QueryParameters
import com.maksimowiczm.foodyou.food.infrastructure.usda.network.FoodDataCentralRemoteDataSource
import com.maksimowiczm.foodyou.food.infrastructure.usda.room.FoodDataCentralDao
import com.maksimowiczm.foodyou.food.infrastructure.usda.room.FoodDataCentralDatabase
import com.maksimowiczm.foodyou.food.search.domain.FoodSearchPreferencesRepository
import com.maksimowiczm.foodyou.food.search.domain.SearchParameters
import com.maksimowiczm.foodyou.food.search.domain.SearchQuery
import com.maksimowiczm.foodyou.food.search.domain.SearchableFoodDto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

class FoodDataCentralRepository(
    private val networkDataSource: FoodDataCentralRemoteDataSource,
    private val searchPreferencesRepository: FoodSearchPreferencesRepository,
    private val settingsRepository: FoodDataCentralSettingsRepository,
    private val database: FoodDataCentralDatabase,
    private val dao: FoodDataCentralDao,
    private val logger: Logger,
) {
    private val mapper = FoodDataCentralProductMapper()

    @OptIn(ExperimentalPagingApi::class)
    fun search(
        params: SearchParameters.FoodDataCentral,
        pageSize: Int,
    ): Flow<PagingData<SearchableFoodDto>> {
        val config = PagingConfig(pageSize = pageSize)

        if (params.query is SearchQuery.OpenFoodFactsUrl) {
            return flowOf(
                PagingData.empty(
                    sourceLoadStates =
                        LoadStates(NotLoading(true), NotLoading(true), NotLoading(true))
                )
            )
        }

        val factory = {
            when (params.query) {
                SearchQuery.Blank -> dao.getPagingSource()
                is SearchQuery.Barcode -> dao.getPagingSourceByBarcode(params.query.barcode)
                is SearchQuery.Text -> dao.getPagingSourceByQuery(params.query.query)
                is SearchQuery.OpenFoodFactsUrl -> error("Unreachable")
                is SearchQuery.FoodDataCentralUrl -> dao.getPagingSourceByFdcId(params.query.fdcId)
            }
        }

        return searchPreferencesRepository.observe().flatMapLatest { prefs ->
            val remoteMediator =
                if (prefs.allowFoodDataCentralUSDA && params.query is SearchQuery.NotBlank) {
                    FoodDataCentralRemoteMediator(
                        query = params.query,
                        database = database,
                        remote = networkDataSource,
                        apiKey = settingsRepository.load().apiKey,
                        logger = logger,
                    )
                } else null

            Pager(config = config, remoteMediator = remoteMediator, pagingSourceFactory = factory)
                .flow
                .map { pagingData -> pagingData.map { entity -> mapper.searchableFoodDto(entity) } }
        }
    }

    fun count(parameters: SearchParameters.FoodDataCentral): Flow<Int> {
        return when (parameters.query) {
            is SearchQuery.Blank -> dao.observeCount()
            is SearchQuery.Barcode -> dao.observeCountByBarcode(parameters.query.barcode)
            is SearchQuery.Text -> dao.observeCountByQuery(parameters.query.query)
            is SearchQuery.OpenFoodFactsUrl -> flowOf(0)
            is SearchQuery.FoodDataCentralUrl -> dao.observeCountByFdcId(parameters.query.fdcId)
        }
    }

    fun observe(parameters: QueryParameters.FoodDataCentral): Flow<FoodStatus<FoodProductDto>> =
        channelFlow {
            send(FoodStatus.Loading(parameters.identity, null))

            val fdcId = parameters.identity.fdcId

            val localProduct = dao.observe(fdcId).first()

            if (localProduct == null) {
                try {
                    val apiKey = settingsRepository.load().apiKey
                    val openFoodFactsProduct =
                        networkDataSource.getProduct(fdcId, apiKey).getOrThrow()
                    val entity = mapper.foodDataCentralProductEntity(openFoodFactsProduct)
                    dao.upsertProduct(entity)
                    send(FoodStatus.Available(mapper.foodProductDto(entity)))
                } catch (e: FoodDatabaseError) {
                    when (e) {
                        is FoodDatabaseError.ProductNotFound ->
                            send(FoodStatus.NotFound(parameters.identity))

                        else -> send(FoodStatus.Error(parameters.identity, null, e))
                    }
                }
            } else {
                send(FoodStatus.Available(mapper.foodProductDto(localProduct)))
            }

            dao.observe(fdcId).drop(1).collectLatest {
                when (it) {
                    null -> send(FoodStatus.NotFound(parameters.identity))
                    else -> send(FoodStatus.Available(mapper.foodProductDto(it)))
                }
            }
        }

    suspend fun refresh(
        identity: FoodProductIdentity.FoodDataCentral
    ): Result<FoodProductDto, FoodDatabaseError> {
        val fdcId = identity.fdcId

        return try {
            val remote = networkDataSource.getProduct(id = fdcId, apiKey = "DEMO_KEY").getOrThrow()
            val entity = mapper.foodDataCentralProductEntity(remote)
            dao.upsertProduct(entity)
            Ok(mapper.foodProductDto(entity))
        } catch (e: FoodDatabaseError) {
            Err(e)
        }
    }
}

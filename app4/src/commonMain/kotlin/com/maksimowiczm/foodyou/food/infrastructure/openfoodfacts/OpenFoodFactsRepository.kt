package com.maksimowiczm.foodyou.food.infrastructure.openfoodfacts

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import co.touchlab.kermit.Logger
import com.maksimowiczm.foodyou.common.Err
import com.maksimowiczm.foodyou.common.Ok
import com.maksimowiczm.foodyou.common.Result
import com.maksimowiczm.foodyou.food.domain.FoodDatabaseError
import com.maksimowiczm.foodyou.food.domain.FoodProductDto
import com.maksimowiczm.foodyou.food.domain.FoodProductIdentity
import com.maksimowiczm.foodyou.food.domain.FoodProductRepository.FoodStatus
import com.maksimowiczm.foodyou.food.domain.QueryParameters
import com.maksimowiczm.foodyou.food.infrastructure.openfoodfacts.network.OpenFoodFactsRemoteDataSource
import com.maksimowiczm.foodyou.food.infrastructure.openfoodfacts.room.OpenFoodFactsDao
import com.maksimowiczm.foodyou.food.infrastructure.openfoodfacts.room.OpenFoodFactsDatabase
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
import kotlinx.coroutines.flow.map

class OpenFoodFactsRepository(
    private val networkDataSource: OpenFoodFactsRemoteDataSource,
    private val searchPreferencesRepository: FoodSearchPreferencesRepository,
    private val database: OpenFoodFactsDatabase,
    private val dao: OpenFoodFactsDao,
    private val logger: Logger,
) {
    private val mapper = OpenFoodFactsProductMapper()

    @OptIn(ExperimentalPagingApi::class)
    fun search(
        searchFoodParams: SearchParameters.OpenFoodFacts,
        pageSize: Int,
    ): Flow<PagingData<SearchableFoodDto>> {
        val config = PagingConfig(pageSize = pageSize)

        val factory = {
            when (searchFoodParams.query) {
                SearchQuery.Blank -> dao.getPagingSource()
                is SearchQuery.Barcode ->
                    dao.getPagingSourceByBarcode(searchFoodParams.query.barcode)

                is SearchQuery.Text -> dao.getPagingSourceByQuery(searchFoodParams.query.query)
                is SearchQuery.OpenFoodFactsUrl ->
                    dao.getPagingSourceByBarcode(searchFoodParams.query.barcode)
            }
        }

        return searchPreferencesRepository.observe().flatMapLatest { prefs ->
            val remoteMediator =
                if (prefs.allowOpenFoodFacts && searchFoodParams.query is SearchQuery.NotBlank) {
                    OpenFoodFactsRemoteMediator(
                        query = searchFoodParams.query,
                        database = database,
                        remote = networkDataSource,
                        logger = logger,
                    )
                } else null

            Pager(config = config, pagingSourceFactory = factory, remoteMediator = remoteMediator)
                .flow
                .map { data -> data.map(mapper::searchableFoodDto) }
        }
    }

    fun count(parameters: SearchParameters.OpenFoodFacts): Flow<Int> {
        return when (parameters.query) {
            is SearchQuery.Blank -> dao.observeCount()
            is SearchQuery.Barcode -> dao.observeCountByBarcode(parameters.query.barcode)
            is SearchQuery.Text -> dao.observeCountByQuery(parameters.query.query)
            is SearchQuery.OpenFoodFactsUrl -> dao.observeCountByBarcode(parameters.query.barcode)
        }
    }

    fun observe(parameters: QueryParameters.OpenFoodFacts): Flow<FoodStatus> = channelFlow {
        send(FoodStatus.Loading(null))

        val barcode = parameters.identity.barcode

        val localProduct = dao.observe(barcode).first()

        if (localProduct == null) {
            try {
                val openFoodFactsProduct = networkDataSource.getProduct(barcode).getOrThrow()
                val entity = mapper.openFoodFactsProductEntity(openFoodFactsProduct)
                dao.upsertProduct(entity)
                send(FoodStatus.Available(mapper.foodProductDto(entity)))
            } catch (e: FoodDatabaseError) {
                when (e) {
                    is FoodDatabaseError.ProductNotFound -> send(FoodStatus.NotFound)
                    else -> send(FoodStatus.Error(null, e))
                }
            }
        } else {
            send(FoodStatus.Available(mapper.foodProductDto(localProduct)))
        }

        dao.observe(barcode).drop(1).collectLatest {
            when (it) {
                null -> send(FoodStatus.NotFound)
                else -> send(FoodStatus.Available(mapper.foodProductDto(it)))
            }
        }
    }

    suspend fun refresh(
        identity: FoodProductIdentity.OpenFoodFacts
    ): Result<FoodProductDto, FoodDatabaseError> {
        val barcode = identity.barcode

        return try {
            val remote = networkDataSource.getProduct(barcode).getOrThrow()
            val entity = mapper.openFoodFactsProductEntity(remote)
            dao.upsertProduct(entity)
            Ok(mapper.foodProductDto(entity))
        } catch (e: FoodDatabaseError) {
            Err(e)
        }
    }
}

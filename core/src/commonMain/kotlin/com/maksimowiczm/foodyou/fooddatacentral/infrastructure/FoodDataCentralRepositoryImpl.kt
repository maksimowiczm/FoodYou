package com.maksimowiczm.foodyou.fooddatacentral.infrastructure

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import co.touchlab.kermit.Logger
import com.maksimowiczm.foodyou.common.Err
import com.maksimowiczm.foodyou.common.Ok
import com.maksimowiczm.foodyou.common.RemoteData
import com.maksimowiczm.foodyou.common.Result
import com.maksimowiczm.foodyou.common.domain.search.SearchQuery
import com.maksimowiczm.foodyou.fooddatacentral.domain.FoodDataCentralApiError
import com.maksimowiczm.foodyou.fooddatacentral.domain.FoodDataCentralProduct
import com.maksimowiczm.foodyou.fooddatacentral.domain.FoodDataCentralProductIdentity
import com.maksimowiczm.foodyou.fooddatacentral.domain.FoodDataCentralRepository
import com.maksimowiczm.foodyou.fooddatacentral.domain.FoodDataCentralSearchParameters
import com.maksimowiczm.foodyou.fooddatacentral.domain.FoodDataCentralUrlSearchQuery
import com.maksimowiczm.foodyou.fooddatacentral.infrastructure.network.FoodDataCentralRemoteDataSource
import com.maksimowiczm.foodyou.fooddatacentral.infrastructure.room.FoodDataCentralDao
import com.maksimowiczm.foodyou.fooddatacentral.infrastructure.room.FoodDataCentralDatabase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

internal class FoodDataCentralRepositoryImpl(
    private val networkDataSource: FoodDataCentralRemoteDataSource,
    private val database: FoodDataCentralDatabase,
    private val dao: FoodDataCentralDao,
    private val logger: Logger,
) : FoodDataCentralRepository {
    private val mapper = FoodDataCentralProductMapper()

    @OptIn(ExperimentalPagingApi::class)
    override fun search(
        parameters: FoodDataCentralSearchParameters,
        pageSize: Int,
        remoteEnabled: Boolean,
        apiKey: String?,
        onNewProduct: suspend (Set<FoodDataCentralProduct>) -> Unit,
    ): Flow<PagingData<FoodDataCentralProduct>> {
        val config = PagingConfig(pageSize = pageSize)

        val factory = {
            when (val query = parameters.query) {
                is FoodDataCentralUrlSearchQuery -> dao.getPagingSourceByFdcId(query.fdcId)
                is SearchQuery.Barcode -> dao.getPagingSourceByBarcode(query.barcode)
                is SearchQuery.Blank ->
                    dao.getPagingSource(parameters.dataTypes.orAll().map { it.ordinal }.toSet())

                is SearchQuery.NotBlank ->
                    dao.getPagingSourceByQuery(query.query, parameters.dataTypes)
            }
        }

        val remoteMediator =
            if (remoteEnabled && parameters.query is SearchQuery.NotBlank) {
                FoodDataCentralRemoteMediator(
                    query = parameters.query,
                    database = database,
                    remote = networkDataSource,
                    mapper = mapper,
                    apiKey = apiKey,
                    pageSize = pageSize,
                    dataTypes = parameters.dataTypes,
                    logger = logger,
                    onNewProduct = onNewProduct,
                )
            } else null

        return Pager(
                config = config,
                remoteMediator = remoteMediator,
                pagingSourceFactory = factory,
            )
            .flow
            .map { pagingData ->
                pagingData.map { entity -> mapper.foodDataCentralProduct(entity) }
            }
    }

    override fun count(parameters: FoodDataCentralSearchParameters): Flow<Int> {
        return when (parameters.query) {
            is FoodDataCentralUrlSearchQuery -> dao.observeCountByFdcId(parameters.query.fdcId)
            is SearchQuery.Barcode -> dao.observeCountByBarcode(parameters.query.barcode)
            is SearchQuery.Blank ->
                dao.observeCount(parameters.dataTypes.orAll().map { it.ordinal }.toSet())

            is SearchQuery.NotBlank ->
                dao.observeCountByQuery(parameters.query.query, parameters.dataTypes)
        }
    }

    override fun observe(
        identity: FoodDataCentralProductIdentity,
        remoteEnabled: Boolean,
        apiKey: String?,
    ): Flow<RemoteData<FoodDataCentralProduct>> = channelFlow {
        send(RemoteData.Loading(null))

        val fdcId = identity.fdcId

        val localProduct = dao.observe(fdcId).first()

        if (localProduct == null) {
            if (remoteEnabled) {
                try {
                    val openFoodFactsProduct =
                        networkDataSource.getProduct(fdcId, apiKey).getOrThrow()
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
                send(RemoteData.NotFound)
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
        identity: FoodDataCentralProductIdentity,
        apiKey: String?,
    ): Result<FoodDataCentralProduct, FoodDataCentralApiError> {
        val fdcId = identity.fdcId

        return try {
            val remote = networkDataSource.getProduct(id = fdcId, apiKey = apiKey).getOrThrow()
            val entity = mapper.foodDataCentralProductEntity(remote)
            dao.upsertProduct(entity)
            Ok(mapper.foodDataCentralProduct(entity))
        } catch (e: FoodDataCentralApiError) {
            Err(e)
        }
    }

    private fun Set<FoodDataCentralSearchParameters.DataType>?.orAll() =
        this ?: FoodDataCentralSearchParameters.DataType.entries
}

package com.maksimowiczm.foodyou.openfoodfacts.infrastructure

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
import com.maksimowiczm.foodyou.openfoodfacts.domain.OpenFoodFactsApiError
import com.maksimowiczm.foodyou.openfoodfacts.domain.OpenFoodFactsProduct
import com.maksimowiczm.foodyou.openfoodfacts.domain.OpenFoodFactsProductIdentity
import com.maksimowiczm.foodyou.openfoodfacts.domain.OpenFoodFactsRepository
import com.maksimowiczm.foodyou.openfoodfacts.domain.OpenFoodFactsSearchParameters
import com.maksimowiczm.foodyou.openfoodfacts.domain.OpenFoodFactsUrlSearchQuery
import com.maksimowiczm.foodyou.openfoodfacts.infrastructure.network.OpenFoodFactsV2RemoteDataSource
import com.maksimowiczm.foodyou.openfoodfacts.infrastructure.network.SearchaliciousRemoteDataSource
import com.maksimowiczm.foodyou.openfoodfacts.infrastructure.room.OpenFoodFactsDatabase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

internal class OpenFoodFactsRepositoryImpl(
    private val searchApi: SearchaliciousRemoteDataSource,
    private val apiV2: OpenFoodFactsV2RemoteDataSource,
    private val database: OpenFoodFactsDatabase,
    private val logger: Logger,
) : OpenFoodFactsRepository {
    private val mapper = OpenFoodFactsProductMapper()
    private val dao = database.dao

    @OptIn(ExperimentalPagingApi::class)
    override fun search(
        parameters: OpenFoodFactsSearchParameters,
        pageSize: Int,
        remoteEnabled: Boolean,
    ): Flow<PagingData<OpenFoodFactsProduct>> {
        val config = PagingConfig(pageSize = pageSize)

        val factory = {
            when (val query = parameters.query) {
                is OpenFoodFactsUrlSearchQuery -> dao.getPagingSourceByBarcode(query.barcode)
                is SearchQuery.Barcode -> dao.getPagingSourceByBarcode(query.barcode)
                is SearchQuery.Blank -> dao.getPagingSource()
                is SearchQuery.NotBlank -> dao.getPagingSourceByQuery(query.query)
            }
        }

        val remoteMediator =
            if (remoteEnabled && parameters.query is SearchQuery.NotBlank) {
                OpenFoodFactsRemoteMediator(
                    query = parameters.query,
                    database = database,
                    search = searchApi,
                    apiV2 = apiV2,
                    mapper = mapper,
                    pageSize = pageSize,
                    logger = logger,
                )
            } else null

        return Pager(
                config = config,
                pagingSourceFactory = factory,
                remoteMediator = remoteMediator,
            )
            .flow
            .map { data -> data.map(mapper::toModel) }
    }

    override fun count(parameters: OpenFoodFactsSearchParameters): Flow<Int> {
        return when (val query = parameters.query) {
            is OpenFoodFactsUrlSearchQuery -> dao.observeCountByBarcode(query.barcode)
            is SearchQuery.Barcode -> dao.observeCountByBarcode(query.barcode)
            is SearchQuery.Blank -> dao.observeCount()
            is SearchQuery.NotBlank -> dao.observeCountByQuery(query.query)
        }
    }

    override fun observe(
        identity: OpenFoodFactsProductIdentity,
        remoteEnabled: Boolean,
    ): Flow<RemoteData<OpenFoodFactsProduct>> = channelFlow {
        send(RemoteData.Loading(null))

        val barcode = identity.barcode

        val localProduct = dao.observe(barcode).first()

        if (localProduct == null) {
            if (remoteEnabled) {
                try {
                    val openFoodFactsProduct = apiV2.getProduct(barcode).getOrThrow()
                    val entity = mapper.toEntity(openFoodFactsProduct)
                    dao.upsertProduct(entity)
                    send(RemoteData.Success(mapper.toModel(entity)))
                } catch (e: OpenFoodFactsApiError) {
                    when (e) {
                        is OpenFoodFactsApiError.ProductNotFound -> send(RemoteData.NotFound)

                        else -> send(RemoteData.Error(e, null))
                    }
                }
            } else {
                send(RemoteData.NotFound)
            }
        } else {
            send(RemoteData.Success(mapper.toModel(localProduct)))
        }

        dao.observe(barcode).drop(1).collectLatest {
            when (it) {
                null -> send(RemoteData.NotFound)
                else -> send(RemoteData.Success(mapper.toModel(it)))
            }
        }
    }

    override suspend fun refresh(
        identity: OpenFoodFactsProductIdentity
    ): Result<OpenFoodFactsProduct, OpenFoodFactsApiError> {
        val barcode = identity.barcode

        return try {
            val remote = apiV2.getProduct(barcode).getOrThrow()
            val entity = mapper.toEntity(remote)
            dao.upsertProduct(entity)
            Ok(mapper.toModel(entity))
        } catch (e: OpenFoodFactsApiError) {
            Err(e)
        } catch (e: Exception) {
            Err(OpenFoodFactsApiError.Unknown(e))
        }
    }
}

package com.maksimowiczm.foodyou.openfoodfacts.infrastructure

import androidx.paging.PagingData
import co.touchlab.kermit.Logger
import com.maksimowiczm.foodyou.common.Err
import com.maksimowiczm.foodyou.common.Ok
import com.maksimowiczm.foodyou.common.RemoteData
import com.maksimowiczm.foodyou.common.Result
import com.maksimowiczm.foodyou.openfoodfacts.domain.OpenFoodFactsApiError
import com.maksimowiczm.foodyou.openfoodfacts.domain.OpenFoodFactsCredentials
import com.maksimowiczm.foodyou.openfoodfacts.domain.OpenFoodFactsProduct
import com.maksimowiczm.foodyou.openfoodfacts.domain.OpenFoodFactsProductIdentity
import com.maksimowiczm.foodyou.openfoodfacts.domain.OpenFoodFactsRepository
import com.maksimowiczm.foodyou.openfoodfacts.domain.OpenFoodFactsSearchParameters
import com.maksimowiczm.foodyou.openfoodfacts.domain.OpenFoodFactsSearchParameters.OpenFoodFactsVersion
import com.maksimowiczm.foodyou.openfoodfacts.infrastructure.network.OpenFoodFactsApiV1SearchDataSource
import com.maksimowiczm.foodyou.openfoodfacts.infrastructure.network.OpenFoodFactsApiV2ProductDataSource
import com.maksimowiczm.foodyou.openfoodfacts.infrastructure.network.OpenFoodFactsSearchALiciousDataSource
import com.maksimowiczm.foodyou.openfoodfacts.infrastructure.room.OpenFoodFactsPagingKeySearchALiciousDao
import com.maksimowiczm.foodyou.openfoodfacts.infrastructure.room.OpenFoodFactsPagingKeyV1Dao
import com.maksimowiczm.foodyou.openfoodfacts.infrastructure.room.OpenFoodFactsProductDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.first

internal class OpenFoodFactsRepositoryImpl(
    private val productDao: OpenFoodFactsProductDao,
    private val pagingKeyV1Dao: OpenFoodFactsPagingKeyV1Dao,
    private val pagingKeySearchALiciousDao: OpenFoodFactsPagingKeySearchALiciousDao,
    private val apiV1Search: OpenFoodFactsApiV1SearchDataSource,
    private val apiV2Product: OpenFoodFactsApiV2ProductDataSource,
    private val searchALiciousSearch: OpenFoodFactsSearchALiciousDataSource,
    private val logger: Logger,
) : OpenFoodFactsRepository {
    private val mapper = OpenFoodFactsProductMapper()

    override fun search(
        parameters: OpenFoodFactsSearchParameters,
        pageSize: Int,
        remoteEnabled: Boolean,
        credentials: OpenFoodFactsCredentials.Decrypted?,
        onNewProduct: suspend (Set<OpenFoodFactsProduct>) -> Unit,
    ): Flow<PagingData<OpenFoodFactsProduct>> =
        resolve(parameters.version)
            .search(
                parameters = parameters,
                pageSize = pageSize,
                remoteEnabled = remoteEnabled,
                credentials = credentials,
                onNewFetchProduct = onNewProduct,
            )

    override fun count(parameters: OpenFoodFactsSearchParameters): Flow<Int> =
        resolve(parameters.version).count(parameters)

    override fun observe(
        identity: OpenFoodFactsProductIdentity,
        remoteEnabled: Boolean,
    ): Flow<RemoteData<OpenFoodFactsProduct>> = channelFlow {
        send(RemoteData.Loading(null))

        val barcode = identity.barcode

        val localProduct = productDao.observe(barcode).first()

        if (localProduct == null) {
            if (remoteEnabled) {
                try {
                    val openFoodFactsProduct = apiV2Product.getProduct(barcode).getOrThrow()
                    val entity = mapper.toEntity(openFoodFactsProduct)
                    productDao.upsertProduct(entity)
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

        productDao.observe(barcode).drop(1).collectLatest {
            when (it) {
                null -> send(RemoteData.NotFound)
                else -> send(RemoteData.Success(mapper.toModel(it)))
            }
        }
    }

    override suspend fun refresh(
        identity: OpenFoodFactsProductIdentity
    ): Result<OpenFoodFactsProduct, OpenFoodFactsApiError> =
        try {
            val remote = apiV2Product.getProduct(identity.barcode).getOrThrow()
            val entity = mapper.toEntity(remote)
            productDao.upsertProduct(entity)
            Ok(mapper.toModel(entity))
        } catch (e: OpenFoodFactsApiError) {
            Err(e)
        } catch (e: Exception) {
            Err(OpenFoodFactsApiError.Unknown(e))
        }

    private fun resolve(version: OpenFoodFactsVersion): OpenFoodFactsSearchScope =
        when (version) {
            OpenFoodFactsVersion.ApiV1 ->
                OpenFoodFactsSearchScope(
                    pagingKeyDao = pagingKeyV1Dao,
                    productDao = productDao,
                    searchDataSource = apiV1Search,
                    mapper = mapper,
                    apiV2 = apiV2Product,
                    logger = logger,
                )
            OpenFoodFactsVersion.SearchALicious ->
                OpenFoodFactsSearchScope(
                    pagingKeyDao = pagingKeySearchALiciousDao,
                    productDao = productDao,
                    searchDataSource = searchALiciousSearch,
                    mapper = mapper,
                    apiV2 = apiV2Product,
                    logger = logger,
                )
        }
}

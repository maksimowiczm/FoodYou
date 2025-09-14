package com.maksimowiczm.foodyou.app.infrastructure.opensource.openfoodfacts.room

import com.maksimowiczm.foodyou.app.infrastructure.opensource.openfoodfacts.network.LocalOpenFoodFactsPagingHelper
import com.maksimowiczm.foodyou.app.infrastructure.opensource.openfoodfacts.network.OpenFoodFactsPagingKey

internal class RoomOpenFoodFactsPagingHelper(private val dao: OpenFoodFactsDao) :
    LocalOpenFoodFactsPagingHelper {
    override suspend fun getPagingKey(query: String, country: String?): OpenFoodFactsPagingKey? =
        dao.getPagingKey(query, country ?: NULL_COUNTRY)?.toModel()

    override suspend fun upsertPagingKey(pagingKey: OpenFoodFactsPagingKey) {
        dao.upsertPagingKey(pagingKey.toEntity())
    }
}

private const val NULL_COUNTRY = "world"

private fun OpenFoodFactsPagingKey.toEntity(): OpenFoodFactsPagingKeyEntity =
    OpenFoodFactsPagingKeyEntity(
        queryString = queryString,
        country = country ?: NULL_COUNTRY,
        fetchedCount = fetchedCount,
        totalCount = totalCount,
    )

private fun OpenFoodFactsPagingKeyEntity.toModel(): OpenFoodFactsPagingKey =
    OpenFoodFactsPagingKey(
        queryString = queryString,
        country = country,
        fetchedCount = fetchedCount,
        totalCount = totalCount,
    )

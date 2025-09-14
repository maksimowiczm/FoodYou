package com.maksimowiczm.foodyou.app.infrastructure.opensource.usda.room

import com.maksimowiczm.foodyou.app.infrastructure.opensource.usda.network.LocalUsdaPagingHelper
import com.maksimowiczm.foodyou.app.infrastructure.opensource.usda.network.USDAPagingKey

internal class RoomUsdaPagingHelper(private val usdaPagingKeyDao: USDAPagingKeyDao) :
    LocalUsdaPagingHelper {
    override suspend fun getPagingKey(query: String): USDAPagingKey? =
        usdaPagingKeyDao.getPagingKey(query)?.toModel()

    override suspend fun upsertPagingKey(pagingKey: USDAPagingKey) {
        usdaPagingKeyDao.upsertPagingKey(pagingKey.toEntity())
    }
}

private fun USDAPagingKey.toEntity(): USDAPagingKeyEntity =
    USDAPagingKeyEntity(
        queryString = queryString,
        fetchedCount = fetchedCount,
        totalCount = totalCount,
    )

private fun USDAPagingKeyEntity.toModel(): USDAPagingKey =
    USDAPagingKey(queryString = queryString, fetchedCount = fetchedCount, totalCount = totalCount)

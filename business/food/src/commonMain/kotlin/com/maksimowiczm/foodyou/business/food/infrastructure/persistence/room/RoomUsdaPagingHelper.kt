package com.maksimowiczm.foodyou.business.food.infrastructure.persistence.room

import com.maksimowiczm.foodyou.business.food.domain.USDAPagingKey
import com.maksimowiczm.foodyou.business.food.infrastructure.persistence.LocalUsdaPagingHelper
import com.maksimowiczm.foodyou.business.shared.infrastructure.persistence.room.usda.USDAPagingKeyDao
import com.maksimowiczm.foodyou.business.shared.infrastructure.persistence.room.usda.USDAPagingKeyEntity

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

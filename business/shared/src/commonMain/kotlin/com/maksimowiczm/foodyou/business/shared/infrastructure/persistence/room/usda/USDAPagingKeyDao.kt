package com.maksimowiczm.foodyou.business.shared.infrastructure.persistence.room.usda

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert

@Dao
interface USDAPagingKeyDao {

    @Upsert suspend fun upsertPagingKey(pagingKey: USDAPagingKeyEntity)

    @Query("SELECT * FROM USDAPagingKey WHERE queryString = :query")
    suspend fun getPagingKey(query: String): USDAPagingKeyEntity?
}

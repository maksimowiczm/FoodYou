package com.maksimowiczm.foodyou.feature.search.database.dao

import androidx.room.Dao
import androidx.room.Upsert
import com.maksimowiczm.foodyou.feature.search.database.entity.ProductQueryEntity

@Dao
interface SearchDao {
    @Upsert
    suspend fun upsertProductQuery(productQueryEntity: ProductQueryEntity)
}

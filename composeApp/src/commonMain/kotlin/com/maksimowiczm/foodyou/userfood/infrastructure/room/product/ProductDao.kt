package com.maksimowiczm.foodyou.userfood.infrastructure.room.product

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlin.uuid.Uuid
import kotlinx.coroutines.flow.Flow

@Dao
internal abstract class ProductDao {
    @Insert abstract suspend fun insert(productEntity: ProductEntity): Long

    @Update abstract suspend fun update(productEntity: ProductEntity)

    @Delete abstract suspend fun delete(productEntity: ProductEntity)

    @Query(
        """
        SELECT *
        FROM Product
        WHERE uuid = :uuid
        """
    )
    abstract fun observe(uuid: Uuid): Flow<ProductEntity?>
}

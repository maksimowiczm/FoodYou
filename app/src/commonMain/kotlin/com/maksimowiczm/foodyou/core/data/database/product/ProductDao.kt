package com.maksimowiczm.foodyou.core.data.database.product

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.maksimowiczm.foodyou.core.data.model.product.ProductEntity
import com.maksimowiczm.foodyou.core.domain.source.ProductLocalDataSource
import kotlinx.coroutines.flow.Flow

@Dao
abstract class ProductDao : ProductLocalDataSource {

    @Query("SELECT * FROM productentity")
    abstract override suspend fun getProducts(): List<ProductEntity>

    @Update
    abstract override suspend fun updateProduct(product: ProductEntity)

    @Insert
    abstract override suspend fun insertProduct(product: ProductEntity): Long

    @Query(
        """
        SELECT *
        FROM productentity 
        WHERE id = :id
        """
    )
    abstract override fun observeProduct(id: Long): Flow<ProductEntity?>

    @Query(
        """
        DELETE FROM productentity  
        WHERE id = :id
        """
    )
    abstract override suspend fun deleteProduct(id: Long)
}

package com.maksimowiczm.foodyou.core.feature.product.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {
    @Query(
        """
        SELECT COUNT(*)
        FROM productentity p
        WHERE (:query IS NULL OR p.name LIKE '%' || :query || '%' OR p.brand LIKE '%' || :query || '%')
        ORDER BY p.id
        """
    )
    suspend fun getProductsCountByQuery(query: String): Int

    @Query("SELECT * FROM productentity WHERE id = :id")
    suspend fun getProductById(id: Long): ProductEntity?

    @Query("SELECT * FROM productentity WHERE id = :id")
    fun observeProductById(id: Long): Flow<ProductEntity?>

    @Query(
        """
        SELECT EXISTS (
            SELECT 1
            FROM productentity 
            WHERE 
            name = :name AND 
            (brand IS NULL OR brand = :brand) AND 
            (barcode IS NULL OR barcode = :barcode)
        )
        """
    )
    suspend fun productExist(
        name: String,
        brand: String?,
        barcode: String?
    ): Boolean

    @Transaction
    suspend fun insertOpenFoodFactsProducts(products: List<ProductEntity>) {
        val uniqueProducts = products.filterNot {
            productExist(it.name, it.brand, it.barcode)
        }

        insertProducts(uniqueProducts)
    }

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertProducts(products: List<ProductEntity>)

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertProduct(product: ProductEntity): Long

    @Update
    suspend fun updateProduct(product: ProductEntity)

    @Delete
    suspend fun deleteProduct(product: ProductEntity)
}

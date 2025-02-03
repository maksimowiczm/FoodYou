package com.maksimowiczm.foodyou.core.feature.product.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction

@Dao
interface ProductDao {
    @Query("SELECT * FROM productentity WHERE id = :id")
    suspend fun getProductById(id: Long): ProductEntity?

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
}

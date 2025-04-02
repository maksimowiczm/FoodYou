package com.maksimowiczm.foodyou.feature.diary.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import androidx.room.Upsert
import com.maksimowiczm.foodyou.feature.diary.data.model.ProductSource
import com.maksimowiczm.foodyou.feature.diary.database.entity.ProductEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {
    @Query("SELECT * FROM productentity WHERE id = :id")
    suspend fun getProductById(id: Long): ProductEntity?

    @Query("SELECT * FROM productentity WHERE id = :id")
    fun observeProductById(id: Long): Flow<ProductEntity?>

    @Query(
        """
        SELECT id
        FROM productentity 
        WHERE name = :name 
        AND (:brand IS NULL OR brand = :brand) 
        AND (:barcode IS NULL OR barcode = :barcode)
        AND productSource = :source
        """
    )
    suspend fun getProductId(
        name: String,
        brand: String?,
        barcode: String?,
        source: ProductSource
    ): Int?

    /**
     * Updates or inserts the given products into the database. If a product with the same name,
     * brand, barcode and source already exists in the database, it will be updated with the new
     * values. If not, a new product will be inserted.
     */
    @Transaction
    suspend fun upsertUniqueProducts(products: List<ProductEntity>) {
        val ids = products.map { product ->
            val id = getProductId(
                product.name,
                product.brand,
                product.barcode,
                ProductSource.OpenFoodFacts
            )
            id to product
        }

        val productsWithIds = ids.map { (id, product) ->
            if (id != null) {
                product.copy(id = id.toLong())
            } else {
                product
            }
        }

        upsertProducts(productsWithIds)
    }

    @Upsert
    suspend fun upsertProducts(products: List<ProductEntity>)

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertProduct(product: ProductEntity): Long

    @Update
    suspend fun updateProduct(product: ProductEntity)

    @Delete
    suspend fun deleteProduct(product: ProductEntity)

    @Query(
        """
        DELETE FROM ProductEntity 
        WHERE id IN (
            SELECT p.id 
            FROM ProductEntity p
            LEFT JOIN WeightMeasurementEntity m ON m.productId = p.id 
            WHERE m.productId IS NULL 
            AND p.productSource = :source
        )
        """
    )
    suspend fun deleteUnusedProducts(source: ProductSource)
}

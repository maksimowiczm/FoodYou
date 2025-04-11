package com.maksimowiczm.foodyou.core.database.product

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
abstract class ProductDao {

    @Upsert
    protected abstract suspend fun upsertProducts(products: List<ProductEntity>)

    @Upsert
    abstract suspend fun upsertProduct(product: ProductEntity): Long

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
    protected abstract suspend fun getProductId(
        name: String,
        brand: String?,
        barcode: String?,
        source: ProductSource
    ): Int?

    @Transaction
    open suspend fun insertOpenFoodFactsProducts(products: List<ProductEntity>) {
        val ids = products
            .filter { it.productSource == ProductSource.OpenFoodFacts }
            .map { product ->
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

    @Query(
        """
        SELECT *
        FROM productentity 
        WHERE id = :id
        """
    )
    abstract fun observeProduct(id: Long): Flow<ProductEntity?>

    @Query(
        """
        DELETE FROM productentity  
        WHERE id = :id
        """
    )
    abstract suspend fun deleteProduct(id: Long)

    @Query(
        """
        DELETE FROM ProductEntity 
        WHERE id IN (
            SELECT p.id 
            FROM ProductEntity p
            LEFT JOIN ProductMeasurementEntity m ON m.productId = p.id 
            WHERE m.productId IS NULL 
            AND p.productSource = :source
        )
        """
    )
    abstract suspend fun deleteUnusedProducts(source: ProductSource)
}

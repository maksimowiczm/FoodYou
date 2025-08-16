package com.maksimowiczm.foodyou.business.shared.infrastructure.persistence.room.food

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.maksimowiczm.foodyou.business.shared.infrastructure.persistence.room.shared.FoodSourceType
import kotlinx.coroutines.flow.Flow

@Dao
abstract class ProductDao {
    @Query(
        """
        SELECT *
        FROM Product
        WHERE id = :id
        """
    )
    abstract fun observeProduct(id: Long): Flow<ProductEntity?>

    @Query(
        """
        SELECT *
        FROM Product
        LIMIT :limit OFFSET :offset
        """
    )
    abstract fun observeProducts(limit: Int, offset: Int): Flow<List<ProductEntity>>

    @Insert abstract suspend fun insertProduct(product: ProductEntity): Long

    @Update abstract suspend fun updateProduct(product: ProductEntity)

    @Delete abstract suspend fun deleteProduct(product: ProductEntity)

    @Query(
        """
        SELECT EXISTS (
            SELECT 1
            FROM Product
            WHERE name = :name AND
                  (:brand IS NULL OR brand = :brand) AND
                  (:barcode IS NULL OR barcode = :barcode) AND
                  :source = sourceType
        )
        """
    )
    protected abstract suspend fun existsProductByNameAndBrand(
        name: String,
        brand: String?,
        barcode: String?,
        source: FoodSourceType,
    ): Boolean

    /**
     * Inserts a single product into the database if it does not already exist. This method checks
     * for uniqueness based on the product's name, brand, barcode, and source type.
     *
     * @param product The product to be inserted.
     * @return The ID of the inserted product, or null if the product already exists.
     */
    @Transaction
    open suspend fun insertUniqueProduct(product: ProductEntity): Long? =
        if (
            !existsProductByNameAndBrand(
                name = product.name,
                brand = product.brand,
                barcode = product.barcode,
                source = product.sourceType,
            )
        ) {
            insertProduct(product)
        } else {
            null
        }
}

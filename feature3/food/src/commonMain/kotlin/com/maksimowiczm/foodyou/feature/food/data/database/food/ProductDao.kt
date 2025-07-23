package com.maksimowiczm.foodyou.feature.food.data.database.food

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.maksimowiczm.foodyou.feature.food.domain.FoodSource
import kotlinx.coroutines.flow.Flow

@Dao
abstract class ProductDao {

    @Query("SELECT * FROM Product WHERE id = :productId")
    abstract fun observe(productId: Long): Flow<Product?>

    @Insert
    abstract suspend fun insert(product: Product): Long

    @Update
    abstract suspend fun update(product: Product)

    @Delete
    abstract suspend fun delete(product: Product)

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
        source: FoodSource.Type
    ): Boolean

    /**
     * Inserts a single product into the database if it does not already exist.
     * This method checks for uniqueness based on the product's name, brand, barcode, and source type.
     *
     * @param product The product to be inserted.
     * @return The ID of the inserted product, or null if the product already exists.
     */
    @Transaction
    open suspend fun insertUniqueProduct(product: Product): Long? =
        if (!existsProductByNameAndBrand(
                name = product.name,
                brand = product.brand,
                barcode = product.barcode,
                source = product.sourceType
            )
        ) {
            insert(product)
        } else {
            null
        }
}

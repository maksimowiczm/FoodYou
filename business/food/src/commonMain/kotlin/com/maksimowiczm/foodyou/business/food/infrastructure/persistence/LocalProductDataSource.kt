package com.maksimowiczm.foodyou.business.food.infrastructure.persistence

import com.maksimowiczm.foodyou.business.food.domain.FoodId
import com.maksimowiczm.foodyou.business.food.domain.Product
import kotlinx.coroutines.flow.Flow

internal interface LocalProductDataSource {

    fun observeProduct(id: FoodId.Product): Flow<Product?>

    suspend fun deleteProduct(product: Product)

    suspend fun insertProduct(product: Product): FoodId.Product

    /**
     * Inserts a single product into the database if it does not already exist.
     *
     * @return The ID of the inserted product, or null if the product already exists.
     */
    suspend fun insertUniqueProduct(product: Product): FoodId.Product?

    suspend fun updateProduct(product: Product)
}

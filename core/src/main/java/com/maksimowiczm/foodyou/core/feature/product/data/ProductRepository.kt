package com.maksimowiczm.foodyou.core.feature.product.data

import com.github.michaelbull.result.Result
import com.maksimowiczm.foodyou.core.feature.product.data.model.Product
import com.maksimowiczm.foodyou.core.feature.product.data.model.WeightUnit
import kotlinx.coroutines.flow.Flow

enum class ProductCreationError {
    PRODUCT_ALREADY_EXISTS
}

enum class ProductUpdateError {
    PRODUCT_NOT_FOUND
}

enum class ProductDeletionError {
    PRODUCT_NOT_FOUND
}

interface ProductRepository {
    fun observeProductById(id: Long): Flow<Product?>

    suspend fun getProductById(id: Long): Product?

    suspend fun createUserProduct(
        name: String,
        brand: String?,
        barcode: String?,
        calories: Float,
        proteins: Float,
        carbohydrates: Float,
        sugars: Float?,
        fats: Float,
        saturatedFats: Float?,
        salt: Float?,
        sodium: Float?,
        fiber: Float?,
        packageWeight: Float?,
        servingWeight: Float?,
        weightUnit: WeightUnit
    ): Result<Long, ProductCreationError>

    suspend fun updateProduct(
        id: Long,
        name: String,
        brand: String?,
        barcode: String?,
        calories: Float,
        proteins: Float,
        carbohydrates: Float,
        sugars: Float?,
        fats: Float,
        saturatedFats: Float?,
        salt: Float?,
        sodium: Float?,
        fiber: Float?,
        packageWeight: Float?,
        servingWeight: Float?,
        weightUnit: WeightUnit
    ): Result<Unit, ProductUpdateError>

    suspend fun deleteProduct(id: Long): Result<Unit, ProductDeletionError>
}

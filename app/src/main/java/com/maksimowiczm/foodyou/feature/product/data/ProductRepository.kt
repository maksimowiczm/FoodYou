package com.maksimowiczm.foodyou.feature.product.data

import com.github.michaelbull.result.Result
import com.maksimowiczm.foodyou.feature.product.data.model.Product
import com.maksimowiczm.foodyou.feature.product.data.model.WeightUnit

enum class ProductCreationError {
    PRODUCT_ALREADY_EXISTS
}

interface ProductRepository {
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
}

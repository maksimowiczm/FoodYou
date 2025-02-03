package com.maksimowiczm.foodyou.core.feature.product.data

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.maksimowiczm.foodyou.core.feature.product.data.model.Product
import com.maksimowiczm.foodyou.core.feature.product.data.model.ProductSource
import com.maksimowiczm.foodyou.core.feature.product.data.model.WeightUnit
import com.maksimowiczm.foodyou.core.feature.product.data.model.toDomain
import com.maksimowiczm.foodyou.core.feature.product.database.ProductDao
import com.maksimowiczm.foodyou.core.feature.product.database.ProductDatabase
import com.maksimowiczm.foodyou.core.feature.product.database.ProductEntity

class ProductRepositoryImpl(
    productDatabase: ProductDatabase
) : ProductRepository {
    private val productDao: ProductDao = productDatabase.productDao()

    override suspend fun getProductById(id: Long): Product? {
        return productDao.getProductById(id)?.toDomain()
    }

    override suspend fun createUserProduct(
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
    ): Result<Long, ProductCreationError> {
        val entity = ProductEntity(
            name = name,
            brand = brand,
            barcode = barcode,
            calories = calories,
            proteins = proteins,
            carbohydrates = carbohydrates,
            sugars = sugars,
            fats = fats,
            saturatedFats = saturatedFats,
            salt = salt,
            sodium = sodium,
            fiber = fiber,
            packageWeight = packageWeight,
            servingWeight = servingWeight,
            weightUnit = weightUnit,
            productSource = ProductSource.User
        )

        val id = productDao.insertProduct(entity)

        return if (id != -1L) {
            Ok(id)
        } else {
            Err(ProductCreationError.PRODUCT_ALREADY_EXISTS)
        }
    }
}

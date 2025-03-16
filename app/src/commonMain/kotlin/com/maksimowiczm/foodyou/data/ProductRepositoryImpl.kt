package com.maksimowiczm.foodyou.data

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.maksimowiczm.foodyou.data.model.Product
import com.maksimowiczm.foodyou.data.model.ProductSource
import com.maksimowiczm.foodyou.data.model.WeightUnit
import com.maksimowiczm.foodyou.data.model.toDomain
import com.maksimowiczm.foodyou.database.dao.ProductDao
import com.maksimowiczm.foodyou.database.entity.ProductEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ProductRepositoryImpl(private val productDao: ProductDao) : ProductRepository {

    override fun observeProductById(id: Long): Flow<Product?> =
        productDao.observeProductById(id).map {
            it?.toDomain()
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

    override suspend fun updateProduct(
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
    ): Result<Unit, ProductUpdateError> {
        val entity = ProductEntity(
            id = id,
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

        if (productDao.getProductById(id) == null) {
            return Err(ProductUpdateError.PRODUCT_NOT_FOUND)
        }

        productDao.updateProduct(entity)

        return Ok(Unit)
    }

    override suspend fun deleteProduct(id: Long): Result<Unit, ProductDeletionError> {
        val entity = productDao.getProductById(id)

        if (entity == null) {
            return Err(ProductDeletionError.PRODUCT_NOT_FOUND)
        }

        productDao.deleteProduct(entity)

        return Ok(Unit)
    }
}

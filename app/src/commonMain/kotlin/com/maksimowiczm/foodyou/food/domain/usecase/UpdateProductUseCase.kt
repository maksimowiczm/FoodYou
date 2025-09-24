package com.maksimowiczm.foodyou.food.domain.usecase

import com.maksimowiczm.foodyou.common.domain.database.TransactionProvider
import com.maksimowiczm.foodyou.common.domain.food.FoodSource
import com.maksimowiczm.foodyou.common.domain.food.NutritionFacts
import com.maksimowiczm.foodyou.common.log.Logger
import com.maksimowiczm.foodyou.common.log.logAndReturnFailure
import com.maksimowiczm.foodyou.common.result.Ok
import com.maksimowiczm.foodyou.common.result.Result
import com.maksimowiczm.foodyou.food.domain.entity.FoodId
import com.maksimowiczm.foodyou.food.domain.repository.ProductRepository
import kotlinx.coroutines.flow.first

sealed interface UpdateProductError {
    data object NameEmpty : UpdateProductError

    data class ProductNotFound(val id: FoodId.Product) : UpdateProductError
}

class UpdateProductUseCase(
    private val productRepository: ProductRepository,
    private val transactionProvider: TransactionProvider,
    private val logger: Logger,
) {
    suspend fun update(
        id: FoodId.Product,
        name: String,
        brand: String?,
        nutritionFacts: NutritionFacts,
        barcode: String?,
        packageWeight: Double?,
        servingWeight: Double?,
        note: String?,
        source: FoodSource,
        isLiquid: Boolean,
    ): Result<Unit, UpdateProductError> {
        if (name.isBlank()) {
            return logger.logAndReturnFailure(
                tag = TAG,
                throwable = null,
                error = UpdateProductError.NameEmpty,
                message = { "Product name cannot be empty." },
            )
        }

        return transactionProvider.withTransaction {
            val product = productRepository.observeProduct(id).first()
            if (product == null) {
                return@withTransaction logger.logAndReturnFailure(
                    tag = TAG,
                    throwable = null,
                    error = UpdateProductError.ProductNotFound(id),
                    message = { "Product with ID $id not found." },
                )
            }

            val updatedProduct =
                product.copy(
                    name = name,
                    brand = brand?.ifBlank { null },
                    barcode = barcode?.ifBlank { null },
                    nutritionFacts = nutritionFacts,
                    packageWeight = packageWeight,
                    servingWeight = servingWeight,
                    note = note?.ifBlank { null },
                    source = source,
                    isLiquid = isLiquid,
                )

            productRepository.updateProduct(updatedProduct)

            Ok(Unit)
        }
    }

    private companion object {
        const val TAG = "UpdateProductUseCase"
    }
}

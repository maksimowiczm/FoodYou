package com.maksimowiczm.foodyou.business.food.application

import com.maksimowiczm.foodyou.business.food.domain.FoodEvent
import com.maksimowiczm.foodyou.business.food.domain.FoodEventRepository
import com.maksimowiczm.foodyou.business.food.domain.ProductRepository
import com.maksimowiczm.foodyou.business.shared.application.infrastructure.date.DateProvider
import com.maksimowiczm.foodyou.business.shared.application.infrastructure.error.logAndReturnFailure
import com.maksimowiczm.foodyou.business.shared.application.infrastructure.persistence.DatabaseTransactionProvider
import com.maksimowiczm.foodyou.business.shared.domain.food.FoodSource
import com.maksimowiczm.foodyou.business.shared.domain.nutrients.NutritionFacts
import com.maksimowiczm.foodyou.shared.common.application.log.Logger
import com.maksimowiczm.foodyou.shared.common.domain.food.FoodId
import com.maksimowiczm.foodyou.shared.common.result.Ok
import com.maksimowiczm.foodyou.shared.common.result.Result
import kotlinx.coroutines.flow.first

sealed interface UpdateProductError {
    data object NameEmpty : UpdateProductError

    data class ProductNotFound(val id: FoodId.Product) : UpdateProductError
}

fun interface UpdateProductUseCase {
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
    ): Result<Unit, UpdateProductError>
}

internal class UpdateProductUseCaseImpl(
    private val productRepository: ProductRepository,
    private val eventRepository: FoodEventRepository,
    private val transactionProvider: DatabaseTransactionProvider,
    private val dateProvider: DateProvider,
    private val logger: Logger,
) : UpdateProductUseCase {
    override suspend fun update(
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
            eventRepository.insert(id, FoodEvent.Edited(dateProvider.now()))

            Ok(Unit)
        }
    }

    private companion object {
        const val TAG = "UpdateProductUseCaseImpl"
    }
}

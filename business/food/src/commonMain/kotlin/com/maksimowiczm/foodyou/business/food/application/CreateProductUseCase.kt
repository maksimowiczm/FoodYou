package com.maksimowiczm.foodyou.business.food.application

import com.maksimowiczm.foodyou.business.food.domain.FoodEvent
import com.maksimowiczm.foodyou.business.food.domain.FoodEventRepository
import com.maksimowiczm.foodyou.business.food.domain.ProductRepository
import com.maksimowiczm.foodyou.business.shared.application.infrastructure.error.logAndReturnFailure
import com.maksimowiczm.foodyou.business.shared.application.infrastructure.persistence.DatabaseTransactionProvider
import com.maksimowiczm.foodyou.business.shared.domain.food.FoodSource
import com.maksimowiczm.foodyou.business.shared.domain.nutrients.NutritionFacts
import com.maksimowiczm.foodyou.shared.common.application.log.Logger
import com.maksimowiczm.foodyou.shared.common.domain.food.FoodId
import com.maksimowiczm.foodyou.shared.common.result.Ok
import com.maksimowiczm.foodyou.shared.common.result.Result

sealed interface CreateProductError {
    object NameEmpty : CreateProductError
}

fun interface CreateProductUseCase {
    suspend fun create(
        name: String,
        brand: String?,
        barcode: String?,
        note: String?,
        isLiquid: Boolean,
        packageWeight: Double?,
        servingWeight: Double?,
        source: FoodSource,
        nutritionFacts: NutritionFacts,
        event: FoodEvent.FoodCreationEvent,
    ): Result<FoodId.Product, CreateProductError>
}

internal class CreateProductUseCaseImpl(
    private val transactionProvider: DatabaseTransactionProvider,
    private val productRepository: ProductRepository,
    private val foodEventRepository: FoodEventRepository,
    private val logger: Logger,
) : CreateProductUseCase {
    override suspend fun create(
        name: String,
        brand: String?,
        barcode: String?,
        note: String?,
        isLiquid: Boolean,
        packageWeight: Double?,
        servingWeight: Double?,
        source: FoodSource,
        nutritionFacts: NutritionFacts,
        event: FoodEvent.FoodCreationEvent,
    ): Result<FoodId.Product, CreateProductError> {
        if (name.isBlank()) {
            return logger.logAndReturnFailure(
                tag = TAG,
                throwable = null,
                error = CreateProductError.NameEmpty,
                message = { "Product name cannot be empty." },
            )
        }

        return transactionProvider.withTransaction {
            val productId =
                productRepository.insertProduct(
                    name = name,
                    brand = brand?.ifBlank { null },
                    barcode = barcode?.ifBlank { null },
                    note = note?.ifBlank { null },
                    isLiquid = isLiquid,
                    packageWeight = packageWeight,
                    servingWeight = servingWeight,
                    source = source,
                    nutritionFacts = nutritionFacts,
                )
            foodEventRepository.insert(productId, event)
            Ok(productId)
        }
    }

    private companion object {
        const val TAG = "CreateProductUseCaseImpl"
    }
}

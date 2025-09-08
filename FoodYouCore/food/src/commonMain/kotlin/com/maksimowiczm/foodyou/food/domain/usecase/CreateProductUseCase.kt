package com.maksimowiczm.foodyou.food.domain.usecase

import com.maksimowiczm.foodyou.food.domain.entity.FoodHistory
import com.maksimowiczm.foodyou.food.domain.entity.FoodId
import com.maksimowiczm.foodyou.food.domain.repository.FoodHistoryRepository
import com.maksimowiczm.foodyou.food.domain.repository.ProductRepository
import com.maksimowiczm.foodyou.shared.domain.Ok
import com.maksimowiczm.foodyou.shared.domain.Result
import com.maksimowiczm.foodyou.shared.domain.database.TransactionProvider
import com.maksimowiczm.foodyou.shared.domain.food.FoodSource
import com.maksimowiczm.foodyou.shared.domain.food.NutritionFacts
import com.maksimowiczm.foodyou.shared.domain.log.Logger
import com.maksimowiczm.foodyou.shared.domain.log.logAndReturnFailure

sealed interface CreateProductError {
    object NameEmpty : CreateProductError
}

class CreateProductUseCase(
    private val productRepository: ProductRepository,
    private val historyRepository: FoodHistoryRepository,
    private val transactionProvider: TransactionProvider,
    private val logger: Logger,
) {
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
        history: FoodHistory.CreationHistory,
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

            historyRepository.insert(productId, history)

            Ok(productId)
        }
    }

    private companion object {
        const val TAG = "CreateProductUseCase"
    }
}

package com.maksimowiczm.foodyou.business.food.application.command

import com.maksimowiczm.foodyou.business.food.domain.FoodEvent
import com.maksimowiczm.foodyou.business.food.domain.FoodSource
import com.maksimowiczm.foodyou.business.food.infrastructure.persistence.LocalFoodEventDataSource
import com.maksimowiczm.foodyou.business.food.infrastructure.persistence.LocalProductDataSource
import com.maksimowiczm.foodyou.business.shared.domain.error.ErrorLoggingUtils
import com.maksimowiczm.foodyou.business.shared.domain.infrastructure.persistence.DatabaseTransactionProvider
import com.maksimowiczm.foodyou.business.shared.domain.nutrients.NutritionFacts
import com.maksimowiczm.foodyou.shared.common.date.now
import com.maksimowiczm.foodyou.shared.common.domain.food.FoodId
import com.maksimowiczm.foodyou.shared.common.domain.infrastructure.command.Command
import com.maksimowiczm.foodyou.shared.common.domain.infrastructure.command.CommandHandler
import com.maksimowiczm.foodyou.shared.common.domain.result.Ok
import com.maksimowiczm.foodyou.shared.common.domain.result.Result
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.datetime.LocalDateTime

data class UpdateProductCommand(
    val id: FoodId.Product,
    val name: String,
    val brand: String?,
    val nutritionFacts: NutritionFacts,
    val barcode: String?,
    val packageWeight: Double?,
    val servingWeight: Double?,
    val note: String?,
    val source: FoodSource,
    val isLiquid: Boolean,
) : Command<Unit, UpdateProductError> {}

sealed interface UpdateProductError {
    data object NameEmpty : UpdateProductError

    data class ProductNotFound(val id: FoodId.Product) : UpdateProductError
}

internal class UpdateProductCommandHandler(
    private val productDataSource: LocalProductDataSource,
    private val eventDataSource: LocalFoodEventDataSource,
    private val transactionProvider: DatabaseTransactionProvider,
) : CommandHandler<UpdateProductCommand, Unit, UpdateProductError> {

    override suspend fun handle(command: UpdateProductCommand): Result<Unit, UpdateProductError> {
        if (command.name.isBlank()) {
            return ErrorLoggingUtils.logAndReturnFailure(
                tag = TAG,
                throwable = null,
                error = UpdateProductError.NameEmpty,
                message = { "Product name cannot be empty." },
            )
        }

        val product = productDataSource.observeProduct(command.id).firstOrNull()

        if (product == null) {
            return ErrorLoggingUtils.logAndReturnFailure(
                tag = TAG,
                throwable = null,
                error = UpdateProductError.ProductNotFound(command.id),
                message = { "Product with ID ${command.id} not found." },
            )
        }

        val updatedProduct =
            product.copy(
                name = command.name,
                brand = command.brand,
                barcode = command.barcode,
                nutritionFacts = command.nutritionFacts,
                packageWeight = command.packageWeight,
                servingWeight = command.servingWeight,
                note = command.note,
                source = command.source,
                isLiquid = command.isLiquid,
            )

        transactionProvider.withTransaction {
            productDataSource.updateProduct(updatedProduct)
            eventDataSource.insert(command.id, FoodEvent.Edited(LocalDateTime.now()))
        }

        return Ok(Unit)
    }

    private companion object {
        private const val TAG = "UpdateProductCommandHandler"
    }
}

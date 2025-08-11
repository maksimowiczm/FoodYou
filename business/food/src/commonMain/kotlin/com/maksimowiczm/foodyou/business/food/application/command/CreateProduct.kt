package com.maksimowiczm.foodyou.business.food.application.command

import com.maksimowiczm.foodyou.business.food.domain.FoodEvent
import com.maksimowiczm.foodyou.business.food.domain.Product
import com.maksimowiczm.foodyou.business.food.infrastructure.persistence.LocalFoodEventDataSource
import com.maksimowiczm.foodyou.business.food.infrastructure.persistence.LocalProductDataSource
import com.maksimowiczm.foodyou.business.shared.domain.error.ErrorLoggingUtils
import com.maksimowiczm.foodyou.business.shared.domain.food.FoodSource
import com.maksimowiczm.foodyou.business.shared.domain.infrastructure.persistence.DatabaseTransactionProvider
import com.maksimowiczm.foodyou.business.shared.domain.nutrients.NutritionFacts
import com.maksimowiczm.foodyou.shared.common.domain.food.FoodId
import com.maksimowiczm.foodyou.shared.common.domain.infrastructure.command.Command
import com.maksimowiczm.foodyou.shared.common.domain.infrastructure.command.CommandHandler
import com.maksimowiczm.foodyou.shared.common.domain.result.Ok
import com.maksimowiczm.foodyou.shared.common.domain.result.Result

/**
 * @param name Name of the product.
 * @param brand Brand of the product, if available.
 * @param barcode Barcode of the product, if available.
 * @param note Additional note about the product, if available.
 * @param isLiquid Indicates whether the product is liquid (e.g., juice, milk).
 * @param packageWeight Weight of the product package, if available.
 * @param source Source of the product.
 * @param servingWeight Weight of a single serving of the product, if available.
 * @param nutritionFacts Nutrition facts of the product per 100g or 100ml, depending on whether the
 *   product is solid or liquid.
 * @param event Event associated with the creation of the product, used for tracking changes.
 */
data class CreateProductCommand(
    val name: String,
    val brand: String?,
    val barcode: String?,
    val note: String?,
    val isLiquid: Boolean,
    val packageWeight: Double?,
    val servingWeight: Double?,
    val source: FoodSource,
    val nutritionFacts: NutritionFacts,
    val event: FoodEvent.FoodCreationEvent,
) : Command<FoodId.Product, CreateProductError>

sealed interface CreateProductError {
    object NameEmpty : CreateProductError
}

/**
 * Command handler for creating a new product.
 *
 * This handler processes the [CreateProductCommand] and returns [FoodId] on success, or a
 * [CreateProductError] on failure.
 */
internal class CreateProductCommandHandler(
    private val localProductDataSource: LocalProductDataSource,
    private val foodEventDataSource: LocalFoodEventDataSource,
    private val transactionProvider: DatabaseTransactionProvider,
) : CommandHandler<CreateProductCommand, FoodId.Product, CreateProductError> {

    override suspend fun handle(
        command: CreateProductCommand
    ): Result<FoodId.Product, CreateProductError> {
        if (command.name.isBlank()) {
            return ErrorLoggingUtils.logAndReturnFailure(
                tag = TAG,
                throwable = null,
                error = CreateProductError.NameEmpty,
                message = { "Product name cannot be empty." },
            )
        }

        val product = command.toProduct()

        val productId =
            transactionProvider.withTransaction {
                val productId = localProductDataSource.insertProduct(product)
                foodEventDataSource.insert(productId, command.event)
                productId
            }

        return Ok(productId)
    }

    private companion object {
        private const val TAG = "CreateProductCommandHandler"
    }
}

private fun CreateProductCommand.toProduct() =
    Product(
        id = FoodId.Product(0),
        name = name,
        brand = brand,
        barcode = barcode,
        note = note,
        isLiquid = isLiquid,
        packageWeight = packageWeight,
        servingWeight = servingWeight,
        source = source,
        nutritionFacts = nutritionFacts,
    )

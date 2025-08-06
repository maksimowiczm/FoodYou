package com.maksimowiczm.foodyou.business.food.application.command

import com.maksimowiczm.foodyou.business.food.domain.FoodEvent
import com.maksimowiczm.foodyou.business.food.domain.FoodId
import com.maksimowiczm.foodyou.business.food.domain.FoodSource
import com.maksimowiczm.foodyou.business.food.domain.NutritionFacts
import com.maksimowiczm.foodyou.business.food.domain.Product
import com.maksimowiczm.foodyou.business.food.infrastructure.persistence.LocalFoodEventDataSource
import com.maksimowiczm.foodyou.business.food.infrastructure.persistence.LocalProductDataSource
import com.maksimowiczm.foodyou.business.shared.domain.ErrorLoggingUtils
import com.maksimowiczm.foodyou.shared.common.domain.infrastructure.command.Command
import com.maksimowiczm.foodyou.shared.common.domain.infrastructure.command.CommandHandler
import com.maksimowiczm.foodyou.shared.common.domain.result.Ok
import com.maksimowiczm.foodyou.shared.common.domain.result.Result
import kotlin.reflect.KClass
import kotlinx.coroutines.CancellationException

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
) : Command

typealias CreateProductError = Unit

/**
 * Command handler for creating a new product.
 *
 * This handler processes the [CreateProductCommand] and returns [FoodId] on success, or a
 * [CreateProductError] on failure.
 */
internal class CreateProductCommandHandler(
    private val localProductDataSource: LocalProductDataSource,
    private val foodEventDataSource: LocalFoodEventDataSource,
) : CommandHandler<CreateProductCommand, FoodId, CreateProductError> {

    override val commandType: KClass<CreateProductCommand> = CreateProductCommand::class

    override suspend fun handle(command: CreateProductCommand): Result<FoodId, CreateProductError> =
        try {
            val product = command.toProduct()
            val productId = localProductDataSource.insertProduct(product)
            foodEventDataSource.insert(productId, command.event)
            Ok(productId)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            ErrorLoggingUtils.logAndReturnFailure(
                tag = TAG,
                throwable = e,
                error = CreateProductError,
                message = { "Failed to create product: ${e.message}" },
            )
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

package com.maksimowiczm.foodyou.importexport.tbca.domain

import com.maksimowiczm.foodyou.common.domain.database.TransactionProvider
import com.maksimowiczm.foodyou.common.domain.date.DateProvider
import com.maksimowiczm.foodyou.common.domain.food.NutrientValue.Companion.toNutrientValue
import com.maksimowiczm.foodyou.common.domain.food.NutritionFacts
import com.maksimowiczm.foodyou.food.domain.entity.FoodHistory
import com.maksimowiczm.foodyou.food.domain.entity.RemoteProduct
import com.maksimowiczm.foodyou.food.domain.repository.FoodHistoryRepository
import com.maksimowiczm.foodyou.food.domain.repository.ProductRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow

/**
 * Use case for importing TBCA (Brazilian Food Composition Table) foods into the database.
 *
 * Reads all TBCA foods from the repository, converts them to products,
 * and saves them to the database with progress tracking.
 */
fun interface ImportTBCAUseCase {
    /**
     * Imports all TBCA foods into the database.
     *
     * @return Flow emitting the number of foods imported (for progress tracking)
     */
    suspend fun import(): Flow<Int>
}

internal class ImportTBCAUseCaseImpl(
    private val tbcaRepository: TBCARepository,
    private val productRepository: ProductRepository,
    private val historyRepository: FoodHistoryRepository,
    private val transactionProvider: TransactionProvider,
    private val dateProvider: DateProvider,
) : ImportTBCAUseCase {

    override suspend fun import(): Flow<Int> = channelFlow {
        var count = 0

        // Read all TBCA foods
        val remoteFoods = tbcaRepository.readAllFoods()

        // Import in a transaction
        transactionProvider.withTransaction {
            remoteFoods.forEach { remoteProduct ->
                try {
                    importProduct(remoteProduct)
                    send(++count)
                } catch (e: Exception) {
                    // Log but continue importing other foods
                    println("Warning: Failed to import TBCA food '${remoteProduct.name}': ${e.message}")
                }
            }
        }
    }

    private suspend fun importProduct(remoteProduct: RemoteProduct) {
        val productId = productRepository.insertProduct(
            name = remoteProduct.name ?: error("Product name is required"),
            brand = remoteProduct.brand,
            barcode = remoteProduct.barcode,
            note = null,
            isLiquid = remoteProduct.isLiquid,
            packageWeight = remoteProduct.packageWeight,
            servingWeight = remoteProduct.servingWeight,
            source = remoteProduct.source,
            nutritionFacts = remoteProduct.nutritionFacts.toNutritionFacts(),
        )

        // Record in history
        historyRepository.insert(
          productId,
          FoodHistory.Imported(timestamp = dateProvider.nowInstant()),
        )
      }

    /**
     * Converts RemoteNutritionFacts to NutritionFacts using NutrientValue.
     */
    private fun com.maksimowiczm.foodyou.food.domain.entity.RemoteNutritionFacts?.toNutritionFacts(): NutritionFacts {
        if (this == null) return NutritionFacts.Empty

        return NutritionFacts.requireAll(
            proteins = proteins.toNutrientValue(),
            carbohydrates = carbohydrates.toNutrientValue(),
            energy = energy.toNutrientValue(),
            fats = fats.toNutrientValue(),
            saturatedFats = saturatedFats.toNutrientValue(),
            transFats = transFats.toNutrientValue(),
            monounsaturatedFats = monounsaturatedFats.toNutrientValue(),
            polyunsaturatedFats = polyunsaturatedFats.toNutrientValue(),
            omega3 = omega3.toNutrientValue(),
            omega6 = omega6.toNutrientValue(),
            sugars = sugars.toNutrientValue(),
            addedSugars = addedSugars.toNutrientValue(),
            dietaryFiber = dietaryFiber.toNutrientValue(),
            solubleFiber = solubleFiber.toNutrientValue(),
            insolubleFiber = insolubleFiber.toNutrientValue(),
            salt = salt.toNutrientValue(),
            cholesterol = cholesterol.toNutrientValue(),
            caffeine = caffeine.toNutrientValue(),
            vitaminA = vitaminA.toNutrientValue(),
            vitaminB1 = vitaminB1.toNutrientValue(),
            vitaminB2 = vitaminB2.toNutrientValue(),
            vitaminB3 = vitaminB3.toNutrientValue(),
            vitaminB5 = vitaminB5.toNutrientValue(),
            vitaminB6 = vitaminB6.toNutrientValue(),
            vitaminB7 = vitaminB7.toNutrientValue(),
            vitaminB9 = vitaminB9.toNutrientValue(),
            vitaminB12 = vitaminB12.toNutrientValue(),
            vitaminC = vitaminC.toNutrientValue(),
            vitaminD = vitaminD.toNutrientValue(),
            vitaminE = vitaminE.toNutrientValue(),
            vitaminK = vitaminK.toNutrientValue(),
            manganese = manganese.toNutrientValue(),
            magnesium = magnesium.toNutrientValue(),
            potassium = potassium.toNutrientValue(),
            calcium = calcium.toNutrientValue(),
            copper = copper.toNutrientValue(),
            zinc = zinc.toNutrientValue(),
            sodium = sodium.toNutrientValue(),
            iron = iron.toNutrientValue(),
            phosphorus = phosphorus.toNutrientValue(),
            selenium = selenium.toNutrientValue(),
            iodine = iodine.toNutrientValue(),
            chromium = chromium.toNutrientValue(),
        )
    }
}

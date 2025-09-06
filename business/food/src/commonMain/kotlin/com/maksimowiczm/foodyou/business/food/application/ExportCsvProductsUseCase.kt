package com.maksimowiczm.foodyou.business.food.application

import com.maksimowiczm.foodyou.business.food.domain.ProductField
import com.maksimowiczm.foodyou.core.food.domain.entity.Product
import com.maksimowiczm.foodyou.core.food.domain.repository.ProductRepository
import com.maksimowiczm.foodyou.core.shared.database.TransactionProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.first

fun interface ExportCsvProductsUseCase {
    /**
     * Exports products to CSV format with specified fields.
     *
     * @param fields List of [ProductField] to include in the export.
     * @return A [Flow] emitting lines of the CSV as [String].
     */
    suspend fun export(fields: List<ProductField>): Flow<String>
}

internal class ExportCsvProductsUseCaseImpl(
    private val productRepository: ProductRepository,
    private val transactionProvider: TransactionProvider,
) : ExportCsvProductsUseCase {
    override suspend fun export(fields: List<ProductField>): Flow<String> = channelFlow {
        val csvWriter = CsvWriter()

        val header =
            fields
                .map(ProductField::toCsvHeader)
                .joinToString(",", transform = csvWriter::writeString)
        send(header)

        transactionProvider.withTransaction {
            var offset = 0
            while (true) {
                val products = productRepository.observeProducts(PAGE_SIZE, offset).first()
                if (products.isEmpty()) break

                for (product in products) {
                    val csvLine =
                        fields.joinToString(separator = ",") { field ->
                            csvWriter.write(product.field(field))
                        }

                    send(csvLine)
                }

                offset += PAGE_SIZE
            }
        }
    }

    private companion object {
        const val PAGE_SIZE = 250
    }
}

private class CsvWriter {
    fun writeString(value: String): String = "\"$value\""

    fun writeDouble(value: Double): String = value.toString()

    fun writeBoolean(value: Boolean): String = if (value) "1" else "0"

    fun write(value: Any?): String =
        when (value) {
            is String -> writeString(value)
            is Double -> writeDouble(value)
            is Boolean -> writeBoolean(value)
            null -> ""
            else -> error("Unsupported type for CSV export: ${value::class.simpleName}")
        }
}

private fun ProductField.toCsvHeader(): String =
    when (this) {
        ProductField.Name -> "Name"
        ProductField.Brand -> "Brand"
        ProductField.Barcode -> "Barcode"
        ProductField.Note -> "Note"
        ProductField.IsLiquid -> "Is Liquid"
        ProductField.PackageWeight -> "Package Weight (g)"
        ProductField.ServingWeight -> "Serving Weight (g)"
        ProductField.SourceUrl -> "Source URL"
        ProductField.Proteins -> "Proteins (g)"
        ProductField.Carbohydrates -> "Carbohydrates (g)"
        ProductField.Energy -> "Energy (kcal)"
        ProductField.Fats -> "Fats (g)"
        ProductField.SaturatedFats -> "Saturated Fats (g)"
        ProductField.TransFats -> "Trans Fats (g)"
        ProductField.MonounsaturatedFats -> "Monounsaturated Fats (g)"
        ProductField.PolyunsaturatedFats -> "Polyunsaturated Fats (g)"
        ProductField.Omega3 -> "Omega-3 (g)"
        ProductField.Omega6 -> "Omega-6 (g)"
        ProductField.Sugars -> "Sugars (g)"
        ProductField.AddedSugars -> "Added Sugars (g)"
        ProductField.DietaryFiber -> "Dietary Fiber (g)"
        ProductField.SolubleFiber -> "Soluble Fiber (g)"
        ProductField.InsolubleFiber -> "Insoluble Fiber (g)"
        ProductField.Salt -> "Salt (g)"
        ProductField.Cholesterol -> "Cholesterol (g)"
        ProductField.Caffeine -> "Caffeine (g)"
        ProductField.VitaminA -> "Vitamin A (g)"
        ProductField.VitaminB1 -> "Vitamin B1 (g)"
        ProductField.VitaminB2 -> "Vitamin B2 (g)"
        ProductField.VitaminB3 -> "Vitamin B3 (g)"
        ProductField.VitaminB5 -> "Vitamin B5 (g)"
        ProductField.VitaminB6 -> "Vitamin B6 (g)"
        ProductField.VitaminB7 -> "Vitamin B7 (g)"
        ProductField.VitaminB9 -> "Vitamin B9 (g)"
        ProductField.VitaminB12 -> "Vitamin B12 (g)"
        ProductField.VitaminC -> "Vitamin C (g)"
        ProductField.VitaminD -> "Vitamin D (g)"
        ProductField.VitaminE -> "Vitamin E (g)"
        ProductField.VitaminK -> "Vitamin K (g)"
        ProductField.Manganese -> "Manganese (g)"
        ProductField.Magnesium -> "Magnesium (g)"
        ProductField.Potassium -> "Potassium (g)"
        ProductField.Calcium -> "Calcium (g)"
        ProductField.Copper -> "Copper (g)"
        ProductField.Zinc -> "Zinc (g)"
        ProductField.Sodium -> "Sodium (g)"
        ProductField.Iron -> "Iron (g)"
        ProductField.Phosphorus -> "Phosphorus (g)"
        ProductField.Selenium -> "Selenium (g)"
        ProductField.Iodine -> "Iodine (g)"
        ProductField.Chromium -> "Chromium (g)"
    }

private fun Product.field(field: ProductField): Any? =
    when (field) {
        ProductField.Name -> name
        ProductField.Brand -> brand
        ProductField.Barcode -> barcode
        ProductField.Note -> note
        ProductField.IsLiquid -> isLiquid
        ProductField.PackageWeight -> packageWeight
        ProductField.ServingWeight -> servingWeight
        ProductField.SourceUrl -> source.url
        ProductField.Proteins -> nutritionFacts.proteins.value
        ProductField.Carbohydrates -> nutritionFacts.carbohydrates.value
        ProductField.Energy -> nutritionFacts.energy.value
        ProductField.Fats -> nutritionFacts.fats.value
        ProductField.SaturatedFats -> nutritionFacts.saturatedFats.value
        ProductField.TransFats -> nutritionFacts.transFats.value
        ProductField.MonounsaturatedFats -> nutritionFacts.monounsaturatedFats.value
        ProductField.PolyunsaturatedFats -> nutritionFacts.polyunsaturatedFats.value
        ProductField.Omega3 -> nutritionFacts.omega3.value
        ProductField.Omega6 -> nutritionFacts.omega6.value
        ProductField.Sugars -> nutritionFacts.sugars.value
        ProductField.AddedSugars -> nutritionFacts.addedSugars.value
        ProductField.DietaryFiber -> nutritionFacts.dietaryFiber.value
        ProductField.SolubleFiber -> nutritionFacts.solubleFiber.value
        ProductField.InsolubleFiber -> nutritionFacts.insolubleFiber.value
        ProductField.Salt -> nutritionFacts.salt.value
        ProductField.Cholesterol -> nutritionFacts.cholesterol.value
        ProductField.Caffeine -> nutritionFacts.caffeine.value
        ProductField.VitaminA -> nutritionFacts.vitaminA.value
        ProductField.VitaminB1 -> nutritionFacts.vitaminB1.value
        ProductField.VitaminB2 -> nutritionFacts.vitaminB2.value
        ProductField.VitaminB3 -> nutritionFacts.vitaminB3.value
        ProductField.VitaminB5 -> nutritionFacts.vitaminB5.value
        ProductField.VitaminB6 -> nutritionFacts.vitaminB6.value
        ProductField.VitaminB7 -> nutritionFacts.vitaminB7.value
        ProductField.VitaminB9 -> nutritionFacts.vitaminB9.value
        ProductField.VitaminB12 -> nutritionFacts.vitaminB12.value
        ProductField.VitaminC -> nutritionFacts.vitaminC.value
        ProductField.VitaminD -> nutritionFacts.vitaminD.value
        ProductField.VitaminE -> nutritionFacts.vitaminE.value
        ProductField.VitaminK -> nutritionFacts.vitaminK.value
        ProductField.Manganese -> nutritionFacts.manganese.value
        ProductField.Magnesium -> nutritionFacts.magnesium.value
        ProductField.Potassium -> nutritionFacts.potassium.value
        ProductField.Calcium -> nutritionFacts.calcium.value
        ProductField.Copper -> nutritionFacts.copper.value
        ProductField.Zinc -> nutritionFacts.zinc.value
        ProductField.Sodium -> nutritionFacts.sodium.value
        ProductField.Iron -> nutritionFacts.iron.value
        ProductField.Phosphorus -> nutritionFacts.phosphorus.value
        ProductField.Selenium -> nutritionFacts.selenium.value
        ProductField.Iodine -> nutritionFacts.iodine.value
        ProductField.Chromium -> nutritionFacts.chromium.value
    }

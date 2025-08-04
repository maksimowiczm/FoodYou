package com.maksimowiczm.foodyou.feature.importexport.domain.csv

import com.maksimowiczm.foodyou.core.ext.now
import com.maksimowiczm.foodyou.feature.food.domain.CreateProductUseCase
import com.maksimowiczm.foodyou.feature.food.domain.FoodEvent
import com.maksimowiczm.foodyou.feature.food.domain.FoodSource
import com.maksimowiczm.foodyou.feature.food.domain.NutrientValue.Companion.toNutrientValue
import com.maksimowiczm.foodyou.feature.food.domain.NutritionFacts
import com.maksimowiczm.foodyou.feature.importexport.domain.ProductField
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.chunked
import kotlinx.coroutines.flow.map
import kotlinx.datetime.LocalDateTime

interface ImportCsvProductsUseCase {

    /**
     * Imports products from a CSV file represented as a Flow of lines.
     *
     * @param fieldOrder The order of fields in the CSV file.
     * @param csvLines A Flow of CSV lines to be imported.
     * @param source The source of the products being imported, be aware that this will override the
     * source url from the CSV file.
     * @return The number of lines processed successfully.
     */
    suspend fun import(
        fieldOrder: List<ProductField>,
        csvLines: Flow<String>,
        source: FoodSource?
    ): Int

    /**
     * Imports products from a CSV file represented as a Flow of lines, providing feedback on the
     * import process.
     *
     * @param fieldOrder The order of fields in the CSV file.
     * @param csvLines A Flow of CSV lines to be imported.
     * @param source The source of the products being imported, be aware that this will override the
     * source url from the CSV file.
     * @return A Flow emitting the number of lines processed successfully
     */
    suspend fun importWithFeedback(
        fieldOrder: List<ProductField>,
        csvLines: Flow<String>,
        source: FoodSource?
    ): Flow<Int>
}

internal class ImportCsvProductsUseCaseImpl(
    private val createProductUseCase: CreateProductUseCase
) : ImportCsvProductsUseCase {

    @OptIn(ExperimentalCoroutinesApi::class)
    override suspend fun import(
        fieldOrder: List<ProductField>,
        csvLines: Flow<String>,
        source: FoodSource?
    ): Int {
        var count = 0
        val parser = CsvParser()
        val now = LocalDateTime.now()

        csvLines.chunked(CHUNK_SIZE).collect { line ->
            val importedProducts = line
                .filter { it.isNotBlank() }
                .map { csvLine ->
                    csvLine.lineToProduct(parser, fieldOrder)
                }.map { fieldMap ->
                    val sourceType = source?.type ?: FoodSource.Type.User
                    val sourceUrl = source?.url ?: fieldMap[ProductField.SOURCE_URL]
                    val name = fieldMap[ProductField.NAME] ?: error("Product name is required")

                    createProductUseCase.createUnique(
                        name = name,
                        brand = fieldMap[ProductField.BRAND],
                        barcode = fieldMap[ProductField.BARCODE],
                        nutritionFacts = fieldMap.toNutritionFacts(),
                        packageWeight = fieldMap[ProductField.PACKAGE_WEIGHT]?.toFloatOrNull(),
                        servingWeight = fieldMap[ProductField.SERVING_WEIGHT]?.toFloatOrNull(),
                        note = fieldMap[ProductField.NOTE],
                        source = FoodSource(
                            type = sourceType,
                            url = sourceUrl
                        ),
                        isLiquid = fieldMap[ProductField.IS_LIQUID]?.toBooleanStrictOrNull()
                            ?: false,
                        event = FoodEvent.Imported(now)
                    )
                }

            count += importedProducts.size
        }

        return count
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override suspend fun importWithFeedback(
        fieldOrder: List<ProductField>,
        csvLines: Flow<String>,
        source: FoodSource?
    ): Flow<Int> = csvLines.chunked(CHUNK_SIZE).map { lines ->
        import(fieldOrder, lines.asFlow(), source)
    }

    private fun String.lineToProduct(
        csvParser: CsvParser,
        fieldOrder: List<ProductField>
    ): Map<ProductField, String?> {
        val split = csvParser.parseLine(this)
        check(split.size == fieldOrder.size) {
            "CSV line does not match field order size: ${split.size} != ${fieldOrder.size}"
        }

        val map = fieldOrder
            .zip(split)
            .associate { (field, value) ->
                field to value?.trim()
            }

        return map
    }

    private companion object {
        const val CHUNK_SIZE = 200
    }
}

private fun Map<ProductField, String?>.toNutritionFacts() = NutritionFacts(
    proteins = this[ProductField.PROTEINS]?.toFloatOrNull().toNutrientValue(),
    carbohydrates = this[ProductField.CARBOHYDRATES]?.toFloatOrNull().toNutrientValue(),
    energy = this[ProductField.ENERGY]?.toFloatOrNull().toNutrientValue(),
    fats = this[ProductField.FATS]?.toFloatOrNull().toNutrientValue(),
    saturatedFats = this[ProductField.SATURATED_FATS]?.toFloatOrNull().toNutrientValue(),
    transFats = this[ProductField.TRANS_FATS]?.toFloatOrNull().toNutrientValue(),
    monounsaturatedFats = this[ProductField.MONOUNSATURATED_FATS]?.toFloatOrNull()
        .toNutrientValue(),
    polyunsaturatedFats = this[ProductField.POLYUNSATURATED_FATS]?.toFloatOrNull()
        .toNutrientValue(),
    omega3 = this[ProductField.OMEGA3]?.toFloatOrNull().toNutrientValue(),
    omega6 = this[ProductField.OMEGA6]?.toFloatOrNull().toNutrientValue(),
    sugars = this[ProductField.SUGARS]?.toFloatOrNull().toNutrientValue(),
    addedSugars = this[ProductField.ADDED_SUGARS]?.toFloatOrNull().toNutrientValue(),
    dietaryFiber = this[ProductField.DIETARY_FIBER]?.toFloatOrNull().toNutrientValue(),
    solubleFiber = this[ProductField.SOLUBLE_FIBER]?.toFloatOrNull().toNutrientValue(),
    insolubleFiber = this[ProductField.INSOLUBLE_FIBER]?.toFloatOrNull().toNutrientValue(),
    salt = this[ProductField.SALT]?.toFloatOrNull().toNutrientValue(),
    cholesterolMilli = this[ProductField.CHOLESTEROL_MILLI]?.toFloatOrNull().toNutrientValue(),
    caffeineMilli = this[ProductField.CAFFEINE_MILLI]?.toFloatOrNull().toNutrientValue(),
    vitaminAMicro = this[ProductField.VITAMIN_A_MICRO]?.toFloatOrNull().toNutrientValue(),
    vitaminB1Milli = this[ProductField.VITAMIN_B1_MILLI]?.toFloatOrNull().toNutrientValue(),
    vitaminB2Milli = this[ProductField.VITAMIN_B2_MILLI]?.toFloatOrNull().toNutrientValue(),
    vitaminB3Milli = this[ProductField.VITAMIN_B3_MILLI]?.toFloatOrNull().toNutrientValue(),
    vitaminB5Milli = this[ProductField.VITAMIN_B5_MILLI]?.toFloatOrNull().toNutrientValue(),
    vitaminB6Milli = this[ProductField.VITAMIN_B6_MILLI]?.toFloatOrNull().toNutrientValue(),
    vitaminB7Micro = this[ProductField.VITAMIN_B7_MICRO]?.toFloatOrNull().toNutrientValue(),
    vitaminB9Micro = this[ProductField.VITAMIN_B9_MICRO]?.toFloatOrNull().toNutrientValue(),
    vitaminB12Micro = this[ProductField.VITAMIN_B12_MICRO]?.toFloatOrNull().toNutrientValue(),
    vitaminCMilli = this[ProductField.VITAMIN_C_MILLI]?.toFloatOrNull().toNutrientValue(),
    vitaminDMicro = this[ProductField.VITAMIN_D_MICRO]?.toFloatOrNull().toNutrientValue(),
    vitaminEMilli = this[ProductField.VITAMIN_E_MILLI]?.toFloatOrNull().toNutrientValue(),
    vitaminKMicro = this[ProductField.VITAMIN_K_MICRO]?.toFloatOrNull().toNutrientValue(),
    manganeseMilli = this[ProductField.MANGANESE_MILLI]?.toFloatOrNull().toNutrientValue(),
    magnesiumMilli = this[ProductField.MAGNESIUM_MILLI]?.toFloatOrNull().toNutrientValue(),
    potassiumMilli = this[ProductField.POTASSIUM_MILLI]?.toFloatOrNull().toNutrientValue(),
    calciumMilli = this[ProductField.CALCIUM_MILLI]?.toFloatOrNull().toNutrientValue(),
    copperMilli = this[ProductField.COPPER_MILLI]?.toFloatOrNull().toNutrientValue(),
    zincMilli = this[ProductField.ZINC_MILLI]?.toFloatOrNull().toNutrientValue(),
    sodiumMilli = this[ProductField.SODIUM_MILLI]?.toFloatOrNull().toNutrientValue(),
    ironMilli = this[ProductField.IRON_MILLI]?.toFloatOrNull().toNutrientValue(),
    phosphorusMilli = this[ProductField.PHOSPHORUS_MILLI]?.toFloatOrNull().toNutrientValue(),
    seleniumMicro = this[ProductField.SELENIUM_MICRO]?.toFloatOrNull().toNutrientValue(),
    iodineMicro = this[ProductField.IODINE_MICRO]?.toFloatOrNull().toNutrientValue(),
    chromiumMicro = this[ProductField.CHROMIUM_MICRO]?.toFloatOrNull().toNutrientValue()
)

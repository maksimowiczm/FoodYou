package com.maksimowiczm.foodyou.business.food.application

import com.maksimowiczm.foodyou.business.food.domain.ProductField
import com.maksimowiczm.foodyou.business.shared.application.csv.CsvParser
import com.maksimowiczm.foodyou.core.food.domain.entity.FoodHistory
import com.maksimowiczm.foodyou.core.food.domain.entity.FoodId
import com.maksimowiczm.foodyou.core.food.domain.entity.Product
import com.maksimowiczm.foodyou.core.food.domain.repository.FoodHistoryRepository
import com.maksimowiczm.foodyou.core.food.domain.repository.ProductRepository
import com.maksimowiczm.foodyou.core.shared.database.TransactionProvider
import com.maksimowiczm.foodyou.core.shared.date.DateProvider
import com.maksimowiczm.foodyou.core.shared.food.FoodSource
import com.maksimowiczm.foodyou.core.shared.food.NutrientValue.Companion.toNutrientValue
import com.maksimowiczm.foodyou.core.shared.food.NutritionFacts
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow

fun interface ImportCsvProductUseCase {

    /**
     * Imports products from CSV lines using the provided mapper and source type.
     *
     * @param mapper A list of [ProductField] that defines the mapping of CSV columns to product
     *   fields. The list can contain null values for columns that should be ignored.
     * @param lines A [Flow] of [String] representing the lines of the CSV file to be imported.
     * @param source The [FoodSource.Type] indicating the source of the food data.
     * @return A [Flow] of [Int] representing the number of products successfully imported.
     */
    suspend fun import(
        mapper: List<ProductField?>,
        lines: Flow<String>,
        source: FoodSource.Type,
    ): Flow<Int>
}

internal class ImportCsvProductUseCaseImpl(
    private val transactionProvider: TransactionProvider,
    private val productRepository: ProductRepository,
    private val historyRepository: FoodHistoryRepository,
    private val dateProvider: DateProvider,
    private val csvParser: CsvParser,
) : ImportCsvProductUseCase {

    override suspend fun import(
        mapper: List<ProductField?>,
        lines: Flow<String>,
        source: FoodSource.Type,
    ): Flow<Int> = channelFlow {
        var count = 1
        transactionProvider.withTransaction {
            lines.collect { line ->
                processLine(mapper = mapper, source = source, line = line)
                send(count++)
            }
        }
    }

    private suspend fun processLine(
        mapper: List<ProductField?>,
        source: FoodSource.Type,
        line: String,
    ) {
        val values = csvParser.parseLine(line)

        if (values.size != mapper.size) {
            error("Invalid number of columns in CSV line: $line")
        }

        val productData = mapper.zip(values).associate { (field, value) -> field to value }

        val product =
            Product(
                id = FoodId.Product(0),
                name = productData[ProductField.Name] ?: error("Name is required"),
                brand = productData[ProductField.Brand],
                barcode = productData[ProductField.Barcode],
                note = productData[ProductField.Note],
                isLiquid = productData[ProductField.IsLiquid]?.toBooleanStrict() ?: false,
                packageWeight = productData[ProductField.PackageWeight]?.toDouble(),
                servingWeight = productData[ProductField.ServingWeight]?.toDouble(),
                source = FoodSource(type = source, url = productData[ProductField.SourceUrl]),
                nutritionFacts =
                    NutritionFacts.requireAll(
                        proteins = productData[ProductField.Proteins]?.toDouble().toNutrientValue(),
                        carbohydrates =
                            productData[ProductField.Carbohydrates]?.toDouble().toNutrientValue(),
                        energy = productData[ProductField.Energy]?.toDouble().toNutrientValue(),
                        fats = productData[ProductField.Fats]?.toDouble().toNutrientValue(),
                        saturatedFats =
                            productData[ProductField.SaturatedFats]?.toDouble().toNutrientValue(),
                        transFats =
                            productData[ProductField.TransFats]?.toDouble().toNutrientValue(),
                        monounsaturatedFats =
                            productData[ProductField.MonounsaturatedFats]
                                ?.toDouble()
                                .toNutrientValue(),
                        polyunsaturatedFats =
                            productData[ProductField.PolyunsaturatedFats]
                                ?.toDouble()
                                .toNutrientValue(),
                        omega3 = productData[ProductField.Omega3]?.toDouble().toNutrientValue(),
                        omega6 = productData[ProductField.Omega6]?.toDouble().toNutrientValue(),
                        sugars = productData[ProductField.Sugars]?.toDouble().toNutrientValue(),
                        addedSugars =
                            productData[ProductField.AddedSugars]?.toDouble().toNutrientValue(),
                        dietaryFiber =
                            productData[ProductField.DietaryFiber]?.toDouble().toNutrientValue(),
                        solubleFiber =
                            productData[ProductField.SolubleFiber]?.toDouble().toNutrientValue(),
                        insolubleFiber =
                            productData[ProductField.InsolubleFiber]?.toDouble().toNutrientValue(),
                        salt = productData[ProductField.Salt]?.toDouble().toNutrientValue(),
                        cholesterol =
                            productData[ProductField.Cholesterol]?.toDouble().toNutrientValue(),
                        caffeine = productData[ProductField.Caffeine]?.toDouble().toNutrientValue(),
                        vitaminA = productData[ProductField.VitaminA]?.toDouble().toNutrientValue(),
                        vitaminB1 =
                            productData[ProductField.VitaminB1]?.toDouble().toNutrientValue(),
                        vitaminB2 =
                            productData[ProductField.VitaminB2]?.toDouble().toNutrientValue(),
                        vitaminB3 =
                            productData[ProductField.VitaminB3]?.toDouble().toNutrientValue(),
                        vitaminB5 =
                            productData[ProductField.VitaminB5]?.toDouble().toNutrientValue(),
                        vitaminB6 =
                            productData[ProductField.VitaminB6]?.toDouble().toNutrientValue(),
                        vitaminB7 =
                            productData[ProductField.VitaminB7]?.toDouble().toNutrientValue(),
                        vitaminB9 =
                            productData[ProductField.VitaminB9]?.toDouble().toNutrientValue(),
                        vitaminB12 =
                            productData[ProductField.VitaminB12]?.toDouble().toNutrientValue(),
                        vitaminC = productData[ProductField.VitaminC]?.toDouble().toNutrientValue(),
                        vitaminD = productData[ProductField.VitaminD]?.toDouble().toNutrientValue(),
                        vitaminE = productData[ProductField.VitaminE]?.toDouble().toNutrientValue(),
                        vitaminK = productData[ProductField.VitaminK]?.toDouble().toNutrientValue(),
                        manganese =
                            productData[ProductField.Manganese]?.toDouble().toNutrientValue(),
                        magnesium =
                            productData[ProductField.Magnesium]?.toDouble().toNutrientValue(),
                        potassium =
                            productData[ProductField.Potassium]?.toDouble().toNutrientValue(),
                        calcium = productData[ProductField.Calcium]?.toDouble().toNutrientValue(),
                        copper = productData[ProductField.Copper]?.toDouble().toNutrientValue(),
                        zinc = productData[ProductField.Zinc]?.toDouble().toNutrientValue(),
                        sodium = productData[ProductField.Sodium]?.toDouble().toNutrientValue(),
                        iron = productData[ProductField.Iron]?.toDouble().toNutrientValue(),
                        phosphorus =
                            productData[ProductField.Phosphorus]?.toDouble().toNutrientValue(),
                        selenium = productData[ProductField.Selenium]?.toDouble().toNutrientValue(),
                        iodine = productData[ProductField.Iodine]?.toDouble().toNutrientValue(),
                        chromium = productData[ProductField.Chromium]?.toDouble().toNutrientValue(),
                    ),
            )

        val id =
            productRepository.insertUniqueProduct(
                name = product.name,
                brand = product.brand,
                barcode = product.barcode,
                note = product.note,
                isLiquid = product.isLiquid,
                packageWeight = product.packageWeight,
                servingWeight = product.servingWeight,
                source = product.source,
                nutritionFacts = product.nutritionFacts,
            )

        if (id != null) {
            historyRepository.insert(id, FoodHistory.Imported(date = dateProvider.now()))
        }
    }
}

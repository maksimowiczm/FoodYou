package com.maksimowiczm.foodyou.business.food.application.command

import com.maksimowiczm.foodyou.business.food.domain.Product
import com.maksimowiczm.foodyou.business.food.domain.ProductField
import com.maksimowiczm.foodyou.business.food.infrastructure.persistence.LocalProductDataSource
import com.maksimowiczm.foodyou.business.shared.domain.food.FoodSource
import com.maksimowiczm.foodyou.business.shared.domain.infrastructure.csv.CsvParser
import com.maksimowiczm.foodyou.business.shared.domain.infrastructure.persistence.DatabaseTransactionProvider
import com.maksimowiczm.foodyou.business.shared.domain.nutrients.NutrientValue.Companion.toNutrientValue
import com.maksimowiczm.foodyou.business.shared.domain.nutrients.NutritionFacts
import com.maksimowiczm.foodyou.shared.common.domain.food.FoodId
import com.maksimowiczm.foodyou.shared.common.domain.infrastructure.command.Command
import com.maksimowiczm.foodyou.shared.common.domain.infrastructure.command.CommandHandler
import com.maksimowiczm.foodyou.shared.common.domain.result.Ok
import com.maksimowiczm.foodyou.shared.common.domain.result.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow

/**
 * Command to import products from a CSV file.
 *
 * @property mapper A map that defines how CSV columns map to product fields.
 * @property lines A flow of lines from the CSV file.
 *
 * Results in a flow of integers representing the number of products successfully imported.
 */
data class ImportCsvProductsCommand(
    val mapper: List<ProductField?>,
    val lines: Flow<String>,
    val source: FoodSource.Type,
) : Command<Flow<Int>, Unit>

internal class ImportCsvProductsCommandHandler(
    private val transactionProvider: DatabaseTransactionProvider,
    private val localProduct: LocalProductDataSource,
    private val csvParser: CsvParser,
) : CommandHandler<ImportCsvProductsCommand, Flow<Int>, Unit> {
    override suspend fun handle(command: ImportCsvProductsCommand): Result<Flow<Int>, Unit> =
        channelFlow {
                var count = 1
                transactionProvider.withTransaction {
                    command.lines.collect {
                        processLine(command, it)
                        send(count++)
                    }
                }
            }
            .let(::Ok)

    private suspend fun processLine(command: ImportCsvProductsCommand, line: String) {
        val values = csvParser.parseLine(line)

        if (values.size != command.mapper.size) {
            error("Invalid number of columns in CSV line: $line")
        }

        val productData = command.mapper.zip(values).associate { (field, value) -> field to value }

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
                source =
                    FoodSource(type = command.source, url = productData[ProductField.SourceUrl]),
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

        localProduct.insertUniqueProduct(product)
    }
}

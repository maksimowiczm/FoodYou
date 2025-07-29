package com.maksimowiczm.foodyou.feature.importexport.domain.csv

import com.maksimowiczm.foodyou.core.ext.now
import com.maksimowiczm.foodyou.feature.food.domain.CreateProductUseCase
import com.maksimowiczm.foodyou.feature.food.domain.FoodEvent
import com.maksimowiczm.foodyou.feature.food.domain.FoodSource
import com.maksimowiczm.foodyou.feature.food.domain.ProductMapper
import com.maksimowiczm.foodyou.feature.importexport.domain.ProductField
import com.maksimowiczm.foodyou.feature.importexport.domain.ProductFieldMapMapper
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
    private val mapper: ProductFieldMapMapper,
    private val createProductUseCase: CreateProductUseCase,
    private val productMapper: ProductMapper
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
                }.map { productMap ->
                    val entity = mapper.toProduct(
                        sourceType = source?.type ?: FoodSource.Type.User,
                        fieldMap = productMap
                    )

                    entity.copy(
                        sourceUrl = source?.url ?: entity.sourceUrl
                    )
                }.map {
                    val model = productMapper.toModel(it)
                    createProductUseCase.createUnique(
                        name = model.name,
                        brand = model.brand,
                        barcode = model.barcode,
                        nutritionFacts = model.nutritionFacts,
                        packageWeight = model.packageWeight,
                        servingWeight = model.servingWeight,
                        note = model.note,
                        source = FoodSource(
                            type = it.sourceType,
                            url = it.sourceUrl
                        ),
                        isLiquid = model.isLiquid,
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

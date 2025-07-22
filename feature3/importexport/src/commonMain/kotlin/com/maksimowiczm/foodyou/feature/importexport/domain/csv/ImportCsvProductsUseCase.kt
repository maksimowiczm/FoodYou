package com.maksimowiczm.foodyou.feature.importexport.domain.csv

import com.maksimowiczm.foodyou.feature.food.data.database.FoodDatabase
import com.maksimowiczm.foodyou.feature.food.domain.FoodSource
import com.maksimowiczm.foodyou.feature.importexport.domain.ProductField
import com.maksimowiczm.foodyou.feature.importexport.domain.ProductFieldMapMapper
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.chunked

fun interface ImportCsvProductsUseCase {

    /**
     * Imports products from a CSV file represented as a Flow of lines.
     *
     * @param fieldOrder The order of fields in the CSV file.
     * @param csvLines A Flow of CSV lines to be imported.
     * @param source The source of the products being imported, be aware that this will override the
     * source url from the CSV file.
     * @return The number of products successfully imported.
     */
    suspend fun import(
        fieldOrder: List<ProductField>,
        csvLines: Flow<String>,
        source: FoodSource?
    ): Int
}

internal class ImportCsvProductsUseCaseImpl(
    foodDatabase: FoodDatabase,
    private val mapper: ProductFieldMapMapper
) : ImportCsvProductsUseCase {

    private val productDao = foodDatabase.productDao

    @OptIn(ExperimentalCoroutinesApi::class)
    override suspend fun import(
        fieldOrder: List<ProductField>,
        csvLines: Flow<String>,
        source: FoodSource?
    ): Int {
        var count = 0
        val parser = CsvParser()

        csvLines.chunked(CHUNK_SIZE).collect { line ->
            val products = line
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
                }

            count += productDao.insertUniqueProducts(products)
        }

        return count
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

package com.maksimowiczm.foodyou.feature.importexport.domain.csv

import co.touchlab.kermit.Logger
import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import com.maksimowiczm.foodyou.core.database.food.ProductLocalDataSource
import java.io.InputStream
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow

fun interface ImportProductsUseCase {
    /**
     * @return [Flow] of the number of products imported.
     */
    suspend operator fun invoke(stream: InputStream): Flow<Int>
}

internal class ImportProductsUseCaseImpl(
    private val productLocalDataSource: ProductLocalDataSource,
    private val mapper: ProductCsvMapper = ProductCsvMapper
) : ImportProductsUseCase {
    override suspend fun invoke(stream: InputStream): Flow<Int> = channelFlow {
        var count = 0

        csvReader().openAsync(stream) {
            readAllWithHeaderAsSequence().forEachIndexed { index, row ->
                val enumMap =
                    row.map {
                        ProductEntityField.valueOf(it.key.uppercase()) to it.value
                    }.toMap()

                val product = mapper.toProductEntity(enumMap)

                if (product == null) {
                    Logger.w { "Row $index is invalid: $row" }
                    return@forEachIndexed
                }

                productLocalDataSource.insertProduct(product)

                count++
                send(count)
            }
        }
    }
}

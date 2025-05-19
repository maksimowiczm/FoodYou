package com.maksimowiczm.foodyou.feature.importexport.domain

import co.touchlab.kermit.Logger
import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import com.maksimowiczm.foodyou.core.data.model.product.ProductEntityField
import com.maksimowiczm.foodyou.core.domain.source.ProductLocalDataSource
import java.io.InputStream
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow

internal fun interface ImportProductsUseCase {
    /**
     * @return [Flow] of the number of products imported.
     */
    suspend operator fun invoke(stream: InputStream): Flow<Int>
}

internal class ImportProductsUseCaseImpl(
    private val productSource: ProductLocalDataSource,
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

                productSource.upsertProduct(product)

                count++
                send(count)
            }
        }
    }
}

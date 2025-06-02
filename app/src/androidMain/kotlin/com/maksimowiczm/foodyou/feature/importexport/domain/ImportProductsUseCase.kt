package com.maksimowiczm.foodyou.feature.importexport.domain

import java.io.InputStream
import kotlinx.coroutines.flow.Flow

internal fun interface ImportProductsUseCase {
    /**
     * @return [Flow] of the number of products imported.
     */
    suspend operator fun invoke(stream: InputStream): Flow<Int>
}

// internal class ImportProductsUseCaseImpl(
//    private val productSource: ProductLocalDataSource,
//    private val mapper: ProductCsvMapper = ProductCsvMapper
// ) : ImportProductsUseCase {
//    override suspend fun invoke(stream: InputStream): Flow<Int> = channelFlow {
//        var count = 0
//        csvReader().openAsync(stream) {
//            readAllWithHeaderAsSequence().forEachIndexed { index, row ->
//                val enumMap =
//                    row.map {
//                        ProductEntityField.valueOf(it.key.uppercase()) to it.value
//                    }.toMap()
//
//                val product = mapper.toProductEntity(enumMap)
//
//                if (product == null) {
//                    Logger.w { "Row $index is invalid: $row" }
//                    return@forEachIndexed
//                }
//
//                productSource.insertProduct(product)
//
//                count++
//                send(count)
//            }
//        }
//    }
// }

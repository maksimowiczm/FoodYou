package com.maksimowiczm.foodyou.feature.importexport.domain

import java.io.OutputStream
import kotlinx.coroutines.flow.Flow

data class ExportProgress(val progress: Int, val total: Int)

internal fun interface ExportProductsUseCase {
    suspend operator fun invoke(stream: OutputStream): Flow<ExportProgress>
}

// internal class ExportProductsUseCaseImpl(
//    private val productSource: ProductLocalDataSource,
//    private val mapper: ProductCsvMapper = ProductCsvMapper
// ) : ExportProductsUseCase {
//    override suspend fun invoke(stream: OutputStream): Flow<ExportProgress> = channelFlow {
//        val products = productSource.getProducts()
//        val max = products.size
//
//        csvWriter().openAsync(stream) {
//            writeRow(csvHeaderFields())
//
//            products.forEachIndexed { index, product ->
//                val row =
//                    mapper.toStringMap(product).toList().sortedBy { it.first }.map { it.second }
//
//                writeRow(row)
//
//                send(ExportProgress(index, max))
//            }
//        }
//    }
// }

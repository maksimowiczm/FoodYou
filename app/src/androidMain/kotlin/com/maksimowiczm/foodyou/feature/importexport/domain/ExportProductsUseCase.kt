package com.maksimowiczm.foodyou.feature.importexport.domain

import com.github.doyaaaaaken.kotlincsv.dsl.csvWriter
import com.maksimowiczm.foodyou.core.database.food.ProductLocalDataSource
import java.io.OutputStream
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.first

data class ExportProgress(val progress: Int, val total: Int)

internal fun interface ExportProductsUseCase {
    suspend operator fun invoke(stream: OutputStream): Flow<ExportProgress>
}

internal class ExportProductsUseCaseImpl(
    private val productSource: ProductLocalDataSource,
    private val mapper: ProductCsvMapper = ProductCsvMapper
) : ExportProductsUseCase {
    override suspend fun invoke(stream: OutputStream): Flow<ExportProgress> = channelFlow {
        val products = productSource.observeProducts().first()
        val max = products.size

        csvWriter().openAsync(stream) {
            writeRow(csvHeaderFields())

            products.forEachIndexed { index, product ->
                val row =
                    mapper.toStringMap(product).toList().sortedBy { it.first }.map { it.second }

                writeRow(row)

                send(ExportProgress(index, max))
            }
        }
    }
}

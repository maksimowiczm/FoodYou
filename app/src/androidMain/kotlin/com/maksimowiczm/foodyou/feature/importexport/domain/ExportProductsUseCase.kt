package com.maksimowiczm.foodyou.feature.importexport.domain

import java.io.InputStream
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

// Allow android imports because android source set

data class ExportProgress(val progress: Int, val total: Int)

internal fun interface ExportProductsUseCase {
    suspend operator fun invoke(stream: InputStream): Flow<ExportProgress>
}

internal class ExportProductsUseCaseImpl : ExportProductsUseCase {
    override suspend fun invoke(stream: InputStream): Flow<ExportProgress> = flow {
        val max = 15
        for (i in 0..max) {
            emit(ExportProgress(i, max))
            delay(1000L)
        }
    }
}

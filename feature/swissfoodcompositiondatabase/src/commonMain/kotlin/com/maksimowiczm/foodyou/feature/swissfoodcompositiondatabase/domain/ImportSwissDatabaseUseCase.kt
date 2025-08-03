package com.maksimowiczm.foodyou.feature.swissfoodcompositiondatabase.domain

import com.maksimowiczm.foodyou.feature.food.domain.FoodSource
import com.maksimowiczm.foodyou.feature.importexport.domain.ProductField
import com.maksimowiczm.foodyou.feature.importexport.domain.csv.ImportCsvProductsUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.flow

interface ImportSwissDatabaseUseCase {
    suspend fun import(languages: Set<Language>)
    suspend fun importWithFeedback(languages: Set<Language>): Flow<Int>
}

internal class ImportSwissDatabaseUseCaseImpl(
    private val importCsvProductsUseCase: ImportCsvProductsUseCase
) : ImportSwissDatabaseUseCase {
    override suspend fun import(languages: Set<Language>) {
        for (language in languages) {
            language.import { fieldOrder, content ->
                importCsvProductsUseCase.import(
                    fieldOrder = fieldOrder,
                    csvLines = content.asFlow(),
                    source = FoodSource(
                        type = FoodSource.Type.SwissFoodCompositionDatabase,
                        url = sourceUrl
                    )
                )
            }
        }
    }

    override suspend fun importWithFeedback(languages: Set<Language>): Flow<Int> = flow {
        var totalImported = 0

        for (language in languages) {
            language.import { fieldOrder, content ->
                importCsvProductsUseCase.importWithFeedback(
                    fieldOrder = fieldOrder,
                    csvLines = content.asFlow(),
                    source = FoodSource(
                        type = FoodSource.Type.SwissFoodCompositionDatabase,
                        url = sourceUrl
                    )
                )
            }.collect {
                totalImported += it
                emit(totalImported)
            }
        }
    }

    private suspend fun <T> Language.import(
        callback: suspend Language.(List<ProductField>, List<String>) -> T
    ): T {
        val bytes = this.readBytes()

        // Get lines from bytes
        val lines = bytes.decodeToString().lines()

        val header = lines.first()
        val content = lines.drop(1)

        val fieldOrder = header.split(",")
            .map { it.trim() }
            .map { fieldName ->
                val field = ProductField.entries.firstOrNull {
                    it.name.equals(
                        fieldName,
                        ignoreCase = true
                    )
                }

                field ?: error("Unknown field: $fieldName")
            }

        return callback(fieldOrder, content)
    }
}

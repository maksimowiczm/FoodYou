package com.maksimowiczm.foodyou.feature.swissfoodcompositiondatabase.domain

import com.maksimowiczm.foodyou.feature.food.domain.FoodSource
import com.maksimowiczm.foodyou.feature.importexport.domain.ProductField
import com.maksimowiczm.foodyou.feature.importexport.domain.csv.ImportCsvProductsUseCase
import kotlinx.coroutines.flow.asFlow

fun interface ImportSwissDatabaseUseCase {
    suspend fun import(languages: Set<Language>)
}

internal class ImportSwissDatabaseUseCaseImpl(
    private val importCsvProductsUseCase: ImportCsvProductsUseCase
) : ImportSwissDatabaseUseCase {
    override suspend fun import(languages: Set<Language>) {
        for (language in languages) {
            language.import()
        }
    }

    private suspend fun Language.import() {
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

        importCsvProductsUseCase.import(
            fieldOrder = fieldOrder,
            csvLines = content.asFlow(),
            source = FoodSource(
                type = FoodSource.Type.User,
                url = sourceUrl
            )
        )
    }
}

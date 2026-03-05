package com.maksimowiczm.foodyou.importexport.swissfoodcompositiondatabase.domain

import kotlinx.coroutines.flow.Flow

interface SwissFoodCompositionDatabaseRepository {
    enum class Language {
        ENGLISH,
        GERMAN,
        FRENCH,
        ITALIAN;

        val size = 1190
    }

    suspend fun readCsvFile(language: Language): Flow<Byte>
}

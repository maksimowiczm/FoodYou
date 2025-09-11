package com.maksimowiczm.foodyou.app.business.opensource.domain.importexport

interface SwissFoodCompositionDatabaseRepository {
    enum class Language {
        ENGLISH,
        GERMAN,
        FRENCH,
        ITALIAN;

        val size = 1190
    }

    suspend fun readCsvLines(language: Language): List<String>
}

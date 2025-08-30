package com.maksimowiczm.foodyou.business.food.domain

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

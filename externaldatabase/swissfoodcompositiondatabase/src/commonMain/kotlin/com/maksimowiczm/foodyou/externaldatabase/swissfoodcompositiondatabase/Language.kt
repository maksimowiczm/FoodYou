package com.maksimowiczm.foodyou.externaldatabase.swissfoodcompositiondatabase

import foodyou.externaldatabase.swissfoodcompositiondatabase.generated.resources.Res

enum class Language {
    ENGLISH,
    GERMAN,
    FRENCH,
    ITALIAN;

    suspend fun readBytes(): ByteArray =
        when (this) {
            ENGLISH -> Res.readBytes("files/swiss-food-composition-database/data.csv")
            GERMAN -> Res.readBytes("files/swiss-food-composition-database/data-de-DE.csv")
            FRENCH -> Res.readBytes("files/swiss-food-composition-database/data-fr-FR.csv")
            ITALIAN -> Res.readBytes("files/swiss-food-composition-database/data-it-IT.csv")
        }

    val size = 1190
}

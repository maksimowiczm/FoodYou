package com.maksimowiczm.foodyou.app.infrastructure.compose

import com.maksimowiczm.foodyou.app.business.opensource.domain.importexport.SwissFoodCompositionDatabaseRepository
import com.maksimowiczm.foodyou.app.business.opensource.domain.importexport.SwissFoodCompositionDatabaseRepository.Language.*
import foodyou.infrastructure.generated.resources.Res

internal class ComposeSwissFoodCompositionDatabaseRepository :
    SwissFoodCompositionDatabaseRepository {
    override suspend fun readCsvLines(
        language: SwissFoodCompositionDatabaseRepository.Language
    ): List<String> {
        val bytes =
            when (language) {
                ENGLISH -> Res.readBytes("files/swiss-food-composition-database/data.csv")
                GERMAN -> Res.readBytes("files/swiss-food-composition-database/data-de-DE.csv")
                FRENCH -> Res.readBytes("files/swiss-food-composition-database/data-fr-FR.csv")
                ITALIAN -> Res.readBytes("files/swiss-food-composition-database/data-it-IT.csv")
            }

        return bytes.decodeToString().split("\n").filterNot(String::isBlank)
    }
}

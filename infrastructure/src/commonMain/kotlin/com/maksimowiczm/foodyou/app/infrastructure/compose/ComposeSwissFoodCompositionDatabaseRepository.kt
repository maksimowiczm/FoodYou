package com.maksimowiczm.foodyou.app.infrastructure.compose

import com.maksimowiczm.foodyou.business.food.domain.SwissFoodCompositionDatabaseRepository
import com.maksimowiczm.foodyou.business.food.domain.SwissFoodCompositionDatabaseRepository.Language.ENGLISH
import com.maksimowiczm.foodyou.business.food.domain.SwissFoodCompositionDatabaseRepository.Language.FRENCH
import com.maksimowiczm.foodyou.business.food.domain.SwissFoodCompositionDatabaseRepository.Language.GERMAN
import com.maksimowiczm.foodyou.business.food.domain.SwissFoodCompositionDatabaseRepository.Language.ITALIAN
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

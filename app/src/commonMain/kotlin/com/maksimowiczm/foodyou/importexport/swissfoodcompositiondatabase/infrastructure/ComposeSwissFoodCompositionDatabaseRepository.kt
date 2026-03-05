package com.maksimowiczm.foodyou.importexport.swissfoodcompositiondatabase.infrastructure

import com.maksimowiczm.foodyou.app.generated.resources.Res
import com.maksimowiczm.foodyou.importexport.swissfoodcompositiondatabase.domain.SwissFoodCompositionDatabaseRepository
import com.maksimowiczm.foodyou.importexport.swissfoodcompositiondatabase.domain.SwissFoodCompositionDatabaseRepository.Language.ENGLISH
import com.maksimowiczm.foodyou.importexport.swissfoodcompositiondatabase.domain.SwissFoodCompositionDatabaseRepository.Language.FRENCH
import com.maksimowiczm.foodyou.importexport.swissfoodcompositiondatabase.domain.SwissFoodCompositionDatabaseRepository.Language.GERMAN
import com.maksimowiczm.foodyou.importexport.swissfoodcompositiondatabase.domain.SwissFoodCompositionDatabaseRepository.Language.ITALIAN
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow

internal class ComposeSwissFoodCompositionDatabaseRepository :
    SwissFoodCompositionDatabaseRepository {

    override suspend fun readCsvFile(
        language: SwissFoodCompositionDatabaseRepository.Language
    ): Flow<Byte> {
        val bytes =
            when (language) {
                ENGLISH -> Res.readBytes("files/swiss-food-composition-database/data.csv")
                GERMAN -> Res.readBytes("files/swiss-food-composition-database/data-de-DE.csv")
                FRENCH -> Res.readBytes("files/swiss-food-composition-database/data-fr-FR.csv")
                ITALIAN -> Res.readBytes("files/swiss-food-composition-database/data-it-IT.csv")
            }

        return bytes.toList().asFlow()
    }
}

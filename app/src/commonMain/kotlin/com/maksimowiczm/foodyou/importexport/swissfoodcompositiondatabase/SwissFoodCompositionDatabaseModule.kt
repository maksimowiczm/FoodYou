package com.maksimowiczm.foodyou.importexport.swissfoodcompositiondatabase

import com.maksimowiczm.foodyou.importexport.swissfoodcompositiondatabase.domain.ImportSwissFoodCompositionDatabaseUseCase
import com.maksimowiczm.foodyou.importexport.swissfoodcompositiondatabase.domain.ImportSwissFoodCompositionDatabaseUseCaseImpl
import com.maksimowiczm.foodyou.importexport.swissfoodcompositiondatabase.domain.SwissFoodCompositionDatabaseRepository
import com.maksimowiczm.foodyou.importexport.swissfoodcompositiondatabase.infrastructure.ComposeSwissFoodCompositionDatabaseRepository
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind
import org.koin.dsl.module

val importExportSwissFoodCompositionDatabaseModule = module {
    factoryOf(::ImportSwissFoodCompositionDatabaseUseCaseImpl)
        .bind<ImportSwissFoodCompositionDatabaseUseCase>()

    factoryOf(::ComposeSwissFoodCompositionDatabaseRepository)
        .bind<SwissFoodCompositionDatabaseRepository>()
}

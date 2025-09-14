package com.maksimowiczm.foodyou.app.infrastructure.opensource.importexport

import com.maksimowiczm.foodyou.app.business.opensource.domain.importexport.SwissFoodCompositionDatabaseRepository
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind

internal fun Module.importExportModule() {
    factoryOf(::ComposeSwissFoodCompositionDatabaseRepository)
        .bind<SwissFoodCompositionDatabaseRepository>()
}

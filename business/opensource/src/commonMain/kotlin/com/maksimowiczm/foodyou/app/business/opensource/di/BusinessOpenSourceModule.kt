package com.maksimowiczm.foodyou.app.business.opensource.di

import com.maksimowiczm.foodyou.app.business.opensource.domain.importexport.importExportModule
import org.koin.dsl.module

val businessOpenSourceModule = module {
    includeCoreUseCases()
    importExportModule()
}

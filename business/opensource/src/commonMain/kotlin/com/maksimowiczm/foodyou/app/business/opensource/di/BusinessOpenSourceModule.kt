package com.maksimowiczm.foodyou.app.business.opensource.di

import com.maksimowiczm.foodyou.app.business.opensource.domain.importexport.importExportModule
import com.maksimowiczm.foodyou.app.business.opensource.domain.poll.pollModule
import com.maksimowiczm.foodyou.app.business.opensource.domain.search.searchModule
import org.koin.dsl.module

val businessOpenSourceModule = module {
    includeCoreUseCases()
    importExportModule()
    pollModule()
    searchModule()
}

package com.maksimowiczm.foodyou.importexport

import com.maksimowiczm.foodyou.importexport.domain.importExportDomainModule
import com.maksimowiczm.foodyou.importexport.infrastructure.importExportInfrastructureModule
import org.koin.dsl.module

val importExportModule = module {
    importExportDomainModule()
    importExportInfrastructureModule()
}

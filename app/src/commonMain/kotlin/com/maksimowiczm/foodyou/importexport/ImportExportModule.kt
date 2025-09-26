package com.maksimowiczm.foodyou.importexport

import com.maksimowiczm.foodyou.importexport.domain.importExportDomainModule
import org.koin.dsl.module

val importExportModule = module { importExportDomainModule() }

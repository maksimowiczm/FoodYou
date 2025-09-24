package com.maksimowiczm.foodyou.importexport.domain

import com.maksimowiczm.foodyou.importexport.domain.usecase.ExportCsvProductsUseCase
import com.maksimowiczm.foodyou.importexport.domain.usecase.ExportCsvProductsUseCaseImpl
import com.maksimowiczm.foodyou.importexport.domain.usecase.ImportCsvProductUseCase
import com.maksimowiczm.foodyou.importexport.domain.usecase.ImportCsvProductUseCaseImpl
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind

internal fun Module.importExportDomainModule() {
    factoryOf(::ExportCsvProductsUseCaseImpl).bind<ExportCsvProductsUseCase>()
    factoryOf(::ImportCsvProductUseCaseImpl).bind<ImportCsvProductUseCase>()
}

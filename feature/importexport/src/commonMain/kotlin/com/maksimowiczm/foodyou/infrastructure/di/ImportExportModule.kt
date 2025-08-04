package com.maksimowiczm.foodyou.infrastructure.di

import com.maksimowiczm.foodyou.feature.importexport.domain.csv.ImportCsvProductsUseCase
import com.maksimowiczm.foodyou.feature.importexport.domain.csv.ImportCsvProductsUseCaseImpl
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind
import org.koin.dsl.module

val importExportModule = module {
    factoryOf(::ImportCsvProductsUseCaseImpl).bind<ImportCsvProductsUseCase>()
}

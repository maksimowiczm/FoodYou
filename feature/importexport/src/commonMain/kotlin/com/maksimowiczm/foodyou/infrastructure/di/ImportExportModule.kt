package com.maksimowiczm.foodyou.infrastructure.di

import com.maksimowiczm.foodyou.feature.importexport.domain.csv.ExportProductsUseCase
import com.maksimowiczm.foodyou.feature.importexport.domain.csv.ExportProductsUseCaseImpl
import com.maksimowiczm.foodyou.feature.importexport.domain.csv.ImportProductsUseCase
import com.maksimowiczm.foodyou.feature.importexport.domain.csv.ImportProductsUseCaseImpl
import org.koin.dsl.bind
import org.koin.dsl.module

val importExportModule = module {
    factory { ImportProductsUseCaseImpl(get()) }.bind<ImportProductsUseCase>()
    factory { ExportProductsUseCaseImpl(get()) }.bind<ExportProductsUseCase>()
}

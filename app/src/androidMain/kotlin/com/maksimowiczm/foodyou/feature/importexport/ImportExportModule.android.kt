package com.maksimowiczm.foodyou.feature.importexport

import com.maksimowiczm.foodyou.feature.importexport.data.BackupService
import com.maksimowiczm.foodyou.feature.importexport.domain.ExportProductsUseCase
import com.maksimowiczm.foodyou.feature.importexport.domain.ExportProductsUseCaseImpl
import com.maksimowiczm.foodyou.feature.importexport.domain.ImportProductsUseCase
import com.maksimowiczm.foodyou.feature.importexport.domain.ImportProductsUseCaseImpl
import com.maksimowiczm.foodyou.feature.importexport.ui.ImportExportViewModel
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

actual val importExportModule = module {
    viewModelOf(::ImportExportViewModel)

    factoryOf(::BackupService)

    factory { ExportProductsUseCaseImpl(get()) }.bind<ExportProductsUseCase>()
    factory { ImportProductsUseCaseImpl(get()) }.bind<ImportProductsUseCase>()
}

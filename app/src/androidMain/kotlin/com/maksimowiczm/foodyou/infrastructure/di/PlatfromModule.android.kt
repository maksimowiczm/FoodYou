package com.maksimowiczm.foodyou.infrastructure.di

import com.maksimowiczm.foodyou.feature.importexport.data.BackupService
import com.maksimowiczm.foodyou.feature.importexport.ui.ImportExportViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

actual val platformModule: Module = module {
    viewModelOf(::ImportExportViewModel)

    factoryOf(::BackupService)
}

package com.maksimowiczm.foodyou.feature.importexport

import com.maksimowiczm.foodyou.feature.importexport.ui.ImportExportViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

actual val importExportModule = module {
    viewModelOf(::ImportExportViewModel)
}

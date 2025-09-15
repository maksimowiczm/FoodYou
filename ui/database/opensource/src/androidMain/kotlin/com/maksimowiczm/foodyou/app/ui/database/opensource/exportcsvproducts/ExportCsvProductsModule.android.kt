package com.maksimowiczm.foodyou.app.ui.database.opensource.exportcsvproducts

import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModelOf

internal actual fun Module.exportCsvProductsModule() {
    viewModelOf(::ExportProductsViewModel)
}

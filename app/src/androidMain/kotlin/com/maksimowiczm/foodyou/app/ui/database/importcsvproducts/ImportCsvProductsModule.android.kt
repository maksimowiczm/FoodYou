package com.maksimowiczm.foodyou.app.ui.database.importcsvproducts

import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModelOf

internal actual fun Module.importCsvProductsModule() {
    viewModelOf(::ImportCsvProductsViewModel)
}

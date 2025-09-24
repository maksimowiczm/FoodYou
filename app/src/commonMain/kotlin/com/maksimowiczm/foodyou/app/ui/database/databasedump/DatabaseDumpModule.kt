package com.maksimowiczm.foodyou.app.ui.database.databasedump

import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModelOf

internal fun Module.databaseDumpModule() {
    viewModelOf(::DatabaseDumpViewModel)
}

package com.maksimowiczm.foodyou.app.ui.database.swissfoodcompositiondatabase

import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModelOf

internal fun Module.swissFoodCompositionDatabaseModule() {
    viewModelOf(::SwissFoodCompositionDatabaseViewModel)
}

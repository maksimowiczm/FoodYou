package com.maksimowiczm.foodyou.infrastructure.di

import com.maksimowiczm.foodyou.feature.swissfoodcompositiondatabase.ui.SwissFoodCompositionDatabaseViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val swissFoodCompositionDatabaseModule = module {
    viewModelOf(::SwissFoodCompositionDatabaseViewModel)
}

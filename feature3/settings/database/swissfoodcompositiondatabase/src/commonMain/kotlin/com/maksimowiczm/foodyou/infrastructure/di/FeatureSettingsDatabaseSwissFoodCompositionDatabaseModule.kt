package com.maksimowiczm.foodyou.infrastructure.di

import com.maksimowiczm.foodyou.feature.settings.database.swissfoodcompositiondatabase.presentation.SwissFoodCompositionDatabaseViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val featureSettingsDatabaseSwissFoodCompositionDatabaseModule = module {
    viewModelOf(::SwissFoodCompositionDatabaseViewModel)
}

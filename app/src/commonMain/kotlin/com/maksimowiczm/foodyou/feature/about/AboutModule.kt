package com.maksimowiczm.foodyou.feature.about

import com.maksimowiczm.foodyou.feature.about.ui.AboutSettingsViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val aboutModule = module {
    viewModelOf(::AboutSettingsViewModel)
}

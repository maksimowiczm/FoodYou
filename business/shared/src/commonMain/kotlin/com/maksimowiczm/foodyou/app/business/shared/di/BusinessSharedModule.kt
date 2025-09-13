package com.maksimowiczm.foodyou.app.business.shared.di

import com.maksimowiczm.foodyou.app.business.shared.domain.settings.settingsModule
import org.koin.dsl.module

val businessSharedModule = module { settingsModule() }

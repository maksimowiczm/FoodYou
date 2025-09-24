package com.maksimowiczm.foodyou.settings

import com.maksimowiczm.foodyou.settings.infrastructure.settingsInfrastructureModule
import org.koin.dsl.module

val settingsModule = module { settingsInfrastructureModule() }

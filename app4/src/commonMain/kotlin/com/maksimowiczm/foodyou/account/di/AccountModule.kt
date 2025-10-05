package com.maksimowiczm.foodyou.account.di

import com.maksimowiczm.foodyou.account.application.UpdateSettingsCommandHandler
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val accountModule = module { factoryOf(::UpdateSettingsCommandHandler) }

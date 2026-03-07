package com.maksimowiczm.foodyou.common.logger.di

import co.touchlab.kermit.Logger
import org.koin.dsl.bind
import org.koin.dsl.module

val loggerModule = module { single { Logger.Companion }.bind<Logger>() }
